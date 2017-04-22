package com.egleey.util.icecast.libshout;

import java.util.*;

public class Playlist {
    private HashMap<String, String> songList;

    public Playlist() {
        songList = new HashMap<>();
    }

    public Playlist addToSongList(String id, String mediaPath) {
        songList.put(id, mediaPath);
        return this;
    }

    public Playlist removeFromSongList(String id) {
        songList.remove(id);
        return this;
    }

    public Playlist mergeSongList(HashMap<String, String> list) {
        songList.putAll(list);
        return this;
    }

    public HashMap<String, String> getSongList() {
        return songList;
    }

    public String get(int i) {
        if (songList.size() > i)
        return songList.values().toArray()[i].toString();
        return null;
    }

    public void shuffle() {
        HashMap<String, String> container = new LinkedHashMap<>();

        final List<String> keys = new ArrayList<>(songList.keySet());
        Collections.shuffle(keys);

        for (String key : keys) container.put(key, songList.get(key));
        songList = container;
    }
}
