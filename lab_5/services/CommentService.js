'use strict'
import mongoose from "mongoose"
import Comment from "../models/Comment.js"
import * as ProductService from "./ProductService.js"

export const getCommentsById = (listOfIDS) => {
    return Comment.find({ _id : { $in : listOfIDS } } ).sort({_id: -1}).exec()
}

export const addComment = async (comment, productID) => {
    const commentID = mongoose.Types.ObjectId()
    const createdComment = await Comment.create({_id: commentID, ...comment, date: new Date()})
    await ProductService.addCommentToProduct(productID, commentID, comment.stars )
    return createdComment
}