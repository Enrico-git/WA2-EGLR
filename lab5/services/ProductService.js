'use strict'
import Product from "../models/Product.js"
import mongoose from "mongoose"

export const getProductById = (id, numOfComments = 0) => {
    return Product.findOne({ _id: id }, { comments: { $slice: Number(-numOfComments) } }).exec()
}

export const getProducts = (filter, sort, numOfComments = 0) => {
    return Product.find({
            ...filter?.categories && {
                category: { // if filter.categories is present we add the category filter
                    $in: filter.categories
                }
            },
            ...filter?.stars && {
                stars: {
                    $gte: filter.minStars
                }
            },
            ...(filter?.minPrice || filter?.maxPrice) && {
                price: {
                    ...filter.minPrice && {$gte: filter.minPrice},
                    ...filter.maxPrice && {$lte: filter.maxPrice}
                }
            }
        },
        {
            comments: { $slice: Number(-numOfComments) }
        })
        .sort({
            [sort?.value]: sort?.order
        })
        .exec()
}

export const addProduct = product => {
    return Product.create({ _id: mongoose.Types.ObjectId(), ...product, createdAt: new Date(), stars: 0, comments: [] })
}

export const addCommentToProduct = (productID, commentID, stars) => {
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
        ], {useFindAndModify: false}
    ).exec()
}
