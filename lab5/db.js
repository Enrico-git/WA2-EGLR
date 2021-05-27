'use strict'
import mongoose from "mongoose";

export const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
    console.log('connected to db');
})

try {
    await mongoose.connect('mongodb://localhost:27017/catalog', {useNewUrlParser: true, useUnifiedTopology: true});
} catch(e){
    console.error(e)
}