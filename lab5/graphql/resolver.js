'use strict';

import * as ProductService from "../services/ProductService.js";
import * as CommentService from "../services/CommentService.js";

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

            try {
                return await ProductService.getProductById(args.id, numOfComments)
            } catch (e) {
                console.error(e);
                throw e;
            }
        },
        products: async (parent, args, context, info) => {
            const filter = args.filter;
            const sort = args.sort;
            const numOfComments = info.fieldNodes[0]
                .selectionSet
                .selections
                .find(e => e.name.value == "comments")
                ?.arguments[0]
                .value
                .value;
            try {
                return await ProductService.getProducts(filter, sort, numOfComments);
            } catch(e) {
                console.error(e);
                throw e;
            }
        }
    },

    Product: {
        comments: async (product) => {
            try {
                return await CommentService.getCommentsById(product.comments);
            } catch(e) {
                console.error(e);
                throw e;
            }
        }

    },

    Mutation: {
        createProduct: async (parent, args, context, info) => {
            try {
                return await ProductService.addProduct(args.createProductInput);
            } catch(e) {
                console.error(e);
                throw e;
            }
        },
        createComment: async (parent, args, context, info) => {
            try {
                return await CommentService.addComment(args.createCommentInput, args.productId);
            } catch(e) {
                console.error(e)
                throw e
            }
        }
    }
}
