'use strinct'
import Product from "../models/Product.js"

export const getProductById = (id, numOfComments = 20) => {
    return Product.findOne({_id: id}, {comments: {$slice: Number(numOfComments)}}).exec()
}

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