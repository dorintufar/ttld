"use strict";

define(["jquery"], function ($) {
    const template =
        '<div class="item-row" data-path="__path__">' +
            '<div id="__descriptor_directory__"></div>' +
            '<div id="__descriptor_file__"></div>' +
        '</div>';

    return {
        render : function (descriptor_directory, descriptor_file, path) {
            if (typeof descriptor_directory === "string" && typeof descriptor_file === "string") {
                return $(template
                    .replace("__descriptor_directory__", descriptor_directory)
                    .replace("__descriptor_file__", descriptor_file)
                    .replace("__path__", path)
                );
            }

            return null;
        }
    }
});