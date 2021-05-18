'use strinct'
import express from "express"
import { makeExecutableSchema } from "graphql-tools"
import { graphqlHTTP } from "express-graphql"
import morgan from "morgan"
import "./db.js"
import * as ProductService from "./services/ProductService.js"
import * as CommentService from "./services/CommentService.js"

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
        category: ProductCategory!,
        stars: Float,
        comments (numberOfLastRecentComments: Int) : [Comment],
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
    Product: {
        comments: async (product) => {
            return await CommentService.getCommentsById(product.comments)
        }
    }
}
const schema = makeExecutableSchema({ typeDefs, resolvers })

const app = express()

app.use(morgan('tiny'))
app.use('/graphQL', graphqlHTTP({schema, graphiql: true }))
app.listen(3000)