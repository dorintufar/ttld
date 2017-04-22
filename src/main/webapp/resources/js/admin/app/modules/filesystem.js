"use strict";

const DESCRIPTOR_DIRECTORY = "directory";
const DESCRIPTOR_FILE = "file";

const EVENT_ENTRY_CREATE = "ENTRY_CREATE";
const EVENT_ENTRY_DELETE = "ENTRY_DELETE";

define([
    "jquery",
    "jquery.bootstrap",
    "underscore",
    "parameters",
    "os_meta",
    "modules/renderer/directory",
    "modules/renderer/directory_segment",
    "modules/renderer/file"
], function($, bootstrap, _, parameters, os_meta, renderer_directory, renderer_directory_segment, renderer_file) {
    const body = $("body");
    const path_input = $(".filesystem-path");

    const directory_up = $(".filesystem-directory-up");
    const directory_new = $(".filesystem-directory-new");
    const directory_new_modal = $(".filesystem-directory-new-modal");
    const directory_current_container = $(".current-directory-container");

    const file_new = $(".filesystem-file-new");
    const file_new_modal = $(".filesystem-file-new-modal");

    const filesystem = {
        current_container : null,
        current_map_segment : null,
        rendered_containers : {},
        path_current : "",
        map : null,
        player : null,

        set_player(player) {
            this.player = player;
        },

        set_current_path(path, force) {
            if (this.map === null) {
                return false;
            }

            if (typeof path === "string") {
                this.path_current = path;
                path_input.val(path);
            }

            if (this.current_container !== null) {
                this.current_container.hide();
            }

            if (typeof this.rendered_containers[this.path_current] !== "undefined" && force !== true) {
                this.current_container = this.rendered_containers[this.path_current];
                this.current_container.show();
                return true;
            }

            this.current_map_segment = this._find(this.path_current);

            this._render_current();
            this.player.update_playing_row();
            this.rendered_containers[this.path_current] = this.current_container;

            return true;
        },

        set_map(map) {
            this.map = map;
            this.set_current_path(path_input.val());
        },

        update(event, path, is_file, separator) {
            var pieces = path.split(separator);
            if (pieces[0] === "")
                pieces.shift();

            path = pieces.join(separator);

            switch (event) {
                case EVENT_ENTRY_CREATE :
                    this._add_to_map(pieces, is_file);
                    this._clean_and_render_current();
                    break;
                case EVENT_ENTRY_DELETE :
                    this._delete_from_map(pieces);
                    if (path === this.path_current) {
                        pieces.pop();
                        this.set_current_path(pieces.join(os_meta.file_separator));
                    }
                    this._clean_and_render_current();
                    break;
            }
        },

        _find(path) {
            var segment = this.map.children;
            if (path.length > 0) {
                const pieces = path.split(os_meta.file_separator);

                for (var i = 0; i < pieces.length; i++) {
                    if (typeof segment[pieces[i]] === "undefined") {
                        return null;
                    }
                    segment = segment[pieces[i]].children;
                }
            }
            return segment;
        },

        _render_current() {
            if (this.current_map_segment === null) {
                return false;
            }

            this.current_container = renderer_directory_segment.render(DESCRIPTOR_DIRECTORY, DESCRIPTOR_FILE, this.path_current);
            var $current_container = this.current_container;

            const $path_current = this.path_current + (
                this.path_current[this.path_current.length] === os_meta.file_separator || this.path_current.length === 0  ? "" : os_meta.file_separator
            );

            const root_and_current = parameters.audio_resources_root + $path_current;
            var item;

            _.each(this.current_map_segment, function(piece, name) {
                switch (piece.type) {
                    case DESCRIPTOR_FILE :
                        item = renderer_file.render(name, root_and_current + name);
                        $current_container.children("#" + DESCRIPTOR_FILE).append(item);
                        break;
                    case DESCRIPTOR_DIRECTORY :
                        item = renderer_directory.render(name, $path_current + name);
                        $current_container.children("#" + DESCRIPTOR_DIRECTORY).append(item);
                        break;
                }
            });

            directory_current_container.append($current_container);
            return true
        },

        _delete_from_map(path_pieces) {
            var to_delete = this.map.children;
            for (var i = 0; i < path_pieces.length; i++) {
                if (typeof to_delete[path_pieces[i]] === "undefined") {
                    return null;
                }
                if (i === path_pieces.length - 1) {
                    delete to_delete[path_pieces[i]];
                    break;
                }
                to_delete = to_delete[path_pieces[i]].children;
            }
        },

        _add_to_map(path_pieces, is_file) {
            var current = this.map.children;
            for (var i = 0; i < path_pieces.length - 1; i++) {
                current = current[path_pieces[i]].children;
            }

            current[path_pieces[path_pieces.length - 1]] = {
                type : is_file ? DESCRIPTOR_FILE : DESCRIPTOR_DIRECTORY
            };

            if (!is_file) {
                current[path_pieces[path_pieces.length - 1]].children = {};
            }
        },

        _clean_and_render_current() {
            _.each(this.rendered_containers, function(container) {
                container.remove();
            });

            this.rendered_containers = {};
            this.set_current_path(this.path_current);
        }
    };

    path_input.on("keypress", function(e) {
        var code = e.keyCode || e.which;

        if(code === 13) { //Enter keycode
            var path = path_input.val();

            if (path[path.length] === os_meta.file_separator) {
                path = path.substr(0, path.length - 1);
            }

            filesystem.set_current_path(path);
        }
    });

    directory_up.on("click", function () {
        // if (filesystem.current_map_segment === null){
        //     return;
        // }

        var pieces = path_input.val().split(os_meta.file_separator);
        pieces.pop();
        filesystem.set_current_path(pieces.join(os_meta.file_separator));
    });

    directory_new.on("click", function () {
        // if (filesystem.current_map_segment === null){
        //     return;
        // }

        directory_new_modal.modal("show");
    });

    file_new.on("click", function () {
        // if (filesystem.current_map_segment === null){
        //     return;
        // }

        file_new_modal.modal("show");
    });

    body.on("dblclick", ".player-filesystem-row-directory", function(e) {
        const target = $(e.currentTarget);
        filesystem.set_current_path(target.data("path"));
    });

    return filesystem;
});