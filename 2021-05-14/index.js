import express from 'express'
import {buildSchema} from "graphql";
import {graphqlHTTP} from "express-graphql";

const schema = buildSchema(`
    type Query {
        hello: String!
        messages: [Message!]!
        goodBye(name: String): String!
    }
    type Message {
        id: ID
        text: String
    }
`)

const root = {
    // {
    //     hello
    // }
    hello: () => {
        return 'Hello Graphql'
    },

    // {
    //     messages: {
    //         id
    //         text
    //     }
    // }
    messages: () => {
        return [{id: 1, text: "msg1"}, {id: 2, text: "msg2"}]
    },

    // {
    //     goodBye(name: "Joe")
    // }
    goodBye: ({name}) => {
        return `Good bye ${name}`
    }

}

const app = express()


app.use("/graphql", graphqlHTTP(
    {
        schema: schema, rootValue: root, graphiql: true
    }
))

app.listen(3000)