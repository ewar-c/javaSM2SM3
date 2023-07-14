package src.SM2;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import src.FileUtils;

/**
 * SM2 国密算法 工具类
 */
public class FileSM2 {

    /**
     * SM2 文件加密
     *  @param publicKey  公钥
     * @param dataBytes  提交的原始文件以流的形式
     * @param outputPath 输出的加密文件路径
     * @param fileName   输出的加密文件名称
     */
    public static void lockFile(String publicKey, byte[] dataBytes, String outputPath, String fileName) throws Exception {
        if (StringUtils.isEmpty(publicKey) || null == dataBytes ||
                StringUtils.isEmpty(outputPath) || StringUtils.isEmpty(fileName)) {
            throw new Exception("缺少必要参数!");
        } else {
            long startTime = System.currentTimeMillis();
            // 初始化SM2对象
            try {
                SM2 SM_2 = new SM2(null, publicKey);
                byte[] data;
                data = SM_2.encrypt(dataBytes, KeyType.PublicKey);
                FileUtils.byteToFile(data, outputPath, fileName);
            } catch (Exception e) {
                System.out.println("Exception | " + e);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("本次加密操作,所耗时间为：" + (endTime - startTime));
        }
    }

    /**
     * SM2 文件解密
     *  @param privateKey   私钥
     * @param lockFilePath 加密文件路径
     * @param outputPath   输出的解密文件路径
     * @param fileName     输出的解密文件名称
     */
    public static void unlockFile(String privateKey, String lockFilePath, String outputPath, String fileName) throws Exception {
        if (StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(lockFilePath) || StringUtils.isEmpty(fileName)) {
            throw new Exception("缺少必要参数!");
        } else {
            long startTime = System.currentTimeMillis();
            try {
                // 初始化SM2对象
                SM2 SM_2 = new SM2(privateKey, null);
                byte[] bytes = FileUtils.fileToByte(lockFilePath);
                byte[] data;
                data = SM_2.decrypt(bytes, KeyType.PrivateKey);
                FileUtils.byteToFile(data, outputPath, fileName);
            } catch (Exception e) {
                System.out.println("Exception | " + e);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("本次解密操作,所耗时间为：" + (endTime - startTime));
        }
    }

}

