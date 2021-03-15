package Implementations;

import Configs.HttpRequestDispatcher;
import Exceptions.HttpServerExceptions;
import Server.HttpHandler;
import Server.HttpRequest;
import Server.HttpResponse;
import Server.HttpServerContext;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


class DefaultHttpRequestDispatcher implements HttpRequestDispatcher {
    private final HttpHandler defaultHttpHandler;
    private final Map<String, HttpHandler> httpHandlers;

    DefaultHttpRequestDispatcher(HttpHandler defaultHttpHandler, Map<String, HttpHandler> httpHandlers) {
        super();
        Objects.requireNonNull( defaultHttpHandler, "Default handler should be not null" );
        Objects.requireNonNull( httpHandlers, "httpHandlers should be not null" );
        this.defaultHttpHandler = defaultHttpHandler;
        this.httpHandlers = httpHandlers;
    }

    @Override
    public void handle(HttpServerContext context, HttpResponse response, HttpRequest request) throws IOException {
        try {
            HttpHandler handler = getHttpHandler( request );
            handler.handle( context, response, request );
        } catch (RuntimeException e) {
            if (e instanceof HttpServerExceptions) {
                throw e;
            } else {
                throw new HttpServerExceptions( "Handle request: " + request.getUri() + " failed: " + e.getMessage(), e );
            }
        }
    }

    protected HttpHandler getHttpHandler(HttpRequest request) {
        HttpHandler handler = httpHandlers.get( request.getUri() );
        if (handler == null) {
            handler = defaultHttpHandler;
        }
        return handler;
    }

}
