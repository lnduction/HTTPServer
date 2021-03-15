package Exceptions;

public class BadRequestExceptions extends  AbstractRequestFailedException{

    private static final long serialVersionUIFD = Long.MIN_VALUE;

    public BadRequestExceptions(String message, Throwable cause, String startingLine) {
        super( message, cause, startingLine );
        setStatusCode( 400 );
    }
}
