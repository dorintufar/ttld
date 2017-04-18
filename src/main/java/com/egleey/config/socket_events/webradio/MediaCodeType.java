package com.egleey.config.socket_events.webradio;

public enum MediaCodeType {
    CODE_STREAM_URL_REQUIRED(1),
    CODE_STREAM_PLAY(2),
    CODE_STREAM_PAUSE(3),
    CODE_STREAM_STOP(4);

    public final static String REQUEST_CODE_METHOD_NAME = "getRequestCode";
    private Integer requestCode;

    MediaCodeType(Integer requestCode) {
        this.requestCode = requestCode;
    }

    public Integer getRequestCode() {
        return requestCode;
    }
}
