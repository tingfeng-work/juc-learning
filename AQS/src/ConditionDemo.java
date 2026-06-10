import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class ConditionDemo {
    static class MyBlockingQueue {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity = 5;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();


        public void put(Integer value) throws InterruptedException {
            lock.lock();
            try {
                // 1. 如果队列满了，让生产者等待 notFull
                while (queue.size() == capacity) {
                    notFull.await();
                }
                // 2. 队列没满，把 value 放入 queue
                queue.add(value);
                // 3. 打印：哪个线程生产了什么，当前队列大小是多少
                System.out.println(Thread.currentThread().getName()
                        + " 生产了: " + value
                        + "，当前队列大小: " + queue.size());
                // 4. 生产完后，唤醒等待 notEmpty 的消费者
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public Integer take() throws InterruptedException {
            lock.lock();
            try {
                // 1. 如果队列空了，让消费者等待 notEmpty
                while (queue.isEmpty())
                    notEmpty.await();
                // 2. 队列不空，取出一个元素
                Integer result = queue.poll();
                // 3. 打印：哪个线程消费了什么，当前队列大小是多少
                System.out.println(Thread.currentThread().getName()
                        + " 消费了: " + result
                        + "，当前队列大小: " + queue.size());
                // 4. 消费完后，唤醒等待 notFull 的生产者
                notFull.signal();
                // 5. 返回取出的元素
                return result;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        MyBlockingQueue queue = new MyBlockingQueue();
        new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                try {
                    queue.put(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Producer-1").start();

        new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                try {
                    Integer take = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Consumer-1").start();
    }
}
