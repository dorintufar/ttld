package com.egleey.service.webradio;

import com.egleey.util.components.DirectoryWatcher;
import com.egleey.util.components.Filewalker;
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
        initJsonMirror();
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

    private void initJsonMirror() {
        jsonMirror = assembleNew(FIELD_TYPE_DIRECTORY);

        JsonObject children = (JsonObject) jsonMirror.get(DESCRIPTOR_CHILDREN);
        String absoluteAudioDirectoryRoot = System.getProperty("user.dir") + File.separator + AUDIO_DIRECTORY_ROOT + File.separator;

        Filewalker.getInstance().walk(AUDIO_DIRECTORY_ROOT, (f) -> {
            String path = f.getAbsolutePath().replace(absoluteAudioDirectoryRoot, "");
            String[] pieces = path.split(File.separator);
            JsonObject ref = children;

            for (int i = 0, n = pieces.length; i < n; i++) {
                if (i == n - 1) {
                    ref.add(pieces[i], assembleNew(FIELD_TYPE_FILE));
                } else if (ref.get(pieces[i]) == null) {
                    ref.add(pieces[i], assembleNew(FIELD_TYPE_DIRECTORY));
                    ref = (JsonObject) ref.get(pieces[i]);
                    ref = (JsonObject) ref.get(DESCRIPTOR_CHILDREN);
                }
            }
        });

        save(jsonMirror);
    }

    private void appendListeners() throws FileNotFoundException {
        JsonObject[] finalData =  {jsonMirror};

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

            save(finalData[0]);
        });
    }

    private void save(JsonObject object) {
        try {
            FileOutputStream fOut = new FileOutputStream(AUDIO_DIRECTORY_JSON_MIRROR);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(object.toString());
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            System.out.format("Can't update mirror : %s", AUDIO_DIRECTORY_JSON_MIRROR);
        }
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
