package com.egleey.service.webradio;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

public class MediaStreamVLCPlayer extends MediaPlayerEventAdapter {
    private String address;
    private Integer port;
    private HeadlessMediaPlayer mediaPlayer;

    private String currentPlayingId;
    private String currentPlaying;
    private long currentPlayingTime;
    private int currentVolume;

    private JsonObject currentState;
    private static final String MUTE_TRACK = "src/main/resources/audio/Jazzy Elevator Music.mp3";

    private static final String DESCRIPTOR_VOLUME = "volume";
    private static final String DESCRIPTOR_ID = "id";

    public MediaStreamVLCPlayer(String serverAddress, int serverPort) {
        String opts = formatHttpStream();
        this.currentPlaying = "";
        this.currentPlayingId = "";
        this.currentPlayingTime = 0;
        this.currentState = new JsonObject();
        this.address = serverAddress;
        this.port = serverPort;

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(
                String.format("--http-host=%s", serverAddress),
                String.format("--http-port=%d", serverPort));
        this.mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
        this.mediaPlayer.setStandardMediaOptions(opts);
        this.mediaPlayer.addMediaPlayerEventListener(this);

        mute();
    }

    public void play(String media, String id) {
        if (id == null) {
            return;
        }

        if (!media.equals(currentPlaying)) {
            currentPlaying = media;
            currentPlayingTime = 0;
        }
        mediaPlayer.setRepeat(false);
        currentPlayingId = id;
        prepareAndPlay(media);
        mediaPlayer.setTime(currentPlayingTime);

        System.out.println(currentPlaying);
    }

    public void play() {
        mediaPlayer.setVolume(currentVolume);
        mediaPlayer.playMedia(currentPlaying);
        mediaPlayer.setTime(currentPlayingTime);
    }

    public void stop() {
        currentPlayingTime = 0;
        currentPlayingId = "";
        currentPlaying = "";
        mute();
    }

    public void setVolume(int volume) {
        currentVolume = volume;
        mediaPlayer.setVolume(currentVolume);
    }

    public void pause() {
        currentPlayingTime = mediaPlayer.isPlaying() ? mediaPlayer.getTime() : 0;
        mute();
    }

    public JsonObject getCurrentState() {
        currentState.add(DESCRIPTOR_VOLUME, new JsonPrimitive(mediaPlayer.getVolume()));
        currentState.add(DESCRIPTOR_ID, new JsonPrimitive(currentPlayingId));


        return currentState;
    }

    public String getAddress() {
        return String.format("http://%s:%s", address, port);
    }

    public HeadlessMediaPlayer getVLCPlayer() {
        return this.mediaPlayer;
    }

    private static String formatHttpStream() {
        return ":sout=#transcode{acodec=vorb,ab=128,channels=2,samplerate=44100}:http{mime=audio/ogg,mux=ogg}";
    }

    private void mute() {
        mediaPlayer.setRepeat(true);
        prepareAndPlay(MUTE_TRACK);
        currentPlayingTime = 0;
        currentVolume = mediaPlayer.getVolume();
        currentPlayingId = "";
        currentPlaying = "";
        mediaPlayer.setVolume(0);

        System.out.println("muted");
    }

    private void prepareAndPlay(String media) {
        mediaPlayer.prepareMedia(media);
        mediaPlayer.playMedia(media);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        pause();
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        mute();
    }
}
