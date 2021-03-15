import Handler.ServerInfoHttpHandler;
import Handler.TestJDBCHandler;
import Implementations.DefaultHttpHandler;
import Implementations.HttpServerFactory;
import Server.HandlerConfig;
import Server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class main {

    private static final Logger LOGGER = LoggerFactory.getLogger(main.class);
    private static final List<String> QUIT_CMDS = Collections.unmodifiableList(Arrays.asList( "q", "quit", "exit" ));
    public static void main(String[] args) {
        Thread.currentThread().setName("CLI-main thread");
        try {
            HttpServerFactory httpServerFactory = HttpServerFactory.create();
            HttpServer httpServer = httpServerFactory.createHttpServer(getHandlerConfig(),null);
            httpServer.start();
            waitForStopCommand(httpServer);
        } catch(Exception e) {
            LOGGER.error("Can't execute cmd: "+e.getMessage(), e);
        }
    }

    private static void waitForStopCommand(HttpServer httpServer) {
        try(Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
            while(true) {
                String cmd = scanner.nextLine();
                if(QUIT_CMDS.contains(cmd.toLowerCase())) {
                    httpServer.stop();
                    break;
                } else {
                    LOGGER.error("Unsupported cmd: "+cmd+". To shutdown server please type: q");
                }
            }
        }
    }

    private static HandlerConfig getHandlerConfig() {
        return new HandlerConfig()
                .addHandler("/info", new ServerInfoHttpHandler())
                .addHandler("/jdbc", new TestJDBCHandler());
    }
    }