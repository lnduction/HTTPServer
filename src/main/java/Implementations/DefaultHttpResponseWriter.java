package Implementations;

import Configs.HttpResponseWriter;
import Configs.HttpServerConfig;
import Configs.ReadableHttpResponse;
import Server.Constants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class DefaultHttpResponseWriter extends AbstractHttpConfigurableComponent implements HttpResponseWriter {
    @Override
    public void writeHttpResponse(OutputStream out, ReadableHttpResponse response) throws IOException {
        PrintWriter printWriter = new PrintWriter(new BufferedWriter( new OutputStreamWriter( out, StandardCharsets.UTF_8 ) )  );
        printWriter.println( String.format( "%s %s %s",
                Constants.defaultVersion, response.getStatus(), httpServerConfig.getStatusMessage( response.getStatus() )));
        for(Map.Entry<String, String> header: response.getHeaders().entrySet())
            printWriter.println(String.format("%s: %s",  header.getKey(), header.getValue() ));
        printWriter.println(  );
        out.flush();
        if (response.getBody().length != 0) {
            out.write( response.getBody() );
            out.flush();
        }
    }

    DefaultHttpResponseWriter(HttpServerConfig httpServerConfig) {
        super(httpServerConfig);
    }
}
