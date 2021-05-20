'use strict'

import express from 'express'
import {makeExecutableSchema} from 'graphql-tools'
import {graphqlHTTP} from 'express-graphql'
import './db.js'
import * as ProductService from './services/ProductService.js'
import * as CommentService from './services/CommentService.js'

const typeDefs = `
    scalar DateTime
    
    enum ProductCategory {
        STYLE
        FOOD
        TECH
        SPORT
    }
    
    enum SortingValue {
        createdAt
        price
    }
    
    enum SortingOrder {
        asc
        desc
    }
    
    input ProductCreateInput {
        name : String!,
        description : String,
        price : Float!,
        category: ProductCategory!
    }
    
    input CommentCreateInput {
        title: String!,
        body: String,
        stars: Int!
    }
    
    type Comment {
        _id: ID!,
        title: String!,
        body: String,
        stars: Int!,
        date: DateTime!
    }
    
    type Product {
        _id: ID!,
        name: String!,
        createdAt: DateTime!,
        description: String,
        price: Float!,
        comments (numberOfLastRecentComments: Int) : [Comment],
        category: ProductCategory!,
        stars: Float
    }
    
    input FilterProductInput {
        categories: [ProductCategory],
        minStars: Int,
        minPrice: Float,
        maxPrice: Float
    }
    
    input SortProductInput {
        value: SortingValue!,
        order: SortingOrder!
    }
    
    type Query {
        products (filter: FilterProductInput, sort: SortProductInput) : [Product],
        product (id: ID!) : Product,
        hello: String
    }
    
    type Mutation {
        createProduct (createProductInput: ProductCreateInput!) : Product,
        createComment (
            createCommentInput: CommentCreateInput!,
            productId: ID!
        ) : Comment
    }
`

const resolvers = {
    Query: {
        hello: () => {
            return 'Hello Graphql....'
        },
        product: async (parent, args, context, info) => {
            const numOfComments = info.fieldNodes[0]
                .selectionSet
                .selections
                .find(e => e.name.value == "comments")
                ?.arguments[0]
                .value
                .value
            return await ProductService.getProductById(args.id, numOfComments)
        }
    },
    Product: {
        comments: async (product) => {
            return CommentService.getCommentsById(product.comments)
        }
    },
    Mutation: {
        createProduct: async (parent, args) => {
            return await ProductService.addProduct(args.createProductInput)
        },
        createComment: async (parent, args) => {
            return await CommentService.addComment(args.createCommentInput, args.productId)
        }
    }
}

const schema = makeExecutableSchema({typeDefs, resolvers})

const app = express()

app.use('/graphql', graphqlHTTP({schema, graphiql: true}))

app.listen(3000)

