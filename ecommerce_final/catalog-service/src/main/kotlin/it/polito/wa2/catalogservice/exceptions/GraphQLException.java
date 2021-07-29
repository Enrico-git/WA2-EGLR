package it.polito.wa2.catalogservice.exceptions;

import graphql.*;
import graphql.language.*;
import java.util.*;

public class GraphQLException extends RuntimeException implements GraphQLError {

    String customMessage;

    public GraphQLException(String customMessage) {
        this.customMessage = customMessage;
    }

    @Override
    public String getMessage() {
        return customMessage;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }
}
