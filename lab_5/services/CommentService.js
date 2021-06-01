'use strict'
import mongoose from "mongoose"
import Comment from "../models/Comment.js"
import * as ProductService from "./ProductService.js"
import {db} from "../db.js"

export const getCommentsById = (listOfIDS) => {
    return Comment.find({ _id : { $in : listOfIDS } } ).sort({_id: -1}).exec()
}

export const addComment = async (comment, productID) => {
    const session = await db.startSession()
    session.startTransaction()
    const commentID = mongoose.Types.ObjectId()
    const createdComment = await Comment.create({_id: commentID, ...comment, date: new Date()})
    if ( ! await ProductService.addCommentToProduct(productID, commentID, comment.stars ))
        throw new Error("Could not add comment to product. Rolling back")
    await session.commitTransaction()
    session.endSession()
    return createdComment
}