"use strict";

define(["modules/audio_dispatcher/codes"], function(codes) {
    return {
        register : function (socket, filesystem, player) {
            socket.on("audio_directory_changed", function(data) {
                if (typeof data === "string") {
                    data = JSON.parse(data);
                }

                filesystem.update(data.event, data.path, data.is_file, data.separator);
            });

            socket.on("audio_directory_root_mirror", function (map) {
                if (typeof map === "string") {
                    map = JSON.parse(map);
                }
                filesystem.set_map(map);
            });

            socket.on("audio_stream_dispatch", function (response) {
                if (response.code === codes.response.RESPONSE_CODE_SUCCESS) {
                    if (typeof response.data !== "undefined")
                    if (typeof response.data.address !== "undefined") {
                        player.set_streaming_address(response.data.address);
                        player.start_streaming();
                    }
                }
            });

            socket.on("web_radio_state", function (response) {
                if (typeof response === "string") {
                    response = JSON.parse(response);
                }
                console.log(response);
                player.set_web_radio_state(response);
            })
        }
    }
});