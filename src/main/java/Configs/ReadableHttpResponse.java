package Configs;


import Server.HttpResponse;

import java.util.Map;

public interface ReadableHttpResponse extends HttpResponse {

    int getStatus();
    Map<String , String> getHeaders();
    byte[] getBody();
    int getBodyLength();
}
