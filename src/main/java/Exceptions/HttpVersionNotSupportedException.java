package Exceptions;

import Server.Constants;

public class HttpVersionNotSupportedException extends AbstractRequestFailedException {

    private static final long serialVersionUIFD = Long.MIN_VALUE;

    public HttpVersionNotSupportedException(String httpVersion, String startingLine) {
        super( "Current server supports only " + Constants.HTTP_1_1 + " protocol, current protocol is " + httpVersion, startingLine );
        setStatusCode(505);
    }
}
