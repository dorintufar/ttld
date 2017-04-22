"use strict";

define(["io"], function(io) {
    const url = "http://127.0.0.1:9000/admin";
    return io.connect(url);
});
