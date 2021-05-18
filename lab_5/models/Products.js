'use strict'

import mongoose from 'mongoose'
import Comment from './Comments.js'

const productSchema = new mongoose.Schema({
    _id: mongoose.ObjectId,
    name: String,
    createdAt: Date,
    description: String,
    price: Number,
    comments: [mongoose.ObjectId],
    category: String,
    stars: Number
})

export default mongoose.model('product', productSchema)