package com.example.davidwillo.youwo.network;

import java.io.Serializable;

/**
 * Created by davidwillo on 6/1/17.
 */

public class HttpResult<T> implements Serializable{
    private Integer resultCode;
    private String resultMessage;
    private T data;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
