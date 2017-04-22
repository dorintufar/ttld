package com.egleey.config.socket_events.webradio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.egleey.config.SocketIOConfig;
import com.egleey.config.socket_events.webradio.components.MediaRequest;
import com.egleey.config.socket_events.webradio.components.MediaResponse;
import com.egleey.service.webradio.LibshoutStreamPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.egleey.config.SocketIOConfig.SOCKET_SERVER_EVENT_AUDIO_STREAM_DISPATCH;

public class MediaDispatcher implements DataListener<MediaRequest> {
    private static final Integer CODE_STREAM_URL_REQUIRED = 1;
    private static final Integer CODE_STREAM_PLAY = 2;
    private static final Integer CODE_STREAM_PAUSE = 3;
    private static final Integer CODE_STREAM_STOP = 4;

    private static final Integer RESPONSE_CODE_SUCCESS = 5;
    private static final Integer RESPONSE_CODE_FAILURE = 6;

    private static final String DESCRIPTOR_ADDRESS = "address";
    private static final String DESCRIPTOR_RESPONSE_TO = "response_to";

    private SocketIONamespace adminNamespace;
    private ArrayList<LibshoutStreamPlayer> libshoutPlayers;
    private LibshoutStreamPlayer defaultPlayer;
    private String mediaRoot;


    public MediaDispatcher(ArrayList<LibshoutStreamPlayer> libshoutPlayers, String mediaRoot, SocketIOServer server) {
        this.adminNamespace = server.getNamespace(SocketIOConfig.NAMESPACE_ADMIN);
        this.libshoutPlayers = libshoutPlayers;
        this.defaultPlayer = libshoutPlayers.get(0);
        this.mediaRoot = mediaRoot;
    }

    @Override
    public void onData(SocketIOClient client, MediaRequest request, AckRequest ackSender) throws Exception {
        MediaResponse response = new MediaResponse();

        if (request == null) {
            response.setCode(RESPONSE_CODE_FAILURE);
            client.sendEvent(SOCKET_SERVER_EVENT_AUDIO_STREAM_DISPATCH);
            return;
        }

        Integer code = request.getCode();
        String media = request.getMedia();

        response.setCode(RESPONSE_CODE_SUCCESS);

        if (Objects.equals(code, CODE_STREAM_URL_REQUIRED)) {
            response.getData().put(DESCRIPTOR_ADDRESS, defaultPlayer.getAddress());
        }

        if (Objects.equals(code, CODE_STREAM_PLAY)) {
            defaultPlayer.play(request.getId(), String.format("%s%s%s", this.mediaRoot, File.separator, media));
        }

        if (Objects.equals(code, CODE_STREAM_PAUSE) || Objects.equals(code, CODE_STREAM_STOP)) {
            defaultPlayer.pause();
        }

        response.getData().put(DESCRIPTOR_RESPONSE_TO, code);

        if (adminNamespace != null) {
            adminNamespace.getBroadcastOperations().sendEvent(SOCKET_SERVER_EVENT_AUDIO_STREAM_DISPATCH, response);
            adminNamespace.getBroadcastOperations().sendEvent(SocketIOConfig.SOCKET_SERVER_EVENT_WEB_RADIO_STATE,
                    defaultPlayer.getCurrentState().toString());
        }
    }
}
