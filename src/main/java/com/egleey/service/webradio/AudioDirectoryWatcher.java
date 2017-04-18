package com.egleey.service.webradio;

import com.egleey.util.components.DirectoryWatcher;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class AudioDirectoryWatcher extends DirectoryWatcher {
    private static final boolean AUDIO_DIRECTORY_IS_RECURSIVE = true;
    private static final String AUDIO_DIRECTORY_JSON_MIRROR = "cache/directory_mirror/audio_directory_mirror.json";
    private static final String AUDIO_DIRECTORY_ROOT_DIR = "audio";

    public static final String AUDIO_DIRECTORY_ROOT = String.format("src/main/webapp/resources/%s", AUDIO_DIRECTORY_ROOT_DIR);

    private static final String DESCRIPTOR_TYPE = "type";
    private static final String DESCRIPTOR_CHILDREN = "children";

    private static final String FIELD_TYPE_FILE = "file";
    private static final String FIELD_TYPE_DIRECTORY = "directory";

    private JsonObject jsonMirror;

    /**
     * Creates a WatchService and registers the given directory
     */
    public AudioDirectoryWatcher() throws IOException {
        super(AUDIO_DIRECTORY_ROOT, AUDIO_DIRECTORY_IS_RECURSIVE);

        createMirrorIfAbsent();
        appendListeners();
    }

    private void createMirrorIfAbsent() throws IOException {
        File file = new File(AUDIO_DIRECTORY_JSON_MIRROR);

        if (file.exists()) {
            return;
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.mkdirs()) {
            throw new IOException("Can't create audio cache directory");
        }

        if (!file.createNewFile()) {
            throw new IOException("Can't create audio directory mirror file");
        }
    }

    private void appendListeners() throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(AUDIO_DIRECTORY_JSON_MIRROR));

        jsonMirror = gson.fromJson(reader, JsonObject.class);
        JsonObject[] finalData =  {jsonMirror};

        if (finalData[0] == null) {
            finalData[0] = assembleNew(FIELD_TYPE_DIRECTORY);
        }

        this.appendListener((event, child) -> {
            File file = child.toFile();
            boolean isFile = file.isFile();

            String[] pieces = file.getPath()
                .replace(String.format("%s/", AUDIO_DIRECTORY_ROOT), "")
                .split(File.separator);

            String eventName = event.kind().name();
            JsonObject current = (JsonObject) finalData[0].get(DESCRIPTOR_CHILDREN);
            JsonObject piece;
            String type;

            if (eventName.equals(ENTRY_CREATE.name())) {
                for (int i = 0, n = pieces.length; i < n; i++) {
                    piece = (JsonObject) current.get(pieces[i]);
                    if (piece == null) {
                        type = isFile && i == n - 1 ? FIELD_TYPE_FILE : FIELD_TYPE_DIRECTORY;
                        piece = assembleNew(type);
                        current.add(pieces[i], piece);
                    }
                    current = (JsonObject) piece.get(DESCRIPTOR_CHILDREN);
                }
            }

            if (eventName.equals(ENTRY_DELETE.name())) {
                for (int i = 0, n = pieces.length; i < n; i++) {
                    if (i == n - 1) {
                        current.remove(pieces[i]);
                        break;
                    }
                    piece = (JsonObject) current.get(pieces[i]);
                    if (piece == null) {
                        break;
                    }
                    current = (JsonObject) piece.get(DESCRIPTOR_CHILDREN);
                }
            }

            try {
                FileOutputStream fOut = new FileOutputStream(AUDIO_DIRECTORY_JSON_MIRROR);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(finalData[0].toString());
                myOutWriter.close();
                fOut.close();
            } catch (Exception e) {
                System.out.format("Can't update mirror : %s", AUDIO_DIRECTORY_JSON_MIRROR);
            }
        });
    }

    private JsonObject assembleNew(String type) {
        JsonObject obj = new JsonObject();
        obj.add(DESCRIPTOR_TYPE, new JsonPrimitive(type));
        if (type.equals(FIELD_TYPE_DIRECTORY)) {
            obj.add(DESCRIPTOR_CHILDREN, new JsonObject());
        }
        return obj;
    }

    public JsonObject getJsonMirror() {
        return this.jsonMirror;
    }
}
