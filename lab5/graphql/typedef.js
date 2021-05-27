'use strict';

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
        price : Float! @constraint(min: 0),
        category: ProductCategory!
    }
    
    input CommentCreateInput {
        title: String!,
        body: String,
        stars: Int! @constraint(min: 0, max: 5)
    }
    
    type Comment {
        _id: ID!,
        title: String!,
        body: String,
        stars: Int! @constraint(min: 0, max: 5),
        date: DateTime!
    }
    
    type Product {
        _id: ID!,
        name: String!,
        createdAt: DateTime!,
        description: String,
        price: Float! @constraint(min: 0),
        comments (numberOfLastRecentComments: Int) : [Comment],
        category: ProductCategory!,
        stars: Float @constraint(min: 0, max: 5)
    }
    
    input FilterProductInput {
        categories: [ProductCategory],
        minStars: Int @constraint(min: 0, max: 5),
        minPrice: Float @constraint(min: 0),
        maxPrice: Float @constraint(min: 0)
    }
    
    input SortProductInput {
        value: SortingValue!,
        order: SortingOrder!
    }
    
    type Query {
        products (filter: FilterProductInput, sort: SortProductInput) : [Product],
        product (id: ID!) : Product
    }
    
    type Mutation {
        createProduct (createProductInput: ProductCreateInput!) : Product,
        createComment (
            createCommentInput: CommentCreateInput!,
            productId: ID!
        ) : Comment
    }`