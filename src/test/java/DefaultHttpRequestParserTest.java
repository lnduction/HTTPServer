import Exceptions.BadRequestExceptions;
import Exceptions.HttpVersionNotSupportedException;
import Exceptions.MethodNotAllowedException;
import Implementations.DefaultHttpRequestParser;
import Server.HttpRequest;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DefaultHttpRequestParserTest {
    private DefaultHttpRequestParser defaultHttpRequestParser;

    @Before
    public void before() {
        defaultHttpRequestParser = new DefaultHttpRequestParser();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private InputStream getClassPathResourceStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

    @Test
    public void testGetSimple() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("get-simple.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("GET", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());

            assertEquals("localhost", request.getHeaders().get("Host"));
            assertEquals("Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5",
                    request.getHeaders().get("User-Agent"));
            assertEquals("text/html", request.getHeaders().get("Accept"));
            assertEquals("close", request.getHeaders().get("Connection"));

            assertTrue(request.getParameters().isEmpty());

            assertEquals("localhost", request.getRemoteAddress());
        }
    }

    @Test
    public void testGetHeadersCaseUnsensitive() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("get-headers-case-unsensitive.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("localhost", request.getHeaders().get("Host"));
            assertEquals("Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5",
                    request.getHeaders().get("User-Agent"));
            assertEquals("text/html", request.getHeaders().get("Accept"));
            assertEquals("close", request.getHeaders().get("Connection"));
        }
    }

    @Test
    public void testGetHeadersNewLine() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("get-headers-new-line.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("localhost", request.getHeaders().get("Host"));
            assertEquals("text/html;charset=windows-1251", request.getHeaders().get("Content-Type"));
            assertEquals("text/html", request.getHeaders().get("Accept"));
        }
    }

    @Test
    public void testInvalidHttpVersion() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("get-invalid-http-version.txt")) {
            thrown.expect( HttpVersionNotSupportedException.class);
            thrown.expectMessage(new IsEqual<String>("Current server supports only HTTP/1.1 protocol, current protocol is HTTP/1.2"));

            defaultHttpRequestParser.parserHttpRequest(httpMessage, "");
        }
    }

    @Test
    public void testHeadSimple() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("head-simple.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("HEAD", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());
        }
    }

    @Test
    public void testGetWithSimpleParams() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("get-with-simple-params.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("GET", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());

            assertEquals(2, request.getParameters().size());
            assertEquals("value1", request.getParameters().get("param1"));
            assertEquals("true", request.getParameters().get("param2"));
        }
    }

    @Test
    public void testGetWithDuplicateParams() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("get-with-duplicate-params.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("GET", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());

            assertEquals(2, request.getParameters().size());
            assertEquals("value1,value2,value1", request.getParameters().get("param1"));
            assertEquals("true", request.getParameters().get("param2"));
        }
    }

    @Test
    public void testGetWithDecodedParams() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("get-with-decoded-params.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("GET", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());

            assertEquals(6, request.getParameters().size());
            assertEquals("welcome@devstudy.net", request.getParameters().get("email"));
            assertEquals("", request.getParameters().get("password"));
            assertEquals("5", request.getParameters().get("number"));
            assertEquals("test&qwerty?ty=u", request.getParameters().get("p"));
            assertEquals("Simple Text", request.getParameters().get("text"));
            assertEquals("http://devstudy.net", request.getParameters().get("url"));
        }
    }

    @Test
    public void testPostSimple() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("post-simple.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("POST", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());

            assertEquals(5, request.getParameters().size());
            assertEquals("welcome@devstudy.net", request.getParameters().get("email"));
            assertEquals("", request.getParameters().get("password"));
            assertEquals("5", request.getParameters().get("number"));
            assertEquals("Simple Text", request.getParameters().get("text"));
            assertEquals("http://devstudy.net", request.getParameters().get("url"));
        }
    }

    @Test
    public void testPostWithEmptyBody() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("post-with-empty-body.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("POST", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());

            assertEquals(0, request.getParameters().size());
        }
    }

    @Test
    public void testPostWithEmptyBodyWithoutContentLength() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream(
                "post-with-empty-body-and-without-content-length.txt")) {
            HttpRequest request = defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
            assertEquals("POST", request.getMethod());
            assertEquals("/index.html", request.getUri());
            assertEquals("HTTP/1.1", request.getHttpVersion());

            assertEquals(0, request.getParameters().size());
        }
    }

    @Test
    public void testRuntimeException() throws IOException {
        thrown.expect( BadRequestExceptions.class);
        thrown.expectMessage(new IsEqual<String>("Can't parse http request: null"));

        defaultHttpRequestParser.parserHttpRequest(null, "localhost");
    }

    @Test
    public void testMethodNotAllowedException() throws IOException {
        try (InputStream httpMessage = getClassPathResourceStream("method-not-allowed.txt")) {
            thrown.expect( MethodNotAllowedException.class);
            thrown.expectMessage(new IsEqual<String>("Only [GET, POST, HEAD] are supported. Current method is PUT"));

            defaultHttpRequestParser.parserHttpRequest(httpMessage, "localhost");
        }
    }
}