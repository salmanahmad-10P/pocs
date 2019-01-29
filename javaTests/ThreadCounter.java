import java.util.concurrent.atomic.AtomicInteger;

public class ThreadCounter extends Thread {
    private static final AtomicInteger count = new AtomicInteger();
    
    public static void main(String[] args) {
        while (true)
            (new ThreadCounter()).start();
            
    }

    @Override
    public void run() {
        System.out.println(count.incrementAndGet());
        
        while (true)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
    }
}
