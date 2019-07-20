package com.shubham.kafkawriter.resource;

import java.util.concurrent.ExecutionException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.shubham.kafkawriter.auth.User;
import com.shubham.kafkawriter.kafka.KafkaMessageHandler;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/kafkawriter/v1")
public class KafkaWriterResource {

  @POST
  @Path("/sendData")
  @Produces(MediaType.APPLICATION_JSON)
  public Response sendMessage(String message, @Auth User principal)
      throws InterruptedException, ExecutionException {
    KafkaMessageHandler.handler(message);
    log.info("Message Processed successfully.");
    return Response.status(Status.OK).entity("Success").build();
  }

}
