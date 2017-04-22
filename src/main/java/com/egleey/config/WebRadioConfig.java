package com.egleey.config;

import com.egleey.service.webradio.LibshoutStreamPlayer;
import com.egleey.util.icecast.IcecastServer;
import com.egleey.util.icecast.libshout.stream.LibshoutStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;

@Configuration
public class WebRadioConfig {
    @Value("${icecast.server.path.config}")
    public String ICECAST_PATH_CONFIG;
    @Value("${icecast.server.path.native}")
    public String ICECAST_PATH_NATIVE;

    @Value("${icecast.streams.host}")
    public String streamHost;
    @Value("${icecast.streams.port}")
    public String streamPort;
    @Value("${icecast.streams.protocol}")
    public String streamProtocol;
    @Value("${icecast.streams.password}")
    public String streamPassword;
    @Value("${icecast.streams.mount}")
    public String streamMount;
    @Value("${icecast.streams.name}")
    public String streamName;
    @Value("${icecast.streams.standby}")
    public String standBy;

    private ArrayList<LibshoutStreamPlayer> libshoutPlayers = new ArrayList<>();

    public WebRadioConfig() {
        libshoutPlayers = new ArrayList<>();
    }

    @Bean
    public IcecastServer icecastServer() {
        IcecastServer server = new IcecastServer(ICECAST_PATH_NATIVE, ICECAST_PATH_CONFIG);
        server.run();
        return server;
    }

    @Bean
    public ArrayList<LibshoutStreamPlayer> libshoutPlayers() {
        // TODO: add multiple processing
        LibshoutStreamPlayer player;

        for (int i = 0; i < 1; i++) {
            HashMap<LibshoutStream.Parameter, String> parameters = new HashMap<>();

            parameters.put(LibshoutStream.Parameter.HOST, streamHost);
            parameters.put(LibshoutStream.Parameter.PORT, streamPort);
            parameters.put(LibshoutStream.Parameter.PROTOCOL, streamProtocol);
            parameters.put(LibshoutStream.Parameter.PASSWORD, streamPassword);
            parameters.put(LibshoutStream.Parameter.MOUNT, streamMount);
            parameters.put(LibshoutStream.Parameter.NAME, streamName);
            parameters.put(LibshoutStream.Parameter.STAND_BY, standBy);

            player = new LibshoutStreamPlayer(parameters);
            player.startBroadcasting();
            libshoutPlayers.add(player);
        }

        return libshoutPlayers;
    }
}
