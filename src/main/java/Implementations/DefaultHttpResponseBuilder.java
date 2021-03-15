package Implementations;

import Configs.HttpResponseBuilder;
import Configs.HttpServerConfig;
import Configs.ReadableHttpResponse;
import Server.ServerInfo;

import java.text.SimpleDateFormat;
import java.util.*;

class DefaultHttpResponseBuilder extends AbstractHttpConfigurableComponent implements HttpResponseBuilder {

    DefaultHttpResponseBuilder(HttpServerConfig httpServerConfig) {
        super(httpServerConfig);
    }

    protected ReadableHttpResponse createReadableHttpResponseInstance(){
        return new DefaultReadableHttpResponse();
    }

    @Override
    public ReadableHttpResponse buildHttpResponse() {

        DefaultReadableHttpResponse response = new DefaultReadableHttpResponse();

        response.setHeader( "Date",  new Date() );
        response.setHeader(  "Server", httpServerConfig.getServerInfo().getName() );
        response.setHeader(  "Content-Language", "en" );
        response.setHeader(  "Connection", "close" );
        response.setHeader(  "Content-Type", "text/html" );

        return response;
    }

    @Override
    public void prepareHttpResponse(ReadableHttpResponse response, boolean clearBody) {
        if (response.getStatus() >= 400 && response.isBodyEmpty())
            setDefaultResponseErrorBody(response);

        setContentLength(response);
        if (clearBody) clearBody(response);
        else response.setHeader( "Content-Length", response.getBodyLength() );
    }

    protected void setDefaultResponseErrorBody(ReadableHttpResponse response) {
        Map<String, Object> args = buildMap(new Object[][] {
                { "STATUS-CODE", response.getStatus() },
                { "STATUS-MESSAGE", httpServerConfig.getStatusMessage(response.getStatus()) }
        });
        String content = httpServerConfig.getHttpServerContext().getHtmlTemplateManager().processTemplate("error.html", args);
        response.setBody(content);
    }

    protected void setContentLength(ReadableHttpResponse response) {
        response.setHeader("Content-Length", response.getBodyLength());
    }

    protected void clearBody(ReadableHttpResponse response) {
        response.setBody("");
    }

    private  Map<String, Object> buildMap(Object[][] data) {
        Map<String, Object> map = new HashMap<>();
        for(Object[] row : data)
            map.put(String.valueOf(row[0]), row[1]);
        return Collections.unmodifiableMap(map);
    }
}
