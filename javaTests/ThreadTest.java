public class ThreadTest extends Thread {

    private static int sleepTime = 60000;
    private static int stackSize = 5;
    private static boolean killThread = false;

    public static void main(String args[]) {
        int numberThreads = 1;
        int t = 0;
        try {
            numberThreads = (System.getProperty("numberThreads") != null) ? Integer.parseInt(System.getProperty("numberThreads")) : numberThreads;
            sleepTime = (System.getProperty("sleepTime") != null) ? Integer.parseInt(System.getProperty("sleepTime")) : sleepTime;
            stackSize = (System.getProperty("stackSize") != null) ? Integer.parseInt(System.getProperty("stackSize")) : stackSize;
            for(t=0; t < numberThreads; t++) {
                ThreadTest newThread = new ThreadTest();
                newThread.start();
                System.out.println("main() number of threads = "+t);
            }
            System.out.println("main() # of threads instantiated = "+t+"    :    press <enter> to end test ");
            System.in.read();
            killThread = true;
        } catch (Exception x) {
            System.out.println("main() # of threads instantiated = "+t+" :   exception = "+x.getLocalizedMessage());
        } catch (java.lang.OutOfMemoryError x) {
            System.out.println("main() # of threads instantiated = "+t+" :   exception = "+x.getLocalizedMessage());
            Runtime.getRuntime().exit(0);
        }
    }

    public void run() {
        try {
            loop(0);
            while(!killThread) {
                Thread.sleep(2000);
            }
        } catch(Exception x) {
            System.out.println("sleep() exception = "+x.getLocalizedMessage());
        }
    }

    public void loop(int counter) {
        if(counter < stackSize) {
            counter = counter +1;
            loop(counter);
        }
    }
}
