package com.example.davidwillo.youwo.life.express;

public class Content {
    private String time;
    private String context;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "Content{" +
                "time='" + time + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
