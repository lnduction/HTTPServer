package Server;

import java.io.IOException;

public interface HttpHandler {
    void handle(HttpServerContext context, HttpResponse response, HttpRequest request ) throws IOException;
}
