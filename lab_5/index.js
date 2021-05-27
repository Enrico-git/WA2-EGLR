'use strict';

import express from 'express'
import {graphqlHTTP} from "express-graphql";
import {makeExecutableSchema} from "graphql-tools";
import "./db.js"
import {typeDefs} from './graphql/typedef.js';
import {resolvers} from './graphql/resolver.js';
import {constraintDirective, constraintDirectiveTypeDefs} from 'graphql-constraint-directive';



const schema = makeExecutableSchema({
    typeDefs: [constraintDirectiveTypeDefs, typeDefs],
    resolvers,
    schemaTransforms: [constraintDirective()]
})

const app = express();

app.use('/graphql', graphqlHTTP({schema, graphiql:true}));

app.listen(3000);