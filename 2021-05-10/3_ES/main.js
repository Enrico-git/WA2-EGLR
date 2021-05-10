"use strict";

import express from 'express'
import morgan from 'morgan' //for log in server endpoint

const app = express()

app.use(morgan('dev'))
app.use(express.static('public', {})) //for image person
app.use(express.json()) // for post rest incomingData != null

app.get("/", (req, res) => {
	res.send("<html><body><h1>Home page</h1><img src='person.png'/></body></html>")
})

app.get(("/rest"), (req, res) => {
	res.json({a: 'b', date: new Date(), l: [1, 2, 3]})
})

app.post("/rest", (req, res) => {
        res.json({result: 'ok', incomingData: req.body})
})

app.listen(3000, () => ( console.log("Server ready") ))

