import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalDemo {
    private static final ExecutorService pool = Executors.newFixedThreadPool(1);

    private static final ThreadLocal<byte[]> threadlocal = new ThreadLocal<>();

    private static final int TASK_COUNT = 100;

    private static void noRemoveTest() {
        for (int i = 0; i < TASK_COUNT; i++) {
            pool.execute(() -> {
                ThreadLocal<byte[]> local = new ThreadLocal<>();

                byte[] bytes = new byte[1024 * 1024 * 20];

                local.set(bytes);
                Runtime runtime = Runtime.getRuntime();

                long used = runtime.totalMemory() - runtime.freeMemory();

                System.out.println(Thread.currentThread().getName()
                        + " used memory: " + used / 1024 / 1024 + " MB");
            });
        }
    }

    private static void removeTest() {
        for (int i = 0; i < TASK_COUNT; i++) {
            pool.execute(() -> {
                ThreadLocal<byte[]> local = new ThreadLocal<>();

                byte[] bytes = new byte[1024 * 1024 * 5];

                local.set(bytes);
                Runtime runtime = Runtime.getRuntime();

                long used = runtime.totalMemory() - runtime.freeMemory();

                System.out.println(Thread.currentThread().getName()
                        + " used memory: " + used / 1024 / 1024 + " MB");
            });
        }
    }

    public static void main(String[] args) {
        removeTest();
        pool.shutdown();
    }
}
