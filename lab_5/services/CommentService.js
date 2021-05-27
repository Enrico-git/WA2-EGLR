'use strict'

import Comments from '../models/Comments.js'
import {addCommentToProduct} from './ProductService.js'
import mongoose from 'mongoose'

import {db} from '../db.js'

export const getCommentsById = (comments) => {
    return Comments.find({_id: {$in: comments}}).sort({_id: -1}).exec()
}

export const addComment = async (comment, productID) => {
    const session = await db.startSession()
    session.startTransaction()
    const commentID = mongoose.Types.ObjectId()
    const createdComment = await Comments.create({_id: commentID, ...comment, date: new Date()})

    if (! await addCommentToProduct(productID, commentID, comment.stars))
        throw new Error("productID not found")

    await session.commitTransaction()
    session.endSession()
    return createdComment
}