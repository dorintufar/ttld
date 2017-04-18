// For any third party dependencies, like jQuery, place them in the lib folder.

// Configure loading modules from the lib directory,
// except for 'app' ones, which are in a sibling
// directory.
requirejs.config({
    "baseUrl" : "../resources/js/frontend/app",
    "paths" : {
        "jquery" : "../../../vendor/jquery-3.2.0/jquery-3.2.0.min",
        "jquery.bootstrap" : "../../../vendor/bootstrap-3.3.7-dist/js/bootstrap.min",
        "underscore" : "../../../vendor/underscore-1.8.3/underscore-1.8.3.min",
        "io" : "../../../vendor/socket.io-1.3.7/socket.io-1.3.7.min",
        "md5" : "../../util/md5.min",
    },

    "shim" : {
        "jquery.bootstrap" : {
            "deps" : ["jquery"]
        }
    }
});

// Start loading the main app file. Put all of
// your application logic in there.
requirejs([
    "jquery",
    "jquery.bootstrap",
    "underscore",
    "md5",
    "io",

    "main"
]);
