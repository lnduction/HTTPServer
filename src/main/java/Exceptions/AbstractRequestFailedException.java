package Exceptions;

public abstract class AbstractRequestFailedException extends HttpServerExceptions{

    private static final long serialVersionUIFD = Long.MIN_VALUE;
    private final String startingLine;

    public AbstractRequestFailedException(String message, String startingLine) { super( message );  this.startingLine = startingLine; }

    public AbstractRequestFailedException(String message, Throwable cause,  String startingLine) { super( message, cause ); this.startingLine = startingLine; }

    public AbstractRequestFailedException(Throwable cause,  String startingLine) { super( cause );  this.startingLine = startingLine; }

    public String getStartingLine() { return startingLine; }
}
