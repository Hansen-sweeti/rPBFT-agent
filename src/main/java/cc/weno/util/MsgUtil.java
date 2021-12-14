package cc.weno.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cc.weno.config.AllNodeCommonMsg;
import cc.weno.dao.node.Node;
import cc.weno.dao.pbft.PbftMsg;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼           BUG辟易
 *
 * @author: xiaohuiduan
 * @data: 2020/2/25 下午6:27
 * @description: 进行加密和签名的pbftmsg工具类
 */
@Slf4j
public class MsgUtil {
    /**
     * 直接的RSA
     */
    private static RSA selfRsa = new RSA(Node.getInstance().getPrivateKey(), Node.getInstance().getPublicKey());
    /**
     * 签名
     */
    private static Sign selfSign = SecureUtil.sign(SignAlgorithm.MD5withRSA,Node.getInstance().getPrivateKey(),null);

    private static Map<Integer, String> publicKeyMap = AllNodeCommonMsg.publicKeyMap;

    /**
     * 使用自己的私钥进行签名
     *
     * @param msg
     */
    public static void signMsg(PbftMsg msg) {
        String hash = String.valueOf(msg.hashCode());
        byte[] sign= selfSign.sign(hash.getBytes());
        // String sign = selfRsa.encryptBase64(hash, KeyType.PrivateKey);
        msg.setSign(parseByte2HexStr(sign));
    }

     // 格式装换
     public  static String parseByte2HexStr(byte[] buf) {
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

    /**
     * 使用对方的公钥进行加密
     *
     * @param msg
     */
    private static boolean encryptMsg(int index, PbftMsg msg) {
        String publicKey;
        if ((publicKey = publicKeyMap.get(index)) == null) {
            log.error("对方公钥为空");
            return false;
        }

        if (msg.getBody() == null) {
            log.warn("消息体为空，不进行加密");
            return true;
        }

        RSA encryptRsa;
        try {
            encryptRsa = new RSA(null, publicKey);
        } catch (Exception e) {
            log.error("使用对方公钥创建RSA对象失败");
            return false;
        }

        // 进行加密
        msg.setBody(encryptRsa.encryptBase64(msg.getBody(), KeyType.PublicKey));
        return true;
    }

    /**
     * 使用自己的私钥进行解密
     *
     * @param msg
     */
    private static boolean decryptMsg(PbftMsg msg) {
        if (msg.getBody() == null) {
            log.warn("消息体为空，不进行解密");
            return true;
        }
        String body;
        try {
            body = selfRsa.decryptStr(msg.getBody(), KeyType.PrivateKey);
        } catch (Exception e) {
            log.error(String.format("私钥解密失败!%s", e.getMessage()));
            return false;
        }
        msg.setBody(body);
        return true;
    }

    /**
     * 消息的前置处理：
     * ①：进行加密
     * ②：进行签名
     *
     * @param index
     * @param msg
     * @return
     */
    public static boolean preMsg(int index, PbftMsg msg) {
        // if (!encryptMsg(index, msg)) {
        //     return false;
        // }
        signMsg(msg);
        return true;
    }

    /**
     * 对消息进行后置处理
     *
     * @param msg
     * @return
     */
    public static boolean afterMsg(PbftMsg msg) {
        // if (!isRealMsg(msg) || !decryptMsg(msg)) {
        //     return false;
        // }
        return isRealMsg(msg) ;
    }

    /**
     * 判断消息是否被改变
     * 首先判断签名是否有问题
     *
     * @param msg 解密之前的消息！！！！！
     * @return
     */
    public static boolean isRealMsg(PbftMsg msg) {
        // 获得此时消息的hash值
        String nowHash = String.valueOf(msg.hashCode());
        String sign = msg.getSign();
        try {
            Sign pubSign = SecureUtil.sign(SignAlgorithm.MD5withRSA,null,publicKeyMap.get(msg.getNode()));
            boolean verify=pubSign.verify(nowHash.getBytes(),parseHexStr2Byte(sign));
//            log.info(verify+"");
            return verify;
        } catch (Exception e) {
            log.warn(String.format("验证签名失效%s", e.getMessage()));
        }
        return false;
    }

    // 格式转换
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

}
