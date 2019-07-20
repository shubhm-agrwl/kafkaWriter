package com.shubham.kafkawriter.auth;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Objects;

public class User implements Principal {

    private final BigDecimal id;
    private final String name;

    public User(BigDecimal id, String name) {
        this.id = id;
        this.name = name;
    }

    public BigDecimal getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MyUser{" +
          "id='" + id + '\'' +
          ", name='" + name + '\'' +
          '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User myUser = (User) o;
        return Objects.equals(id, myUser.id) && Objects.equals(name, myUser.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}