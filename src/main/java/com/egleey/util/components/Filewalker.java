package com.egleey.util.components;

import java.io.File;

public class Filewalker {

    private static Filewalker instance;

    /**
     * Singletone
     */
    private Filewalker() {}

    public void walk(String path, WalkCallback callback) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), callback);
            } else {
                callback.call(f);
            }
        }
    }

    public static Filewalker getInstance() {
        if (instance == null) {
            instance = new Filewalker();
        }

        return instance;
    }

    @FunctionalInterface
    public interface WalkCallback {
        void call(File f);
    }
}
