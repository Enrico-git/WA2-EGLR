'use strict'

import mongoose from 'mongoose'

const commentSchema = new mongoose.Schema({
    _id: mongoose.ObjectId,
    title: String,
    body: String,
    stars: Number,
    date: Date
})

export default mongoose.model('comment', commentSchema)