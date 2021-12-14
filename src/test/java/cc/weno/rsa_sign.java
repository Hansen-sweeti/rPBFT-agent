 package cc.weno;

 import cn.hutool.crypto.SecureUtil;
 import cn.hutool.crypto.asymmetric.Sign;
 import cn.hutool.crypto.asymmetric.SignAlgorithm;
 import org.junit.Test;
 import cn.hutool.crypto.asymmetric.RSA;

 import java.io.UnsupportedEncodingException;
 import java.nio.charset.StandardCharsets;


 public class Rsa_sign {


     public  String parseByte2HexStr(byte[] buf) {
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < buf.length; i++) {
             String hex = Integer.toHexString(buf[i] & 0xFF);
             if (hex.length() == 1) {
                 hex = '0' + hex;
             }
             sb.append(hex.toUpperCase());
         }
         return sb.toString();
     }
     public static byte[] parseHexStr2Byte(String hexStr) {
         if (hexStr.length() < 1)
             return null;
         byte[] result = new byte[hexStr.length() / 2];
         for (int i = 0; i < hexStr.length() / 2; i++) {
             int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
             int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
             result[i] = (byte) (high * 16 + low);
         }
         return result;
     }

     @Test
    public  void  testHollo() throws UnsupportedEncodingException {
         // 1.生成公钥何时要
         RSA rsa = new RSA();
         String pri_key=rsa.getPrivateKeyBase64();
         String pub_key=rsa.getPublicKeyBase64();


         Sign sign1 = SecureUtil.sign(SignAlgorithm.MD5withRSA,pri_key,null);
         // String cer=selfRsa.encryptBase64(pub_key, KeyType.PrivateKey);
         byte[] cer=sign1.sign(pub_key.getBytes());
         System.out.println(pub_key);
         String temp= parseByte2HexStr(cer);
         System.out.println(temp);

         byte[] res=parseHexStr2Byte(temp);
//         System.out.println(temp.getBytes());
         // 验证
         Sign sign2 = SecureUtil.sign(SignAlgorithm.MD5withRSA,null,pub_key);

         boolean verify = sign2.verify(pub_key.getBytes(),res);
         System.out.println(verify);
     }


    
 }
