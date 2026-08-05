package com.printxpress.app.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DbExecutor {
    private static final ExecutorService IO = Executors.newFixedThreadPool(3);

    private DbExecutor() {}

    public static ExecutorService io() {
        return IO;
    }
}
