package Exceptions;

import Server.Constants;

public class MethodNotAllowedException extends AbstractRequestFailedException {

    private static final long serialVersionUIFD = Long.MIN_VALUE;

    public MethodNotAllowedException(String method, String startingLine) {
        super( "Only " + Constants.ALLOWED_METHODS + " are supported. Current method is " + method, startingLine );
        setStatusCode(405);
    }
}
