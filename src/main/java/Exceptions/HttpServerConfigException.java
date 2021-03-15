package Exceptions;

public class HttpServerConfigException extends HttpServerExceptions {

    private static final long serialVersionUIFD = Long.MIN_VALUE;

    public HttpServerConfigException(String message) {
        super( message );
    }

    public HttpServerConfigException(String message, Throwable cause) {
        super( message, cause );
    }

    public HttpServerConfigException(Throwable cause) {
        super( cause );
    }
}
