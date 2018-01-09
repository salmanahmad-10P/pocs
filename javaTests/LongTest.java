public class LongTest {

    public static void main(String args[]) {
        long smallLong = 1484165145;
        String tString = Long.toString(smallLong); 
        long largeLong = Long.parseLong(tString + "00");
        long rlLong = Long.rotateLeft(smallLong, 3);
        long rrLong = Long.rotateRight(smallLong, 3);

        System.out.println("main() \n"+smallLong+"\n"+largeLong+"\n"+rlLong+"\n" + rrLong+"\n"+tString);
    }
}
