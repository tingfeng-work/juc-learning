import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAdder;

public class Demo3 {
    static LongAdder count  = new LongAdder();


    static class MyTask implements Runnable {
        public void run() {
            for (int i = 0; i < 100000; i++) {
                count.add(1);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask myTask = new MyTask();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(myTask);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("理论结果：" + 100 * 100000);
        System.out.println("实际结果：" + count);
    }
}
