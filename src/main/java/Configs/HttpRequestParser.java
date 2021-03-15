package Configs;

import Exceptions.HttpServerExceptions;
import Server.HttpRequest;

import java.io.IOException;
import java.io.InputStream;

public interface HttpRequestParser {
    HttpRequest parserHttpRequest(InputStream inputStream, String remoteAddress) throws IOException, HttpServerExceptions;

}
