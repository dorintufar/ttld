package com.egleey.config.socket_events.webradio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.egleey.service.webradio.MediaStreamVLCPlayer;

import static com.egleey.config.SocketIOConfig.SOCKET_SERVER_EVENT_WEB_RADIO_STATE;

public class WebRadioCurrentState implements DataListener<Object> {
    private MediaStreamVLCPlayer mediaStreamVLCPlayer;

    public WebRadioCurrentState(MediaStreamVLCPlayer mediaStreamVLCPlayer) {
        this.mediaStreamVLCPlayer = mediaStreamVLCPlayer;
    }

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
        client.sendEvent(SOCKET_SERVER_EVENT_WEB_RADIO_STATE, mediaStreamVLCPlayer.getCurrentState().toString());
    }
}
