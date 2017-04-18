package com.egleey.config.socket_events;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.egleey.service.webradio.AudioDirectoryWatcher;

import static com.egleey.config.SocketIOConfig.SOCKET_SERVER_EVENT_AUDIO_DIRECTORY_ROOT_MIRROR;

public class AudioDirectoryDispatcher implements DataListener<Object> {
    private AudioDirectoryWatcher audioDirectoryWatcher;

    public AudioDirectoryDispatcher(AudioDirectoryWatcher audioDirectoryWatcher) {
        this.audioDirectoryWatcher = audioDirectoryWatcher;
    }

    @Override
    public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
        client.sendEvent(SOCKET_SERVER_EVENT_AUDIO_DIRECTORY_ROOT_MIRROR,
                audioDirectoryWatcher.getJsonMirror().toString());
    }
}
