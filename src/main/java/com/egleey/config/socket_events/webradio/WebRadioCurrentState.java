package com.egleey.config.socket_events.webradio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.egleey.service.webradio.LibshoutStreamPlayer;

import java.util.ArrayList;

import static com.egleey.config.SocketIOConfig.SOCKET_SERVER_EVENT_WEB_RADIO_STATE;

public class WebRadioCurrentState implements DataListener<Object> {
    private ArrayList<LibshoutStreamPlayer> libshoutStreamPlayers;
    private LibshoutStreamPlayer libshoutCurrentPlayer;

    public WebRadioCurrentState(ArrayList<LibshoutStreamPlayer> libshoutStreamPlayers) {
        this.libshoutStreamPlayers = libshoutStreamPlayers;
        this.libshoutCurrentPlayer = libshoutStreamPlayers.get(0);
    }

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
        client.sendEvent(SOCKET_SERVER_EVENT_WEB_RADIO_STATE, libshoutCurrentPlayer.getCurrentState().toString());
    }
}
