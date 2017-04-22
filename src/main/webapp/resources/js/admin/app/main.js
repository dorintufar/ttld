"use strict";

define(function (require) {
    const filesystem = require("modules/filesystem");

    const socket = require("modules/socket_connector");
    const socket_events = require("modules/socket_events");

    const player = require("modules/player");
    const player_file_upload_form = require("modules/form/player_file_upload_form");
    const player_create_directory_form = require("modules/form/player_create_directory_form");

    socket_events.register(socket, filesystem, player);
    player.set_filesystem(filesystem);
    player.set_socket(socket);
    filesystem.set_player(player);

    const codes = require("modules/audio_dispatcher/codes");

    socket.emit("audio_directory_root_mirror");
    socket.emit("audio_stream_dispatch", {code : codes.request.CODE_STREAM_URL_REQUIRED});
});