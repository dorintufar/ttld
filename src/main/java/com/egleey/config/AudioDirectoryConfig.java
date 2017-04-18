package com.egleey.config;

import com.egleey.service.webradio.AudioDirectoryWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AudioDirectoryConfig {

    @Bean
    public AudioDirectoryWatcher audioDirectoryWatcher() {
        try {
            AudioDirectoryWatcher adw = new AudioDirectoryWatcher();
            adw.start();
            return adw;
        } catch (IOException e) {
            throw new RuntimeException("Can't setup audio directory watcher");
        }
    }
}
