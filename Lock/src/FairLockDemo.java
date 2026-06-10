import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class FairLockDemo {
    private static void testLock(ReentrantLock lock) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            int threadNo = i;

            new Thread(() -> {
                try {
                    for (int j = 0; j < 3; j++) {
                        lock.lock();
                        try {
                            System.out.println(Thread.currentThread().getName()
                                    + " 第 " + j + " 次抢到锁");
                            Thread.sleep(100);
                        } finally {
                            lock.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }, "T-" + threadNo).start();
        }

        latch.await();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("===== 非公平锁 =====");
        testLock(new ReentrantLock(false));

        System.out.println("===== 公平锁 =====");
        testLock(new ReentrantLock(true));
    }
}
