'use strict'
import * as ProductService from "../services/ProductService.js"
import * as CommentService from "../services/CommentService.js"

export const resolvers = {
    Query: {
        product: async (parent, args, context, info) => {
            const numOfComments = info.fieldNodes[0]
            .selectionSet
            .selections
            .find(e=> e.name.value == "comments")
            ?.arguments[0]
            .value
            .value
                
            return await ProductService.getProductById(args.id, numOfComments)
        },
        products: async (parent, args, context, info) => {
            const filter = args.filter
            const sort = args.sort
            
            const numOfComments = info.fieldNodes[0]
            .selectionSet
            .selections
            .find(e=> e.name.value == "comments")
            ?.arguments[0]
            .value
            .value
            
            return await ProductService.getProducts(filter, sort, numOfComments)
        }
    },
    Mutation: {
        createProduct: async (parent, args, context, info) => {
            return await ProductService.addProduct(args.createProductInput)
        },
        createComment: async (parent, args) => {
            return await CommentService.addComment(args.createCommentInput, args.productId)
        }
    },
    Product: {
        comments: async (product) => {
            return await CommentService.getCommentsById(product.comments)
        }
    }
}