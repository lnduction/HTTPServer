package Configs;

public interface HttpResponseBuilder {

    ReadableHttpResponse buildHttpResponse();
    void prepareHttpResponse(ReadableHttpResponse response, boolean clearBody);
}
