package cc.weno.p2p.server;

import cc.weno.config.AllNodeCommonMsg;
import cc.weno.dao.bean.ReplayJson;
import cc.weno.dao.node.Node;
import cc.weno.dao.node.NodeAddress;
import cc.weno.dao.node.NodeBasicInfo;
import cc.weno.dao.pbft.MsgCollection;
import cc.weno.dao.pbft.MsgType;
import cc.weno.dao.pbft.PbftMsg;
import cc.weno.p2p.client.ClientAction;
import cc.weno.p2p.common.MsgPacket;
import cc.weno.util.ClientUtil;
import cc.weno.util.MsgUtil;
import cc.weno.util.PbftUtil;
import cc.weno.util.TimerUtil;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.tio.client.ClientChannelContext;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.io.UnsupportedEncodingException;
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
 * @author: xiaohuiduan
 * @data: 2020/2/14 下午10:07
 * @description: 服务端的Action
 */
@Slf4j
public class ServerAction {
    private Node node = Node.getInstance();
    private MsgCollection msgCollection = MsgCollection.getInstance();
    /**
     * 单例模式构建action
     */
    private static ServerAction action = new ServerAction();

    private MsgCollection collection = MsgCollection.getInstance();

    public static ServerAction getInstance() {
        return action;
    }

    private ServerAction() {
    }


    /**
     * 对PBFT消息做出回应
     *
     * @param channelContext 谁发送的请求
     * @param msg            消息内容
     */
    public void doAction(ChannelContext channelContext, PbftMsg msg) {
        switch (msg.getMsgType()) {
            case MsgType.GET_VIEW:
                onGetView(channelContext, msg);
                break;
            case MsgType.CLIENT_REPLAY:
                addClient(msg);
                break;
            case MsgType.RESPONSE:
                response(msg);
                break;
            default:
                break;
        }
    }

    /**
     * 将自己的view发送给client
     *
     * @param channelContext
     * @param msg
     */
    private void onGetView(ChannelContext channelContext, PbftMsg msg) {
        log.info(String.format("代理节点收到视图请求%s", msg));
        int fromNode = msg.getNode();
        // 设置消息的发送方
        msg.setNode(node.getIndex());
        // 设置消息的目的地
        msg.setToNode(fromNode);
        // log.info(String.format("同意此节点%s的申请", msg));
        msg.setOk(true);
        msg.setViewNum(AllNodeCommonMsg.view);
        MsgUtil.signMsg(msg);
        String jsonView = JSON.toJSONString(msg);
        MsgPacket msgPacket = new MsgPacket();
        try {
            msgPacket.setBody(jsonView.getBytes(MsgPacket.CHARSET));
            Tio.send(channelContext, msgPacket);
        } catch (UnsupportedEncodingException e) {
            log.error(String.format("代理节点回复view消息失败%s", e.getMessage()));
        }
   
    }

    /**
     * 添加未连接的结点
     *
     * @param msg
     */
    private void addClient(PbftMsg msg) {
        log.info(String.format("收到广播消息%s",msg));
        if (!ClientUtil.haveClient(msg.getNode())) {
            String ipStr = msg.getBody();
            ReplayJson replayJson = JSON.parseObject(ipStr, ReplayJson.class);
            ClientChannelContext context = ClientUtil.clientConnect(replayJson.getIp(), replayJson.getPort());

            NodeAddress address = new NodeAddress();
            address.setIp(replayJson.getIp());
            address.setPort(replayJson.getPort());
            NodeBasicInfo info = new NodeBasicInfo();
            info.setIndex(msg.getNode());
            info.setAddress(address);
            // 添加ip地址
            AllNodeCommonMsg.allNodeAddressMap.put(msg.getNode(), info);
            AllNodeCommonMsg.publicKeyMap.put(msg.getNode(), replayJson.getPublicKey());

            log.info(String.format("添加节点%d的ip地址：%s", msg.getNode(), info));
            if (context != null) {
                // 添加client
                ClientUtil.addClient(msg.getNode(), context);
            }
        }
    }
    
    /**
     * 处理共识节点的认证结果
     *
     * @param msg
    */
    private void  response(PbftMsg msg) {
        long count = collection.getAgreeReply().incrementAndGet(msg.getId());
        // log.info(count+"");
        if (count >AllNodeCommonMsg.getAgreeNum() ) return;
        log.info(String.format("收到共识节点返回response%d:%s",count,msg));

        if (count == AllNodeCommonMsg.getAgreeNum() ) {
            // 将节点认证消息保存
            // DbUtil.save();

            // TODO 返回认证成功给客户端
            log.info(String.format("认证成功,时间%dms",System.currentTimeMillis()-msg.getTime()));
            TimerUtil.schedule(()->{
                collection.getAgreeReply().remove(msg.getId());
            },40);
            TimerTask timer=collection.getTimerQueue().get(msg.getId());
            timer.cancel();
        }

    }

}
