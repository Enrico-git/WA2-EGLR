'use strict'
import Comment from "../models/Comment.js"

export const getCommentsById = (listOfIDS) => {
    return Comment.find({ _id : { $in : listOfIDS } } ).exec()
}