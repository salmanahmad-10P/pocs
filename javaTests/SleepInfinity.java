public class SleepInfinity {
    public static void main(String args[]) {
        try {
            while(true) {
                Thread.sleep(100000);
            }
        } catch(Exception x) {
            x.printStackTrace();
        }
    }
}
