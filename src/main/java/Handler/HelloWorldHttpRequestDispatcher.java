package Handler;

import Configs.HttpRequestDispatcher;
import Server.HttpRequest;
import Server.HttpResponse;
import Server.HttpServerContext;

import java.io.IOException;

public class HelloWorldHttpRequestDispatcher implements HttpRequestDispatcher {
    @Override
    public void handle(HttpServerContext context, HttpResponse response, HttpRequest request) throws IOException {
        response.setBody( "<h1>Hello world!</h1>" );

    }
}
