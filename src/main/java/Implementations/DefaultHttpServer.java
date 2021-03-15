package Implementations;

import Configs.HttpServerConfig;
import Exceptions.HttpServerExceptions;
import Server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class DefaultHttpServer implements HttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpServer.class);
    private final HttpServerConfig httpServerConfig;
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Thread mainServerThread;
    private volatile boolean serverStopped;

     DefaultHttpServer(HttpServerConfig httpServerConfig) {
        super();
        this.httpServerConfig = httpServerConfig;
        this.executorService = createExecutorService();
        this.mainServerThread = createMainServerThread(createServerRunnable());
        this.serverSocket = createServerSocket();
        this.serverStopped = false;
    }

     ServerSocket createServerSocket(){
        try {
            ServerSocket serverSocket = new ServerSocket(httpServerConfig.getServerInfo().getPort());
            serverSocket.setReuseAddress(true);
            return serverSocket;
        } catch (IOException e) {
            throw new HttpServerExceptions("Can't create server socket with port=" + httpServerConfig.getServerInfo().getPort(), e);
        }
    }

     ExecutorService createExecutorService() {
        ThreadFactory threadFactory = httpServerConfig.getWorkerThreadFactory();
        int threadCount = httpServerConfig.getServerInfo().getThreadCount();
        if(threadCount > 0) {
            return Executors.newFixedThreadPool(threadCount, threadFactory);
        } else {
            return Executors.newCachedThreadPool(threadFactory);
        }
    }

     Thread createMainServerThread(Runnable r) {
        Thread th = new Thread(r, "Main Server Thread");
        th.setPriority(Thread.MAX_PRIORITY);
        th.setDaemon(false);
        return th;
    }

     Runnable createServerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                while (!mainServerThread.isInterrupted()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        executorService.submit(httpServerConfig.buildNewHttpClientSocketHandler(clientSocket));
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            LOGGER.error("Can't accept client socket: " + e.getMessage(), e);
                        }
                        destroyHttpServer();
                        break;
                    }
                }
                System.exit(0);
            }
        };
    }

    @Override
    public void start() {
        if (mainServerThread.getState() != Thread.State.NEW) {
            throw new HttpServerExceptions("Current web server already started or stopped! Please create a new http server instance");
        }
        Runtime.getRuntime().addShutdownHook(getShutdownHook());
        mainServerThread.start();
        LOGGER.info("Server started: " + httpServerConfig.getServerInfo());
    }

    @Override
    public void stop() {
        LOGGER.info("Detected stop cmd");
        mainServerThread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.warn("Error during close server socket: " + e.getMessage(), e);
        }
    }

     Thread getShutdownHook() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                if (!serverStopped) {
                    destroyHttpServer();
                }
            }
        }, "ShutdownHook");
    }

     void destroyHttpServer() {
        try {
            httpServerConfig.close();
        } catch (Exception e) {
            LOGGER.error("Close httpServerConfig failed: " + e.getMessage(), e);
        }
        executorService.shutdownNow();
        LOGGER.info("Server stopped");
        serverStopped = true;
    }
}
