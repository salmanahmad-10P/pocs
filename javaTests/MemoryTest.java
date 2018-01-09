public class MemoryTest {

    public static void main(String args[]) {
        System.out.println("Memory: " + Runtime.getRuntime().maxMemory()/1024/1024);

        System.out.println("CPUs: " + Runtime.getRuntime().availableProcessors());
    }
}
