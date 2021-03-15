package Implementations;

import Server.HttpRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

 class DefaultHttpRequest implements HttpRequest {

    private final String startingLine;
    private final String method;
    private final String uri;
    private final String httpVersion;
    private final String remoteAddress;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;

     public DefaultHttpRequest(String startingLine, String method, String uri, String httpVersion, String remoteAddress, Map<String, String> headers, Map<String, String> parameters) {
         this.startingLine = startingLine;
         this.method = method;
         this.uri = uri;
         this.httpVersion = httpVersion;
         this.remoteAddress = remoteAddress;
         this.headers = Collections.unmodifiableMap(headers);
         this.parameters = Collections.unmodifiableMap(parameters);
     }

     @Override
    public String getStartingLine() {

         return String.format( "%s %s %s", getMethod(), getUri(), getHttpVersion() );
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getHttpVersion() { return httpVersion; }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "DefaultHttpRequest{" + "\n" +
                "startingLine='" + startingLine + '\'' + "\n" +
                ", method='" + method + '\'' + "\n" +
                ", uri='" + uri + '\'' + "\n" +
                ", httpVersion='" + httpVersion + '\'' + "\n" +
                ", remoteAddress='" + remoteAddress + '\'' + "\n" +
                ", headers=" + headers + "\n" +
                ", parameters=" + parameters + "\n" +
                '}';
    }
}
