import cc.weno.config.AllNodeCommonMsg;
import cc.weno.config.StartConfig;
import cc.weno.dao.node.Node;
import cc.weno.dao.node.NodeAddress;
import cc.weno.dao.pbft.MsgCollection;
import cc.weno.dao.pbft.MsgType;
import cc.weno.dao.pbft.PbftMsg;
import cc.weno.util.ClientUtil;
import cc.weno.util.Pbft;
import cc.weno.util.StartPbft;
import cc.weno.util.TimerUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.maintain.Users;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.TimerTask;


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
 * @author: mata
 * @data: 2021/10/13 下午2:46
 * @description: 程序运行开始类
 * 启动参数顺序：ip，port，index，认证请求消息
 */
@Slf4j
public class Main {

    public static void main(String[] args) {

       int i = 0;
       String ip = "127.0.0.1";
       int port = 8080 + i;
       int bf=0;

       StartConfig.basePath = "E:\\demo\\";
       int index = i;

      /*   if (args.length != 5) {
            log.error("参数错误");
            return;
        }
//        程序启动ip地址
        String ip = args[0];
//        端口
        int port = Integer.parseInt(args[1]);
//        程序启动index
        int index = Integer.parseInt(args[2]);
//      节点是否为恶意节点
        int bf= Integer.parseInt(args[3]);
//        文件保存位置，在文件保存位置必须存在一个oldIp.json的文件
        StartConfig.basePath = args[4];  */

        callScript("ca.sh","C:\\Users\\ASUS\\Desktop\\pbft\\pbft-agent\\src\\main\\java\\cc\\ca");
        createIpJsonFile(StartConfig.basePath);

        Node node = Node.getInstance();
        node.setIndex(index);
        node.setBf(bf);
        NodeAddress nodeAddress = new NodeAddress();
        nodeAddress.setIp(ip);
        nodeAddress.setPort(port);
        node.setAddress(nodeAddress);
        StartPbft.start();

//        可以在这里发送消息
        Scanner scanner = new Scanner(System.in);
        MsgCollection collection = MsgCollection.getInstance();
        while (true) {
            callScript("client.sh","C:\\Users\\ASUS\\Desktop\\pbft\\pbft-agent\\src\\main\\java\\cc\\ca");
            String str = scanner.next();
            //new Pbft().changeView();
            PbftMsg msg = new PbftMsg(MsgType.REQUEST, 0);
            msg.setBody(str);

            msg.setCertificate("C:\\Users\\ASUS\\Desktop\\pbft\\pbft-agent\\src\\main\\java\\cc\\ca\\server.pem");//传入证书
            TimerTask timer=TimerUtil.schedule(()->{
               collection.getTimerQueue().remove(msg.getId());
               },1000);
            /*TimerTask timer=TimerUtil.schedule(()->{
               collection.getTimerQueue().remove(msg.getId());
                new Pbft().changeView();
            },1000);*/
            collection.getTimerQueue().put(msg.getId(), timer);
            new Pbft().changeView();
            ClientUtil.clientRequest(msg);
        }
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
     * 如果文件或者文件夹不存在则创建
     *
     * @param basePath
     */
    private static void createIpJsonFile(String basePath) {
        File dir = new File(basePath);

        // 如果目录不存在
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(basePath + "ip.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("json文件创建失败");
                e.printStackTrace();
            }
        }
    }
    //"C:\\Users\\ASUS\\Desktop\\pbft\\pbft-agent\\src\\main\\java\\cc\\ca"
    @SneakyThrows
    private static void callScript(String file,String dir) {
        BufferedReader reader = null;
        String result="";
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", file)
                .directory(new File(dir));
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println("mac@wxw %  " + line);
        }
        System.out.println("\nExited with error code : " + exitCode);
    }
}
 