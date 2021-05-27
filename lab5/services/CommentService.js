'use strict';
import mongoose from 'mongoose';
import Comment from '../models/Comment.js';
import {addCommentToProduct} from './ProductService.js';
import {db} from "../db.js";

export let getCommentsById = (comments) => {
    return Comment.find({ _id : { $in : comments } } ).sort({ _id: -1 }).exec()
}

export const addComment = async (comment, productId) => {
    const session = await db.startSession();
    session.startTransaction();
    const commentId = mongoose.Types.ObjectId();
    const createdComment = await Comment.create({_id:commentId, ...comment, date: new Date()})
    if(!await addCommentToProduct(productId, commentId, comment.stars))
        throw new Error("Could not add comment. Rolling back")
    await session.commitTransaction();
    session.endSession();
    return createdComment;
}