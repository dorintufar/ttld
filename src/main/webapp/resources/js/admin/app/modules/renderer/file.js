"use strict";

define(["jquery", "md5"], function ($, md5) {
    const template = '' +
        '<div class="row vcenter player-filesystem-row player-filesystem-row-file" data-id="__id__" data-path="__path__" data-play="false">' +
            '<div class="col-sm-2">' +
                '<button type="button" class="btn btn-filesystem-control">' +
                    '<span class="glyphicon glyphicon-music" aria-hidden="true"></span>' +
                '</button>' +
                '<button type="button" class="btn btn-filesystem-control filesystem-play-button play" data-path="__path__">' +
                    '<span class="glyphicon glyphicon-play" aria-hidden="true"></span>' +
                '</button>' +
            '</div>' +
            '<div class="file-name col-sm-8">__name__</div>' +
            '<div class="file-edit col-sm-2">' +
                '<button type="button" class="btn btn-filesystem-control filesystem-delete-button pull-right">' +
                    '<span class="glyphicon glyphicon-trash"></span>' +
                '</button>' +
                '<button type="button" class="btn btn-filesystem-control filesystem-edit-button pull-right">' +
                    '<span class="glyphicon glyphicon-edit"></span>' +
                '</button>'+
            '</div>' +
        '</div>';

    return {
        render : function (name, path) {
            if (typeof name === "string") {
                return $(template
                    .replace(/__name__/g, name)
                    .replace(/__path__/g, path)
                    .replace(/__id__/g, md5.encode(path))
                );
            }

            return null;
        }
    }
});