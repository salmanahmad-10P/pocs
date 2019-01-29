import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/*
    -- javac -cp /usr/share/java/apache-commons-io.jar:/usr/share/java/apache-commons-compress.jar TarGZTest.java
    -- java -cp  /usr/share/java/apache-commons-io.jar:/usr/share/java/apache-commons-compress.jar:. TarGZTest /home/jbride/javaTests
*/
public class TarGZTest {

    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        File outFile = new File(filePath+".tar.gz");

        TarArchiveOutputStream tOut = null;
        try {
            tOut = new TarArchiveOutputStream(
                new GzipCompressorOutputStream(
                    new BufferedOutputStream(new FileOutputStream(outFile))));

            addFileToTarGz(tOut, filePath, "");
        } finally {
            if(tOut != null) {
                tOut.finish();
                tOut.close();
                System.out.println("main() just wrote following file = "+outFile+" : of following size = "+outFile.length());
            }
        }
    }

    private static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base) throws IOException {
        System.out.println("addFileToTarGz() path = "+path+" : base = "+base);
        File f = new File(path);
        String entryName = base + f.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);

        tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        tOut.putArchiveEntry(tarEntry);

        if (f.isFile()) {
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            File[] children = f.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }
}
