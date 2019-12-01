package com.example.rabbitmqconsumer.model;

import java.io.Serializable;
import java.util.Date;

public class Spittle implements Serializable {
    private int id;

    private String message;

    public Spittle(){

    }

    public Spittle(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Spittle{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
