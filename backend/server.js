const express = require('express');
const dotenv = require('dotenv');

dotenv.config();
const app = express();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const port = process.env.PORT;
server.listen(port||3000);

var messArray = [];

io.sockets.on('connection' , function(socket){
    console.log("Co nguoi connect");

    socket.on('client-gui-server', function (data){
        messArray.push(data);
        io.sockets.emit('server-gui-client', {danhsach : messArray});
        console.log(data);
    });
})