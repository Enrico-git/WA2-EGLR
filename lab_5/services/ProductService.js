'use strict'

import Product from '../models/Products.js'
import mongoose from "mongoose"

export const getProductById = (id, numOfComments = 0) => {
    return Product.findOne({_id: id}, {comments: {$slice: Number(-numOfComments)}}).exec()
}


/*
export const getProducts = (filter, sort, numOfComments = 20) => {
    return Product.find({
        category: {
            $in: filter.categories
        },
        stars: {
            $gt: filter.minStars
        },
        price: {
            $gt: filter.minPrice,
            $lt: filter.maxPrice
        }},
        {comments: {$slice: Number(numOfComments)}
    })
    .sort ({
        [sort.value]: sort.order
    })
    .exec()
}
 */

export const addProduct = (product) => {
    return Product.create({_id: mongoose.Types.ObjectId(), ...product, createdAt: new Date(), stars: 0, comments: []})
}

export const addCommentToProduct = async (id, commentID, commentStars) => {
    // (product.stars * length(product.comments) + stars) / (length(product.comments) + 1)
    let product = await Product.findById(id).exec()
    let newStars = ((product.stars*product.comments.length)+commentStars)/(product.comments.length+1)
    return product.update( {
        $push: {
            comments: [commentID]
        },
        $set: {
            stars: newStars
        }
    }).exec()
}