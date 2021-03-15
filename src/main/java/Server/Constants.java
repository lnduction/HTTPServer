package Server;

import java.util.*;

public final class Constants {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String HEAD = "HEAD";
    public static final String HTTP_1_1 = "HTTP/1.1";
    public static final List<String > ALLOWED_METHODS = Collections.unmodifiableList( Arrays.asList( GET, POST, HEAD ));
    public static final List<String > SUPPORTED_VERSIONS = Collections.unmodifiableList( Collections.singletonList( HTTP_1_1 ) );
    public static final String defaultVersion = HTTP_1_1;


    private Constants() {

    }
}
