package Implementations;

import java.util.concurrent.ThreadFactory;

public class DefaultThreadFactory implements ThreadFactory {
    private String name;
    private int count;

    DefaultThreadFactory() {
        super();
        count = 1;
        name = "executor-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread th = new Thread(r, name + (count++));
        th.setDaemon(false);
        th.setPriority(8);
        return th;
    }
}
