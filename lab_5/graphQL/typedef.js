'use strict'

export const typeDefs = `
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