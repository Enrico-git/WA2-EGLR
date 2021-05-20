'use strict'
import express from "express"
import { makeExecutableSchema } from "graphql-tools"
import { graphqlHTTP } from "express-graphql"
import morgan from "morgan"
import "./db.js"
import {typeDefs} from "./graphQL/typedef.js"
import {resolvers} from "./graphQL/resolver.js"

const schema = makeExecutableSchema({ typeDefs, resolvers })

const app = express()

app.use(morgan('tiny'))
app.use('/graphQL', graphqlHTTP({schema, graphiql: true }))
app.listen(3000)