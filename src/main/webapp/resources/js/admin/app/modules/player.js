"use strict";

define(["jquery", "parameters", "modules/audio_dispatcher/codes"], function($, parameters, codes) {
    const common_player = document.getElementById("base-player");
    const body = $("body");

    const player = {
        filesystem : null,
        playing_row : null,
        stream_address: null,
        socket: null,
        web_radio_state: null,

        is_streaming: false,

        set_filesystem(filesystem) {
           this.filesystem = filesystem;
        },

        set_web_radio_state(state) {
            this.playing_row = state.id;
            this.update_playing_row();
        },

        update_playing_row() {
            const filesystem_rows = $(".player-filesystem-row.player-filesystem-row-file");
            const filesystem_playing_row = $(
                ".player-filesystem-row.player-filesystem-row-file[data-id='" + this.playing_row + "']"
            );

            filesystem_rows.find(".filesystem-play-button")
                .removeClass("pause")
                .addClass("play")
                .find(".glyphicon-pause")
                .toggleClass("glyphicon-pause glyphicon-play");

            if (this.playing_row === null) {
                return;
            }

            filesystem_playing_row.find(".filesystem-play-button")
                .removeClass("play")
                .addClass("pause")
                .find(".glyphicon-play")
                .toggleClass("glyphicon-play glyphicon-pause");
        },

        set_socket(socket) {
            this.socket = socket;
        },

        set_streaming_address(address) {
            this.stream_address = address;
        },

        start_streaming() {
            if (this.socket === null || this.stream_address === null || typeof this.stream_address !== "string") {
                throw new Error("Can't start streaming, stream address or signaling control server are not defined");
            }

            this.is_streaming = true;
            // common_player.src = this.stream_address;
            // common_player.load();
            // common_player.play();
        },

        signal_stream_audio(data) {
            this.socket.emit('audio_stream_dispatch', data);
        },

        stop_streaming() {
            this.is_streaming = false;
        }
    };

    body.on("click", ".filesystem-play-button.play", function (e) {
        const target = $(e.currentTarget);
        const path = target.data("path");

        player.playing_row = target.parent().parent().data("id");
        player.update_playing_row();

        if (player.is_streaming) {
            player.signal_stream_audio({
                id: player.playing_row,
                code: codes.request.CODE_STREAM_PLAY,
                media: path.replace(parameters.audio_resources_root, "")
            })
        } else {
            if (common_player.src !== path) {
                common_player.src = path;
            }
            // common_player.play();
        }
    });

    body.on("click", ".filesystem-play-button.pause", function (e) {
        const target = $(e.currentTarget);
        const res = target.data("res");

        if (player.is_streaming) {
            player.signal_stream_audio({
                code: codes.request.CODE_STREAM_PAUSE
            });
        } else {
            common_player.pause();
            player.playing_row = null;
            player.update_playing_row();
        }
    });

    return player;
});