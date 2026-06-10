public class ThreadCreateDemo {

    static class MyThread extends Thread{
        public void run(){
            String name = Thread.currentThread().getName();
            for (int i = 1; i <= 10; i++) {
                System.out.println(name+"执行第 "+i+" 次循环");
            }

        }
    }
    public static void main(String[] args) throws InterruptedException {

        MyThread thread1 = new MyThread();
        MyThread thread2 = new MyThread();


        thread1.start();
        Thread.sleep(5);
        thread1.start();

    }
}
