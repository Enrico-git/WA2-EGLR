'use strict'
import Product from "../models/Product.js"
import mongoose from "mongoose"

export const getProductById = (id, numOfComments = 0) => {
    return Product.findOne({ _id: id }, { comments: { $slice: Number(-numOfComments) } }).exec()
}

export const getProducts = (filter, sort, numOfComments = 0) => {
    return Product.find({
        category: {
            $in: filter.categories
        },
        stars: {
            $gte: filter.minStars
        },
        price: {
            $gte: filter.minPrice,
            $lte: filter.maxPrice
        }
    },
        {
            comments: { $slice: Number(-numOfComments) }
        })
        .sort({
            [sort.value]: sort.order
        })
        .exec()
}

export const addProduct = product => {
    return Product.create({ _id: mongoose.Types.ObjectId(), ...product, createdAt: new Date(), stars: 0, comments: [] })
}

export const addCommentToProduct = (productID, commentID, stars) => {
    // const product = await Product.findById(productID).exec()
    // const numOfComments = product.comments.length
    return Product.findByIdAndUpdate(productID,
        [{
            $set: {
                comments: {
                    $concatArrays: ["$comments", [commentID]]
                },
                stars: {
                    $let: {
                        vars: {
                            len: { $size: "$comments" }
                        },
                        in: {
                            $divide: [
                                {
                                    $sum: [
                                        { $multiply: ["$stars", "$$len"] }, stars]
                                },
                                {
                                    $sum: ["$$len", 1]
                                }
                            ]
                        }
                    }
                }
            }
        }
        ]
        ).exec()
    // return product.update({
        // $push: {comments: commentID}, 
        // stars: ((numOfComments*product.stars + stars) / (numOfComments+1))
    // }).exec()
}