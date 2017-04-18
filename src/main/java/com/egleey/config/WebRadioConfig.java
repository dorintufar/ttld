package com.egleey.config;

import com.egleey.service.webradio.MediaStreamVLCPlayer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import static com.egleey.config.Parameters.CURRENT_HOST_ADDRESS;
import static com.egleey.config.Parameters.WEBRADIO_PORT;

@Configuration
public class WebRadioConfig {
    public WebRadioConfig() {
    }

    @Bean
    public MediaStreamVLCPlayer mediaStreamVLCPlayer() {
        new NativeDiscovery().discover();
        return new MediaStreamVLCPlayer(CURRENT_HOST_ADDRESS, WEBRADIO_PORT);
    }
}
