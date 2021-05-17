import mongoose from 'mongoose'
import Comment from './comment.js'

const productSchema = new mongoose.Schema({
    _id: mongoose.ObjectId,
    name: String,
    createdAt: Date,
    description: String,
    price: Number,
    comments: [Comment.schema],
    category: String,
    stars: Number
})

export default mongoose.model('product', productSchema)