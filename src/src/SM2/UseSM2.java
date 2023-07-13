package src.SM2;
import java.util.Base64;

import static src.SM2.SM2Util.*;
import static src.SM2.SM2tools.*;

public class UseSM2 {

    static String[] key;

    static {
        try {
            key = generateSmKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String srcPublicKey=key[0];
    public static String srcPrivateKey=key[1];

    public String outPutPubKey(){
        return srcPublicKey;
    }
    public String outPutPriKey() {return srcPrivateKey;}

    public static String encryptStr(String outStr,String otherPubKey){
        String outStr64 = new String(Base64.getEncoder().encode(outStr.getBytes()));
        byte[] BoutStr64 = Base64.getDecoder().decode(outStr64);

        byte[] encode = encrypt(BoutStr64, createPublicKey(otherPubKey));

        return Base64.getEncoder().encodeToString(encode);
    }
    public static String decryptStr(String str,String key) {

        byte[] btCode = Base64.getDecoder().decode(str);

        byte[] BsrcText = decrypt(btCode, createPrivateKey(key));

        String srcText64 = Base64.getEncoder().encodeToString(BsrcText);

        return new String(Base64.getDecoder().decode(srcText64));
    }

}


