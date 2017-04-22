package com.egleey.util.icecast;

import javax.validation.constraints.NotNull;
import java.io.IOException;

public class IcecastServer {
    private String pathConfig;
    private String pathLib;
    private Process icecastServerProcess;

    private static final String PROCESS_COMMAND_TEMPLATE = "%s -d -c %s";

    public IcecastServer(@NotNull String pathLib, @NotNull String pathConfig) {
        this.pathConfig = pathConfig;
        this.pathLib = pathLib;
        this.onShutdown();
    }

    public void run() {
        try {
            icecastServerProcess = Runtime.getRuntime().exec(String.format(
                    PROCESS_COMMAND_TEMPLATE, pathLib, pathConfig));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        if (icecastServerProcess != null) {
            icecastServerProcess.destroy();
        }
    }

    private void onShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }
}
