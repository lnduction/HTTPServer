package Implementations;

import Configs.ReadableHttpResponse;
import Exceptions.HttpServerExceptions;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

class DefaultReadableHttpResponse implements ReadableHttpResponse {
    private final Map<String, String> headers;
    private byte[] body;
    private int status;

    protected DefaultReadableHttpResponse() {
        this.headers = new LinkedHashMap<>(  );
        this.body = new byte[0];
        this.status = 200;
    }


    @Override
    public int getStatus() { return status; }

    @Override
    public Map<String, String> getHeaders() { return headers; }

    @Override
    public byte[] getBody() { return body; }

    @Override
    public int getBodyLength() { return body.length; }

    @Override
    public void setStatus(int status) { this.status = status; }

    @Override
    public void setHeader(String name, Object value) {

        Objects.requireNonNull(name, "Name cant be null");
        Objects.requireNonNull( value, "Value cant be null" );
        name = parseHeadersKey( name );
        if(value instanceof Date)
            headers.put( name, new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" ).format( value ) );
        else  if(value instanceof FileTime)
            headers.put( name, new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" ).format( new Date(((FileTime)value ).toMillis())) );
        else
            headers.put( name, String.valueOf(value) );
    }

    @Override
    public void setBody(String content) {
        Objects.requireNonNull( content, "Content cant be null" );
        this.body = content.getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    public void setBody(InputStream in) {
        try {
            Objects.requireNonNull( in, "Content cant be null" );
            this.body = IOUtils.toByteArray( in );
        } catch (IOException e) {
            throw new HttpServerExceptions( "Cant set http response from reader: " + e.getMessage(), e );
        }
    }

    @Override
    public void setBody(Reader reader) {
        try {
            Objects.requireNonNull( reader, "Content cant be null" );
            this.body = IOUtils.toByteArray( reader, StandardCharsets.UTF_8 );
        } catch (IOException e) {
            throw new HttpServerExceptions( "Cant set http response from reader: " + e.getMessage(), e );
        }
    }

    @Override
    public boolean isBodyEmpty() {
        return getBodyLength() == 0;
    }

    private String parseHeadersKey(String inputKey) {
        inputKey = inputKey.toLowerCase();
        String[] inputKeyArr = inputKey.split( "-" );
        StringBuilder correctKey = new StringBuilder(  );
        for (String key : inputKeyArr){
            char[] keyCharArr = key.toCharArray();
            keyCharArr[0] = Character.toUpperCase( keyCharArr[0]);
            key = new String( keyCharArr );
            correctKey.append( key ).append( "-" );
        }
        correctKey.replace( correctKey.length() - 1, correctKey.length(), "" );
        return correctKey.toString();
    }



}
