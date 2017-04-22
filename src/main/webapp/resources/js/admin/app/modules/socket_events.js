"use strict";

define(["modules/audio_dispatcher/codes", "os_meta"], function(codes, os_meta) {
    return {
        events : {
            AUDIO_DIRECTORY_CHANGED : "audio_directory_changed",
            AUDIO_DIRECTORY_ROOT_MIRROR: "audio_directory_root_mirror",
            AUDIO_STREAM_DISPATCH: "audio_stream_dispatch",
            WEB_RADIO_STATE: "web_radio_state",
            OS_META: "os_meta"
        },

        register : function (socket, filesystem, player) {
            var $this = this;

            socket.on(this.events.AUDIO_DIRECTORY_CHANGED, function(data) {
                data = $this.decode(data);
                filesystem.update(data.event, data.path, data.is_file, data.separator);
            });

            socket.on(this.events.AUDIO_DIRECTORY_ROOT_MIRROR, function (map) {
                map = $this.decode(map);
                filesystem.set_map(map);
            });

            socket.on(this.events.AUDIO_STREAM_DISPATCH, function (response) {
                if (response.code === codes.response.RESPONSE_CODE_SUCCESS) {
                    if (typeof response.data !== "undefined")
                    if (typeof response.data.address !== "undefined") {
                        player.set_streaming_address(response.data.address);
                        player.start_streaming();
                    }
                }
            });

            socket.on(this.events.WEB_RADIO_STATE, function (response) {
                response = $this.decode(response);
                player.set_web_radio_state(response);
            });

            socket.on(this.events.OS_META, function (response) {
                response = $this.decode(response);
                os_meta.file_separator = response.file_separator;
            })
        },

        decode(response) {
            if (typeof response === "string") {
                response = JSON.parse(response);
            }
            return response;
        }
    }
});