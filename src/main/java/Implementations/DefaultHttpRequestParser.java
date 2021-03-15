package Implementations;

import Configs.HttpRequestParser;
import Exceptions.BadRequestExceptions;
import Exceptions.HttpServerExceptions;
import Exceptions.HttpVersionNotSupportedException;
import Exceptions.MethodNotAllowedException;
import Server.Constants;
import Server.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

 public class DefaultHttpRequestParser implements HttpRequestParser {

     private static final Logger LOGGER = LoggerFactory.getLogger( DefaultHttpRequestParser.class );


     @Override
     public HttpRequest parserHttpRequest(InputStream inputStream, String remoteAddress) throws IOException, HttpServerExceptions {
         List<String> inputData;
         try {
             inputData = readStream( inputStream );
         } catch (RuntimeException e) {
             if (e instanceof HttpServerExceptions) {
                 throw e;
             } else {
                 throw new BadRequestExceptions( "Can't parse http request: " + e.getMessage(), e, null );
             }
         }

         String startingLine = inputData.get( 0 ).replace( "\r", "" ).replace( "\n", "" );

         if (startingLine.contains( "?" )) {
             String[] dividedStartingLine = startingLine.split( "\\?" );
             String[] params = dividedStartingLine[1].split( " " )[0].split( "&" );
             String method = dividedStartingLine[0].split( " " )[0];
             if (!Constants.ALLOWED_METHODS.contains( method )) {
                 LOGGER.error( "Method is not supported" );
                 throw new MethodNotAllowedException( method, startingLine );
             }
             String uri = dividedStartingLine[0].split( " " )[1];
             String httpVersion = dividedStartingLine[1].split( " " )[1];
             if (!Constants.SUPPORTED_VERSIONS.contains( httpVersion )) {
                 LOGGER.error( "Http version is not supported" );
                 throw new HttpVersionNotSupportedException( httpVersion, startingLine );
             }

             DefaultHttpRequest request = new DefaultHttpRequest( startingLine, method, uri, httpVersion, remoteAddress, new LinkedHashMap<>(), uriDecoder( params ) );

             LOGGER.info( "Request is parsed" );

             return request;
         } else {
             String[] dividedStartingLine = startingLine.split( " " );
             String method = dividedStartingLine[0];
             if (!Constants.ALLOWED_METHODS.contains( method )) {
                 LOGGER.error( "Method is not supported" );
                 throw new MethodNotAllowedException( method, startingLine );
             }
             String uri = dividedStartingLine[1];
             String httpVersion = dividedStartingLine[2];
             if (!Constants.SUPPORTED_VERSIONS.contains( httpVersion )) {
                 LOGGER.error( "Http version is not supported" );
                 throw new HttpVersionNotSupportedException( httpVersion, startingLine );
             }
             inputData.remove( 0 );


             LOGGER.info( "input data is: " + inputData.toString() );
             Map<String, String> headers = new LinkedHashMap<>();

             while (!inputData.get( 0 ).trim().isEmpty()) {
                 String[] headerKeyValue = inputData.get( 0 ).replace( "\r", "" ).replace( "\n", "" ).split( ": " );
                 headers.put( parseHeadersKey( headerKeyValue[0] ), headerKeyValue[1] );

                 if (inputData.get( 1 ).trim().length() != inputData.get( 1 ).replace( "\r", "" ).replace( "\n", "" ).length()) {
                     String continuationOfHeader = inputData.get( 1 ).replace( "\r", "" ).replace( "\n", "" ).replace( " ", "" );
                     headerKeyValue[1] += continuationOfHeader;
                     headers.remove( headerKeyValue[0] );
                     headers.put( parseHeadersKey( headerKeyValue[0] ), headerKeyValue[1] );
                     inputData.remove( 1 );
                 }
                 inputData.remove( 0 );
             }
             LOGGER.info( "Headers is parsed" );
             inputData.remove( 0 );

             Map<String, String> params = new LinkedHashMap<>();
             if ( headers.get( "Content-Length" ) != null && !headers.get( "Content-Length" ).equals( "0" )) {
                 params = readParams( inputStream, Integer.parseInt(headers.get( "Content-Length" )) );
             }

             DefaultHttpRequest request = new DefaultHttpRequest( startingLine, method, uri, httpVersion, remoteAddress, headers, params );

             LOGGER.info( "Request is parsed" );

             return request;
         }
     }
     private Map<String, String> readParams(InputStream inputStream, int contentLength) throws IOException {
         byte[] bytes = new byte[contentLength];
         for (int i = 0; i < contentLength; i++) {
             int read = inputStream.read();
             if (read == -1) break;
             bytes[i] = (byte) read;
         }

         String[] asString = new String(  bytes , StandardCharsets.UTF_8 ).split( "&" );
         return uriDecoder( asString );
     }

     private List<String> readStream(InputStream inputStream) throws IOException {
         ByteArray byteArray = new ByteArray();
         while (true) {
             int read = inputStream.read();
             if (read == -1) return new ArrayList<>();
             byteArray.add( (byte) read );
             if (byteArray.isEmptyLine()) break;
         }
         String asString = new String( byteArray.toArray(), StandardCharsets.UTF_8 );
         LOGGER.info( "Read input stream" );

         return new ArrayList<>( Arrays.asList( asString.split( "\r" ) ) );
     }

     private static class ByteArray {
         private byte[] array;
         private int size;

         ByteArray() {
             array = new byte[1024];
         }
         ByteArray(int contentLength) {
             array = new byte[contentLength];
         }

         public void add(byte value) {
             if (size == array.length) {
                 byte[] temp = array;
                 array = new byte[array.length * 2];
                 System.arraycopy( temp, 0, array, 0, size );
             }
             array[size++] = value;
         }

         public byte[] toArray() {
             return Arrays.copyOf( array, size );
         }

         public boolean isEmptyLine() {
             if (size >= 4) {
                 return array[size - 1] == '\n' && array[size - 2] == '\r'
                         && array[size - 3] == '\n' && array[size - 4] == '\r';
             } else {
                 return false;
             }
         }
     }

     private Map<String, String> uriDecoder(String[] encodedParams) {
         LinkedHashMap<String, String> params = new LinkedHashMap<>();
         try {
             for (String encodedParam : encodedParams) {
                 String decodedParam = java.net.URLDecoder.decode( encodedParam, StandardCharsets.UTF_8.name() );
                 String[] paramKeyValue = decodedParam.split( "=" );
                 if (paramKeyValue.length == 1) paramKeyValue = new String[]{paramKeyValue[0], ""};
                 if (paramKeyValue.length >2) {
                     StringBuilder param = new StringBuilder( paramKeyValue[1] );
                     for (int i = 2; i < paramKeyValue.length; i++) param.append( "=" ).append( paramKeyValue[i] );
                     paramKeyValue = new String[]{paramKeyValue[0], param.toString()};
                 }
                 if (params.get( paramKeyValue[0] ) != null) {

                     paramKeyValue[1] = params.get( paramKeyValue[0] ) + "," + paramKeyValue[1];
                     params.remove( paramKeyValue[0] );
                     params.put( paramKeyValue[0], paramKeyValue[1] );
                 } else params.put( paramKeyValue[0], paramKeyValue[1] );
             }
             LOGGER.info( "Uri is decoded" );
         } catch (Exception e) {
             LOGGER.error( "Cant decoding params: " + e.getMessage() );
             return null;
         }
         return params;
     }

     private Map<String, String> parseParams(String paramsLine, int contentLength) {
         if (paramsLine.length() > contentLength) paramsLine = paramsLine.substring( 0, contentLength );
         LOGGER.info( "Params parsed" );
         return uriDecoder( paramsLine.split( "&" ) );
     }

     private String parseHeadersKey(String inputKey) {
         inputKey = inputKey.toLowerCase();
         String[] inputKeyArr = inputKey.split( "-" );
         StringBuilder correctKey = new StringBuilder();
         for (String key : inputKeyArr) {
             char[] keyCharArr = key.toCharArray();
             keyCharArr[0] = Character.toUpperCase( keyCharArr[0] );
             key = new String( keyCharArr );
             correctKey.append( key ).append( "-" );
         }
         correctKey.replace( correctKey.length() - 1, correctKey.length(), "" );
         return correctKey.toString();
     }
 }

