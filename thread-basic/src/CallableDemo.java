import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableDemo implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum = sum + i;
        }
        Thread.sleep(3000);
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CallableDemo task = new CallableDemo();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(task);
        Thread thread = new Thread(futureTask);
        thread.start();
        System.out.println(futureTask.get());

    }
}
