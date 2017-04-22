package com.egleey.service.webradio;

import com.egleey.util.icecast.libshout.stream.Configurator;
import com.egleey.util.icecast.libshout.stream.LibshoutStream;
import com.gmail.kunicins.olegs.libshout.Libshout;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class LibshoutStreamPlayer extends LibshoutStream {

    private static final String DESCRIPTOR_ID = "id";
    private JsonObject currentState;

    private int currentFrame;
    private String currentPlayingId;

    public LibshoutStreamPlayer(HashMap<Parameter, String> parameters) {
        super(parameters);
        currentState = new JsonObject();
        currentFrame = 0;
    }

    @Override
    protected void configure(Configurator configurator) {
        configurator
            .setUnbreakable(true)
            .setStandBySound(getParam(Parameter.STAND_BY))
            .setReconnect(true)
            .setReconnectAttempts(5);
    }

    @Override
    protected void configureLibshout(Libshout libshout) {
        try {
            libshout.setHost(getParam(Parameter.HOST));
            libshout.setPort(Integer.parseInt(getParam(Parameter.PORT)));
            libshout.setProtocol(Integer.parseInt(getParam(Parameter.PROTOCOL)));
            libshout.setPassword(getParam(Parameter.PASSWORD));
            libshout.setMount(getParam(Parameter.MOUNT));
            libshout.setName(getParam(Parameter.NAME));
            libshout.setFormat(Libshout.FORMAT_MP3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject getCurrentState() {
        return currentState;
    }

    public void play(String id, String media) {
        try {
            if (!Objects.equals(id, currentPlayingId)) {
                currentFrame = 0;
            }

            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(new File(media)));
            stream.skip(currentFrame);
            setCurrentMediaStream(stream);

            currentPlayingId = id;
            currentState.add(DESCRIPTOR_ID, new JsonPrimitive(currentPlayingId));
        } catch (IOException ignored) {
        }
    }

    public void stop() {
        currentState.add(DESCRIPTOR_ID, null);
        currentFrame = 0;
        stopBroadcasting();
    }

    public void pause() {
        currentState.add(DESCRIPTOR_ID, null);
        currentFrame = getFrame();
        stopBroadcasting();
    }
}
