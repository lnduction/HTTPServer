package Configs;

import Server.HttpServerContext;
import Server.ServerInfo;

import java.net.Socket;
import java.util.concurrent.ThreadFactory;

public interface HttpServerConfig extends AutoCloseable {
    
    ServerInfo getServerInfo();
    String getStatusMessage(int statusCode);
    HttpRequestParser getHttpRequestParser();
    HttpResponseBuilder getHttpResponseBuilder();
    HttpResponseWriter getHttpResponseWriter();
    HttpServerContext getHttpServerContext();
    HttpRequestDispatcher getHttpRequestDispatcher();
    ThreadFactory getWorkerThreadFactory();
    HttpClientSocketHandler buildNewHttpClientSocketHandler(Socket clientSocket);
}
