package Exceptions;

public class HttpServerExceptions extends RuntimeException {

    private static final long serialVersionUIFD = Long.MIN_VALUE;
    private int statusCode = 500;

    public HttpServerExceptions(String message) { super( message ); }

    public HttpServerExceptions(String message, Throwable cause) { super( message, cause ); }

    public HttpServerExceptions(Throwable cause) { super( cause ); }

    public int getStatusCode() { return statusCode; }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
}
