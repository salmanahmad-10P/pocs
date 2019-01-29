import java.io.BufferedReader;
import java.io.FileReader;

public class cpuinfo {

    public static void main(String args[]) {
        BufferedReader statReader  = null;
        int[] previousArray = new int[4];
        try {
            while(true) {
                statReader = new BufferedReader(new FileReader("/proc/stat"));
                String sumString = statReader.readLine();
                statReader.close();
                String[] sumStringArray = sumString.split(" ");
                System.out.println("sumStringArray size = "+sumStringArray.length);
                int[] currentArray = new int[4];
                for(int r = 2; r < 6; r++) {
                    System.out.println("sumStringArray[r] = "+sumStringArray[r]);
                    //currentArray[r-2] = Long.getLong(sumStringArray[r]).longValue();
                    currentArray[r-2] = Integer.valueOf(sumStringArray[r]);
                }
                double cpuUtilization = calculateCpuUtilization(previousArray, currentArray);
                System.out.println("cpu utilization = "+cpuUtilization);
                previousArray = currentArray;
                Thread.sleep(5000);
            }
        } catch(Throwable x) {
            x.printStackTrace();
        } finally {
            try {
                if(statReader != null) {
                    statReader.close();
                }
            } catch(Exception y) {
                y.printStackTrace();
            }
        }
    }

    private static double calculateCpuUtilization(int[] a, int[] b) {
        int utilization = (b[0]-a[0])+(b[1]-a[1])+(b[2]-a[2]);
        int totalTime = utilization + (b[3]-a[3]);
        return (100*utilization)/totalTime;
    }

}
