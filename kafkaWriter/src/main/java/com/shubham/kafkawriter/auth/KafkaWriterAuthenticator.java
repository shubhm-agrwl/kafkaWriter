package com.shubham.kafkawriter.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import com.shubham.kafkawriter.configuration.AuthServerConfig;
import com.shubham.kafkawriter.utils.JsonUtils;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaWriterAuthenticator implements Authenticator<BasicCredentials, User> {

  AuthServerConfig config;
  private Map<String, AuthorizeInfo> authMap;
  private String url;

  public KafkaWriterAuthenticator(AuthServerConfig config) {

    this.config = config;
    authMap = new ConcurrentHashMap<String, AuthorizeInfo>();
    url = String.format(config.getAuthServerURL(), config.getAuthServerHost(),
        config.getAuthServerPort());
    UserDetailsValidatorThread userDetailsValidatorThread = new UserDetailsValidatorThread();
    userDetailsValidatorThread.start();

  }

  public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
    if (false == config.getAuthEnabled()) {
      log.debug("Authentication is Disabled");
      return Optional.of(new User(BigDecimal.ONE, "success"));
    }

    final String apiKey = credentials.getUsername();
    final String apiSecret = credentials.getPassword();

    if (authMap.containsKey(apiKey)) {
      AuthorizeInfo authInfo = authMap.get(apiKey);
      if (apiSecret.equals(authInfo.getApiSecret())) {
        log.debug("Authorized the User {}", apiKey);
        return Optional.of(new User(BigDecimal.ONE, apiKey));
      }

    } else if (callAuthServer(apiKey, apiSecret)) {
      return Optional.of(new User(BigDecimal.ONE, apiKey));
    }
    return Optional.empty();
  }

  public boolean callAuthServer(String apiKey, String apiSecret) {

    AuthCredentials authCredentials = new AuthCredentials();
    AuthorizeInfo authInfo = new AuthorizeInfo();
    long expiryTime = 0;

    authCredentials.setUserName(apiKey);
    authCredentials.setPassword(apiSecret);

    HttpResponse response = post(url, JsonUtils.toJson(authCredentials));

    if (response.getStatusLine().getStatusCode() == 200) {

      BufferedReader br;
      try {
        br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        String output;
        while ((output = br.readLine()) != null) {
          expiryTime = JsonUtils.fromJson(output, AuthResponse.class).getExpiryTime();
        }
      } catch (Exception e) {
        log.error("Error Occured while processing the Response from Auth Server" + e);
        return false;
      }
      authInfo.setApiSecret(apiSecret);
      authInfo.setStartTime(System.currentTimeMillis());
      authInfo.setExpiryTime(expiryTime);

      authMap.put(apiKey, authInfo);
      return true;

    } else {
      authMap.remove(apiKey);
    }
    return false;

  }

  // call Auth Server API
  public HttpResponse post(String completeUrl, String body) {
    HttpResponse response = null;
    HttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(completeUrl);
    httpPost.setHeader("Content-type", "application/json");

    StringEntity stringEntity;
    try {
      String str = config.getAuthUserName() + ":" + config.getAuthPassword();
      Encoder encoder = Base64.getEncoder();
      String encodedString = encoder.encodeToString(str.getBytes());
      String auth = "Basic " + encodedString;
      httpPost.setHeader("Content-type", "application/json");
      httpPost.setHeader("Authorization", auth);

      stringEntity = new StringEntity(body);
      httpPost.getRequestLine();
      httpPost.setEntity(stringEntity);
      response = httpClient.execute(httpPost);
    } catch (IOException e) {
      log.error("Error Occured while call Auth Server " + e.getMessage());
    }
    return response;

  }

  // This thread will run once the data is expired.
  private class UserDetailsValidatorThread extends Thread {

    @Override
    public void run() {

      while (true) {
        if (!authMap.isEmpty()) {
          Long currentTime = System.currentTimeMillis();
          for (Map.Entry<String, AuthorizeInfo> authEntry : authMap.entrySet()) {
            if (currentTime > authEntry.getValue().getExpiryTime()) {
              callAuthServer(authEntry.getKey(), authEntry.getValue().getApiSecret());
            }
          }
        }
        try {
          Thread.sleep(1000 * config.getExpiryInterval());
        } catch (InterruptedException e) {
          log.error("Unable to call Auth Server: " + e);
        }
      }
    }
  }

}
