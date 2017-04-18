package com.egleey.config.socket_events.webradio.components;

import com.egleey.config.socket_events.webradio.MediaCodeType;
import com.egleey.util.ParseEnum;

public class MediaRequest {
    private String id;

    private MediaCodeType code;

    private Integer time;

    private String media;

    public Integer getCode() {
        return code.getRequestCode();
    }

    public void setCode(Integer code) {
        this.code = ParseEnum.parse(MediaCodeType.class, code, MediaCodeType.REQUEST_CODE_METHOD_NAME);
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getId() {
        return id;
    }
}
