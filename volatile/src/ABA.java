import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABA {
    static AtomicInteger count = new AtomicInteger(100);
    static AtomicStampedReference<Integer> ref =
            new AtomicStampedReference<>(100, 1);

    public static void main(String[] args) {
        new Thread(
                () -> {
                    int stamp = ref.getStamp();
                    Integer expect = ref.getReference(); // 读到100

                    // 等待t2完成ABA
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    boolean success = ref.compareAndSet(expect, 200, stamp, stamp + 1);

                    System.out.println("t1 CAS结果：" + success);
                    System.out.println("最终值：" + ref.getReference());
                    System.out.println("最终版本号：" + ref.getStamp());
                }
        ).start();
        new Thread(
                () -> {
                    int stamp1 = ref.getStamp();
                    ref.compareAndSet(100, 101, stamp1, stamp1 + 1);
                    System.out.println("t2: 100 -> 101, stamp=" + ref.getStamp());
                    int stamp2= ref.getStamp();
                    ref.compareAndSet(101, 100, stamp2, stamp2 + 1);
                    System.out.println("t2: 101 -> 100, stamp=" + ref.getStamp());
                }
        ).start();
    }
}
