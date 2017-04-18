package com.egleey.config.socket_events.webradio.components;

import java.util.HashMap;

public class MediaResponse {
    private Integer code;

    private HashMap<String, Object> data;

    public MediaResponse() {
        this.data = new HashMap<>();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}
