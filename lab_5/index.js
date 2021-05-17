'use strinct'
import express from "express"
import { makeExecutableSchema } from "graphql-tools"
import { graphqlHTTP } from "express-graphql"
import mongoose from "mongoose"
import morgan from "morgan"

mongoose.connect('mongodb://127.0.0.1:27017/catalog', {useNewUrlParser: true, useUnifiedTopology: true});
const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.log('connected to db');
});

const Product = mongoose.model('Product', {
    _id: mongoose.ObjectId,
    name: String,
    createdAt: Date,
    description: String,
    price: Number,
    // comments (numberOfLastRecentComments: Int) : [Comment],
    category: String,
    stars: Number,
    comments: Array
}, "product");


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
        product: async (parent, args) => {
            return await Product.findOne({_id: args.id}).exec()
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
            })
            .sort ({
                [sort.value]: sort.order
            })
            .exec()
        }
    }
}
const schema = makeExecutableSchema({ typeDefs, resolvers })

const app = express()

app.use(morgan('tiny'))
app.use('/graphQL', graphqlHTTP({schema, graphiql: true }))
app.listen(3000)