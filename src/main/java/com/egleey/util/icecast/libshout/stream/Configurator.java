package com.egleey.util.icecast.libshout.stream;

import org.springframework.beans.factory.annotation.Value;

public class Configurator {
    /**
     * sound to stream on pause or stop
     */
    @Value("${libshout.stream.standby.sound}")
    private String standBySound;

    /**
     * stream silent sound when playlist pause or stop
     * was triggered
     */
    private boolean unbreakable;

    /**
     * reconnect if first attempt was failed
     */
    private boolean reconnect;

    /**
     * reconnect attempts. -1 for infinite reconnect attempts
     */
    private int reconnectAttempts;

    public Configurator() {
        reconnectAttempts = -1;
        unbreakable = false;
        reconnect = true;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public Configurator setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public Configurator setStandBySound(String standBySound) {
        this.standBySound = standBySound;
        return this;
    }

    public String getStandBySound() {
        return standBySound;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public Configurator setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
        return this;
    }

    public int getReconnectAttempts() {
        return reconnectAttempts;
    }

    public Configurator setReconnectAttempts(int reconnectAttempts) {
        this.reconnectAttempts = reconnectAttempts;
        return this;
    }
}
