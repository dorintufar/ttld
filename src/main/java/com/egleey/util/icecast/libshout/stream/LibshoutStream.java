package com.egleey.util.icecast.libshout.stream;

import com.gmail.kunicins.olegs.libshout.Libshout;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.HashMap;

public abstract class LibshoutStream {

    public enum Parameter {
        HOST,
        PORT,
        PROTOCOL,
        PASSWORD,
        MOUNT,
        NAME,
        STAND_BY
    }

    private static Logger log = Logger.getLogger(LibshoutStream.class.getName());
    private Libshout libshout;
    private Configurator configurator;
    private HashMap<Parameter, String> params;
    private Thread dataCarry;

    private BufferedInputStream currentMediaStream;
    private boolean reconnecting;
    private boolean alive;
    private int frame;

    public LibshoutStream(HashMap<Parameter, String> parameters) {
        try {
            configurator = new Configurator();
            libshout = new Libshout();
            params = parameters;
            alive = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        configure(configurator);
        configureLibshout(libshout);
        onShutdown();
    }

    private void onShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dataCarry.interrupt();
            alive = false;
        }));
    }

    public int getFrame() {
        return frame;
    }

    public String getAddress() {
        return String.format("http://%s:%s", getParam(Parameter.HOST), getParam(Parameter.PORT));
    }

    public void startBroadcasting() {
        stopBroadcasting();
        dataCarry = new Thread(() -> {
            byte[] buffer = new byte[1024];
            String silence = "";

            startReconnect();
            if (!libshout.isConnected()) {
                log.info("Can't connect to icecast stream server");
                return;
            }
            log.info("Connected to icecast server");

            while (!Thread.currentThread().isInterrupted()) {
                if (configurator.isUnbreakable()) {
                    silence = configurator.getStandBySound();
                    if (silence == null)
                        break;
                }

                try {
                    currentMediaStream = new BufferedInputStream(new FileInputStream(new File(silence)));
                    int read = currentMediaStream.read(buffer);
                    frame += read;

                    while (read > 0 && alive) {
                        libshout.send(buffer, read);
                        read = currentMediaStream.read(buffer);
                        frame += read;
                    }
                    currentMediaStream.close();
                    frame = 0;
                } catch (IOException e) {
                    stopBroadcasting();
                }
            }
        });

        dataCarry.start();
    }

    protected void stopBroadcasting() {
        if (dataCarry != null) {
            try {
                dataCarry.interrupt();
                dataCarry = null;
                currentMediaStream.close();
                currentMediaStream = null;
                if (configurator.isUnbreakable()) {
                    startBroadcasting();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setCurrentMediaStream(BufferedInputStream currentMediaStream) {
        try {
            if (this.currentMediaStream != null) {
                this.currentMediaStream.close();
            }
            this.currentMediaStream = currentMediaStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getParam(@NotNull Parameter key) {
        return params.get(key);
    }

    protected abstract void configure(Configurator configurator);

    protected abstract void configureLibshout(Libshout libshout);

    private void startReconnect() {
        if (libshout.isConnected()) {
            return;
        }

        int reconnect = configurator.getReconnectAttempts();
        reconnecting = true;

        while (reconnecting) {
            log.info("Trying to connect to icecast server");
            if (!libshout.isConnected()) {
                try {
                    libshout.open();
                    reconnecting = false;
                    break;
                } catch (IOException ignored) {
                }
            }
            if (reconnect == 0) {
                stopReconnect();
            }
            if (reconnect != -1) {
                reconnect--;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopReconnect() {
        reconnecting = false;
    }
}
