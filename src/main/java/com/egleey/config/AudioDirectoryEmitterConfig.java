package com.egleey.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.egleey.service.webradio.AudioDirectoryWatcher;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static com.egleey.config.SocketIOConfig.NAMESPACE_ADMIN;
import static com.egleey.config.SocketIOConfig.SOCKET_SERVER_EVENT_AUDIO_DIRECTORY_CHANGED;

@Configuration
@ComponentScan(basePackages = { "com.egleey.service.webradio" })
public class AudioDirectoryEmitterConfig {
    private SocketIOServer socketIOServer;
    private AudioDirectoryWatcher audioDirectoryWatcher;

    private final static String DESCRIPTOR_EVENT = "event";
    private final static String DESCRIPTOR_PATH = "path";
    private final static String DESCRIPTOR_IS_FILE = "is_file";
    private final static String DESCRIPTOR_SEPARATOR = "separator";


    @Autowired
    public AudioDirectoryEmitterConfig(SocketIOServer socketIOServer, AudioDirectoryWatcher audioDirectoryWatcher) {
        this.socketIOServer = socketIOServer;
        this.audioDirectoryWatcher = audioDirectoryWatcher;

        startPropagation();
    }

    public void startPropagation() {
        JsonObject propagation = new JsonObject();
        propagation.add(DESCRIPTOR_SEPARATOR, new JsonPrimitive(File.separator));
        audioDirectoryWatcher.appendListener((event, child) -> {
            propagation.add(DESCRIPTOR_EVENT, new JsonPrimitive(event.kind().name()));
            propagation.add(DESCRIPTOR_PATH, new JsonPrimitive(child.toString().replace(
                    AudioDirectoryWatcher.AUDIO_DIRECTORY_ROOT, "")));
            propagation.add(DESCRIPTOR_IS_FILE, new JsonPrimitive(child.toFile().isFile()));

            socketIOServer.getNamespace(NAMESPACE_ADMIN).getBroadcastOperations().sendEvent(
                SOCKET_SERVER_EVENT_AUDIO_DIRECTORY_CHANGED,
                propagation.toString()
            );
        });
    }
}
