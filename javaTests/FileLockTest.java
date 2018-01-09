import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.MappedByteBuffer;
import java.io.RandomAccessFile;

/**
 * purpose :  test exclusive lock functionality provided by a shared storage environment to support Hornetq HA
 * usage:  mount shared storage on two or more operating systems, and run this compiled program on each of those OSs as follows:
 *     java -DfilePath=/path/to/mounted/shared/storage/fileLockTest.txt -DlockTimeMillis=12000 FileLockTest
 *
 * desired resulsts:
 *   1)  only one of these FileLockTest program should create the file on shared storage if it doesn't already exist
 *   2)  all FileLockTest JVMs from each operating system should be able to read/write to that file on shared storage
 *   3)  only one FileLockTest program should obtain the lock 
 *
 */
public class FileLockTest {
    private static final byte FIRST_TIME_START = '0';
    private static String filePath;
    private static int lockTimeMillis;
    private static FileChannel fc;

    public static void main(String[] args) {
        filePath = (String)System.getProperty("filePath", "/tmp/fileLockTest.txt");
        lockTimeMillis = Integer.parseInt(System.getProperty("lockTimeMillis", "10000")); 

        RandomAccessFile in;
        try {
            File f= new File(filePath);
            if(f.exists())
                System.out.println("1)  the following file already exists : "+filePath);

            in = new RandomAccessFile(f, "rw");
            fc = in.getChannel();

            System.out.println("2)  about to attempt to lock file");
            FileLock fl = fc.tryLock(0, 4, false);
            if(fl != null){
                if(fl.isShared())
                    System.out.println("3)  just locked file. lock type is SHARED");
                else
                    System.out.println("3)  just locked file. lock type is EXCLUSIVE");
                writeToFile();
                if(lockTimeMillis > 0) {
                    System.out.println("4)  sleeping for the following # of millis "+lockTimeMillis);
                    Thread.sleep(lockTimeMillis);
                }
                fl.release();
                System.out.println("5)  just released lock");
            } else {
                System.out.println("6)  file is already locked!");
            }
    
            fc.close();
            in.close();
        }catch(Exception x) {
            x.printStackTrace();
        }
    }

    private static void writeToFile() throws java.io.IOException {
        ByteBuffer id = ByteBuffer.allocateDirect(3);
        byte[] bytes = new byte[3];
        bytes[0] = FIRST_TIME_START;
        bytes[1] = FIRST_TIME_START;
        bytes[2] = FIRST_TIME_START;
        id.put(bytes, 0, 3);
        id.position(0);
        fc.write(id, 0);
        fc.force(true);
    }

}
