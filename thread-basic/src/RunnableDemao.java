public class RunnableDemao {
    public static int count = 0;

    static class MyTask implements Runnable {

        @Override
        public void run() {
            count++;
            System.out.println(Thread.currentThread().getName()+"执行");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask task = new MyTask();

        Thread thread1 = new Thread(task);
        thread1.start();
        thread1.join();
        System.out.println(count);

        Thread thread2 = new Thread(task);
        thread2.start();
        thread2.join();
        System.out.println(count);
    }
}
