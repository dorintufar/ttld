package com.egleey.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.egleey.config.socket_events.AudioDirectoryDispatcher;
import com.egleey.config.socket_events.webradio.MediaDispatcher;
import com.egleey.config.socket_events.webradio.WebRadioCurrentState;
import com.egleey.config.socket_events.webradio.components.MediaRequest;
import com.egleey.service.webradio.AudioDirectoryWatcher;
import com.egleey.service.webradio.MediaStreamVLCPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static com.egleey.config.Parameters.CURRENT_HOST_ADDRESS;
import static com.egleey.config.Parameters.SOCKET_IO_SERVER_PORT;
import static com.egleey.service.webradio.AudioDirectoryWatcher.AUDIO_DIRECTORY_ROOT;

@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = { "com.egleey" })
public class SocketIOConfig {
    public static final String SOCKET_SERVER_EVENT_AUDIO_DIRECTORY_CHANGED = "audio_directory_changed";
    public static final String SOCKET_SERVER_EVENT_AUDIO_DIRECTORY_ROOT_MIRROR = "audio_directory_root_mirror";
    public static final String SOCKET_SERVER_EVENT_AUDIO_STREAM_DISPATCH = "audio_stream_dispatch";
    public static final String SOCKET_SERVER_EVENT_WEB_RADIO_STATE = "web_radio_state";

    public static final String NAMESPACE_ADMIN = "/admin";
    public static final String NAMESPACE_USER = "/user";

    private AudioDirectoryWatcher audioDirectoryWatcher;
    private MediaStreamVLCPlayer mediaStreamVLCPlayer;

    @Autowired
    public SocketIOConfig(AudioDirectoryWatcher audioDirectoryWatcher, MediaStreamVLCPlayer mediaStreamVLCPlayer) {
        this.audioDirectoryWatcher = audioDirectoryWatcher;
        this.mediaStreamVLCPlayer = mediaStreamVLCPlayer;
    }

    @Bean
    public SocketIOServer socketIoServer() {
        Configuration config = new Configuration();
        config.setHostname(CURRENT_HOST_ADDRESS);
        config.setPort(SOCKET_IO_SERVER_PORT);
        SocketIOServer server = new SocketIOServer(config);
        server.addNamespace(NAMESPACE_ADMIN);
        server.addNamespace(NAMESPACE_USER);
        appendEvents(server);
        appendShutdownHook(server);
        server.start();
        return server;
    }

    private void appendShutdownHook(SocketIOServer server) {
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    private void appendEvents(SocketIOServer server) {
        SocketIONamespace adminNamespace = server.getNamespace(NAMESPACE_ADMIN);

        adminNamespace.addEventListener(SOCKET_SERVER_EVENT_AUDIO_DIRECTORY_ROOT_MIRROR,
                Object.class, new AudioDirectoryDispatcher(audioDirectoryWatcher));

        adminNamespace.addEventListener(SOCKET_SERVER_EVENT_AUDIO_STREAM_DISPATCH,
                MediaRequest.class, new MediaDispatcher(mediaStreamVLCPlayer, AUDIO_DIRECTORY_ROOT, server));

        adminNamespace.addEventListener(SOCKET_SERVER_EVENT_WEB_RADIO_STATE,
                Object.class, new WebRadioCurrentState(mediaStreamVLCPlayer));
    }
}
