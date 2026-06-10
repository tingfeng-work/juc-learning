import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {
    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static final Lock readLock = rwLock.readLock();

    private static final Lock writeLock = rwLock.writeLock();

    private static int data = 0;

    private static void read() {
        /*
        *1. readLock.lock()
        2. 打印：线程名 + 开始读 + 当前 data
        3. sleep 1000ms
        4. 打印：线程名 + 结束读
        5. finally 释放 readLock
        * */
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始读" + data);
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "结束读");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            readLock.unlock();
        }
    }

    private static void write() {
        /*
        * 1. 获取 writeLock
        2. 打印：
        线程名 + 开始写
        3. data++
        4. sleep 1000ms
        5. 打印：
        线程名 + 写入后的 data
        6. finally 释放 writeLock
        * */
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始写");
            data++;
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "完成写，写后的data:" + data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        System.out.println("读写互斥验证============");
        new Thread(() -> read(), "Reader-1").start();
        new Thread(() -> write(), "Writer-1").start();


    }
}
