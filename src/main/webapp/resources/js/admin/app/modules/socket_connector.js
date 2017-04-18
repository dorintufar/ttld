"use strict";

define(["io"], function(io) {
    const url = "http://178.168.58.17:9000/admin";
    return io.connect(url);
});
