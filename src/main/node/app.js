var http = require('http'),
    fs = require('fs'),
    util = require('util');

var path = "music.mp3";

var port = 8888;
var host = "localhost";

http.createServer(function (req, res) {

    var stat = fs.statSync(path);
    var total = stat.size;

    if (req.headers.range) {   // meaning client (browser) has moved the forward/back slider
        // which has sent this request back to this server logic ... cool
        var range = req.headers.range;
        var parts = range.replace(/bytes=/, "").split("-");
        var partialstart = parts[0];
        var partialend = parts[1];

        var start = parseInt(partialstart, 10);
        var end = partialend ? parseInt(partialend, 10) : total-1;
        var chunksize = (end-start)+1;
        console.log('RANGE: ' + start + ' - ' + end + ' = ' + chunksize);

        var file = fs.createReadStream(path, {start: start, end: end});
        res.writeHead(206, { 'Content-Range': 'bytes ' + start + '-' + end + '/' + total, 'Accept-Ranges': 'bytes', 'Content-Length': chunksize, 'Content-Type': 'video/mp4' });
        file.pipe(res);

    } else {

        console.log('ALL: ' + total);
        res.writeHead(200, { 'Content-Length': total, 'Content-Type': 'audio/mpeg' });
        fs.createReadStream(path).pipe(res);
    }
}).listen(port, host);

console.log("Server running at http://" + host + ":" + port + "/");

function stream_response( res, file_path, content_type ){
    var readStream = fs.createReadStream(file_path);

    readStream.on('data', function(data) {
        var flushed = res.write(data);
        // Pause the read stream when the write stream gets saturated
        console.log( 'streaming data', file_path );
        if(!flushed){
            readStream.pause();
        }
    });

    res.on('drain', function() {
        // Resume the read stream when the write stream gets hungry
        readStream.resume();
    });

    readStream.on('end', function() {
        res.end();
    });

    readStream.on('error', function(err) {
        console.error('Exception', err, 'while streaming', file_path);
        res.end();
    });

    res.writeHead(200, {'Content-Type': content_type});
}