package com.egleey.config.socket_events.webradio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.egleey.config.SocketIOConfig;
import com.egleey.config.socket_events.webradio.components.MediaRequest;
import com.egleey.config.socket_events.webradio.components.MediaResponse;
import com.egleey.service.webradio.MediaStreamVLCPlayer;

import java.io.File;
import java.util.Objects;

import static com.egleey.config.SocketIOConfig.SOCKET_SERVER_EVENT_AUDIO_STREAM_DISPATCH;

public class MediaDispatcher implements DataListener<MediaRequest> {
    private static final Integer CODE_STREAM_URL_REQUIRED = 1;
    private static final Integer CODE_STREAM_PLAY = 2;
    private static final Integer CODE_STREAM_PAUSE = 3;
    private static final Integer CODE_STREAM_STOP = 4;
    private static final Integer CODE_STREAM_VOLUME_CHANGE = 5;

    private static final Integer RESPONSE_CODE_SUCCESS = 5;
    private static final Integer RESPONSE_CODE_FAILURE = 6;

    private static final String DESCRIPTOR_ADDRESS = "address";
    private static final String DESCRIPTOR_RESPONSE_TO = "response_to";

    private SocketIONamespace adminNamespace;
    private MediaStreamVLCPlayer mediaStreamVLCPlayer;
    private String mediaRoot;


    public MediaDispatcher(MediaStreamVLCPlayer mediaStreamVLCPlayer, String mediaRoot, SocketIOServer server) {
        this.adminNamespace = server.getNamespace(SocketIOConfig.NAMESPACE_ADMIN);
        this.mediaStreamVLCPlayer = mediaStreamVLCPlayer;
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
        Integer time = request.getTime();
        String media = request.getMedia();

        response.setCode(RESPONSE_CODE_SUCCESS);

        if (Objects.equals(code, CODE_STREAM_URL_REQUIRED)) {
            response.getData().put(DESCRIPTOR_ADDRESS, mediaStreamVLCPlayer.getAddress());
        }

        if (Objects.equals(code, CODE_STREAM_PLAY)) {
            mediaStreamVLCPlayer.play(String.format("%s%s%s", this.mediaRoot, File.separator, media), request.getId());
        }

        if (Objects.equals(code, CODE_STREAM_PAUSE) || Objects.equals(code, CODE_STREAM_STOP)) {
            mediaStreamVLCPlayer.pause();
        }

        if (Objects.equals(code, CODE_STREAM_VOLUME_CHANGE)) {
            mediaStreamVLCPlayer.setVolume(time);
        }

        response.getData().put(DESCRIPTOR_RESPONSE_TO, code);

        if (adminNamespace != null) {
            adminNamespace.getBroadcastOperations().sendEvent(SOCKET_SERVER_EVENT_AUDIO_STREAM_DISPATCH, response);
            adminNamespace.getBroadcastOperations().sendEvent(SocketIOConfig.SOCKET_SERVER_EVENT_WEB_RADIO_STATE,
                    mediaStreamVLCPlayer.getCurrentState().toString());
        }
    }
}
