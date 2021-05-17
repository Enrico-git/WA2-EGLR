import express from 'express'
import {makeExecutableSchema} from 'graphql-tools'
import {graphqlHTTP} from 'express-graphql'
import mongoose from 'mongoose'
import Product from './product.js'

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
        product: async (parent, args) => { //args contains the ID
            return await Product.findOne({_id: mongoose.Types.ObjectId(args.id)}).exec()
        }
    },
    //TODO
    Product: {
        comments: (product, args, context, info) => {
            // locate comments for the given product
            console.log(args)
            console.log(product)
        }
    }
}

try {
    await mongoose.connect(
        'mongodb://localhost:27017/catalogue',
        {
            useNewUrlParser: true,
            useUnifiedTopology: true
        }
    )
    mongoose.connection.on('error', err => {
        //handle here disconnections that may happen later
    });
    //here the connection is ready to be used

} catch (error) {
    //problems in establishing the connection
    //handleError(error)
}

const schema = makeExecutableSchema({typeDefs, resolvers})

const app = express()

app.use('/graphql', graphqlHTTP({schema, graphiql: true}))


app.listen(3000)

