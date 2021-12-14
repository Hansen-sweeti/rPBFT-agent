package cc.weno.util;

import cc.weno.config.AllNodeCommonMsg;
import cc.weno.config.StartConfig;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.json.JSONUtil;
import cc.weno.dao.bean.ReplayJson;
import cc.weno.dao.node.Node;
import cc.weno.dao.pbft.PbftMsg;
import lombok.extern.slf4j.Slf4j;

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
 * @data: 2020/2/15 下午10:38
 * @description: pbft算法的工具类
 */
@Slf4j
public class PbftUtil {

    private static boolean flag = true;
    

    public static String ipJsonPath = StartConfig.basePath + "ip.json";
    //mata
//    private static  Sign sign ;
    


//    public static boolean checkMsg(PbftMsg msg) {
//
//        String certificate = msg.getCertificate();
//        try {
//            // log.debug(msg.getBody());
//            // log.debug(certificate);
//            sign = SecureUtil.sign(SignAlgorithm.MD5withRSA,null, AllNodeCommonMsg.publicKeyMap.get(0));
//            boolean verify = sign.verify(msg.getBody().getBytes(), MsgUtil.parseHexStr2Byte(certificate));
//            log.debug(verify+"");
//            return verify;
//        } catch (Exception e) {
//            log.warn(String.format("接口验证失败", e.getMessage()));
//        }
//        return false;
//    }

    public static void save(PbftMsg msg) {
        log.warn(String.format("Pbft消息可以写入块%s", msg));
        log.warn("请根据自己的需要使用DbUtil类进行操作");
    }

    /**
     * 信息写入文件和AllNodeCommonMsg.allNodeAddressMap
     *
     * @param node
     */
    synchronized public static void writeIpToFile(Node node) {
        if (!flag) {
            return;
        }
        log.info(String.format("节点%s写入文件", node.getIndex()));
        FileWriter writer = new FileWriter(ipJsonPath);
        ReplayJson replayJson = new ReplayJson();
        replayJson.setIndex(node.getIndex());
        replayJson.setBf(node.getBf());
        replayJson.setIp(node.getAddress().getIp());
        replayJson.setPort(node.getAddress().getPort());
        replayJson.setPublicKey(node.getPublicKey());
        String json = JSONUtil.toJsonStr(replayJson);
        writer.append(json + "\n");
        flag = false;
    }

}
