'use strict'

import mongoose from "mongoose"

export default mongoose.model('Product', {
    _id: mongoose.ObjectId,
    name: String,
    createdAt: Date,
    description: String,
    price: Number,
    category: String,
    stars: Number,
    comments: [mongoose.ObjectId]
}, "products");