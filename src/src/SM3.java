package src;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import java.util.Arrays;

public class SM3 {
    private static final String ENCODING = "utf-8";
    public static String encrypt(String src) {
        return ByteUtils.toHexString(getEncryptBySrcByte(src.getBytes()));
    }


    public static byte[] getEncryptBySrcByte(byte[] srcByte) {
        SM3Digest sm3 = new SM3Digest();
        sm3.update(srcByte, 0, srcByte.length);
        byte[] encryptByte = new byte[sm3.getDigestSize()];
        sm3.doFinal(encryptByte, 0);
        return encryptByte;
    }
    public static boolean verify(String src, String sm3HexStr) throws Exception {
        byte[] sm3HashCode = ByteUtils.fromHexString(sm3HexStr);
        byte[] newHashCode = getEncryptBySrcByte(src.getBytes(ENCODING));
        return Arrays.equals(newHashCode, sm3HashCode);
    }
    public static void main(String[] args) throws Exception {
        String srcStr = "123456";

        String hexStrNoKey = SM3.encrypt(srcStr);
        System.out.println(hexStrNoKey);//密文
        System.out.println("" + SM3.verify(srcStr, hexStrNoKey));

    }


}
