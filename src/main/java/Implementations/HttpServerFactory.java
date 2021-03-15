package Implementations;

import Configs.HttpServerConfig;
import Server.HandlerConfig;
import Server.HttpServer;

import java.util.Properties;

public class HttpServerFactory {
    protected  HttpServerFactory() { }

    public static HttpServerFactory create(){
    return new HttpServerFactory();
    }

    public HttpServer createHttpServer(HandlerConfig handlerConfig, Properties overrideServerProperties) {
        HttpServerConfig httpServerConfig = new DefaultHttpServerConfig(handlerConfig, overrideServerProperties);
        return new DefaultHttpServer(httpServerConfig);
    }
}
