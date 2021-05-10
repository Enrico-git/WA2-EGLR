"use strict";

import http from 'http';

http.createServer(
	(req, res) => {
	  res.writeHead(200, {'Content-Type': 'text/plain'})
	  res.end("Hello, server!")
	}
).listen(8080)

console.log("Server ready on port 8080")
