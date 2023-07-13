package src;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

/**
 * 文件处理工具类
 *
 * @author wujie
 */
public class FileUtils {

    /**
     * 获得指定文件的byte数组
     *
     * @param filePath 文件路径
     * @return 字节数组
     */
    public static byte[] fileToByte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     *
     * @param multipartFile 真实文件
     * @return byte[] 数组
     */
    public static byte[] multipartFileToByte(MultipartFile multipartFile) throws Exception {
        byte[] buffer = null;
        File file = multipartFileToFile(multipartFile);
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //清除缓存文件
            delteTempFile(file);
        }
        return buffer;
    }



    /**
     * 根据byte数组，生成文件
     * @param bfile 字节数组
     * @param filePath 文件路径
     * @param fileName 文件名
     */
    public static void byteToFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(fos);
        }
    }

    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }
}




