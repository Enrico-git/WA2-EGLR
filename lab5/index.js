import express from 'express'
import {graphqlHTTP} from "express-graphql";
import {makeExecutableSchema} from "graphql-tools";
import mongoose from "mongoose";
import Product from "./product.js";

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
        product (_id: ID!) : Product,
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
            return "Hello, graphQL!";
        },
        product: async (parent, args, context, info) => {
            const result = await Product.findOne({_id: args._id}).exec()
            console.log(result);
            return result;
        },
        products: async (parent, args) => {
            const filter = args.filter

            const sort = args.sort
            return await Product.find({
                category: {
                    $in: filter.categories
                },
                stars: {
                    $gt: filter.minStars
                },
                price: {
                    $gt: filter.minPrice,
                    $lt: filter.maxPrice
                }
            }).sort({
                [sort.value]: sort.order
            }).exec();
        }
    },

}
const schema = makeExecutableSchema({typeDefs, resolvers})

try{
    await mongoose.connect(
        'mongodb://localhost:27017/catalogue',
        { useNewUrlParser: true,
            useUnifiedTopology: true});
    mongoose.connection.on('error', err => {
        //handle here disconnections that may happen later
    });

}catch (error){

}

const app = express();
app.use('/graphql', graphqlHTTP({schema, graphiql:true}));
app.listen(3000);
