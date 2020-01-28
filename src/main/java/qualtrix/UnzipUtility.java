package qualtrix;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class UnzipUtility {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts a zip file specified by a byte output stream and return an output stream
     * of the first file in the zip
     * @param inputStream
     * @throws IOException
     */
    public static ByteArrayOutputStream unzip(InputStream inputStream) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(inputStream);
        // Only get the first entry we only every expect one
        ZipEntry entry = zipIn.getNextEntry();
        ByteArrayOutputStream ret = extractFile(zipIn);
        zipIn.closeEntry();
        zipIn.close();
        return ret;
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @throws IOException
     */
    private static ByteArrayOutputStream extractFile(ZipInputStream zipIn) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(out);
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        return out;
    }
}
