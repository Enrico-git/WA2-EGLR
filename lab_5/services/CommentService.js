'use strict'

import Comments from '../models/Comments.js'
import {addCommentToProduct} from './ProductService.js'
import mongoose from 'mongoose'

export const getCommentsById = (comments) => {
    return Comments.find({ _id : { $in : comments } } ).sort({ _id: -1 }).exec()
}

export const addComment = async (comment, productID) => {
    const commentID = mongoose.Types.ObjectId()
    const createdComment = await Comments.create({_id: commentID, ...comment, date: new Date()})
    await addCommentToProduct(productID, commentID, comment.stars)
    return createdComment
}