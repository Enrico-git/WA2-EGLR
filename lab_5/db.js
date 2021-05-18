'use strict'

import mongoose from "mongoose";

try {
    await mongoose.connect(
        'mongodb://localhost:27017/catalogue',
        {
            useNewUrlParser: true,
            useUnifiedTopology: true
        }
    )
    mongoose.connection.on('error', err => {
        //handle here disconnections that may happen later
    });
    //here the connection is ready to be used

} catch (error) {
    //problems in establishing the connection
    //handleError(error)
}