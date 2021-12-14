
package cc.weno.dao.pbft;
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
 * @data: 2020/2/14 上午11:35
 * @description: 消息类型
 */
public class MsgType {

    /**
     * 代理节点发送请求到主节点
     */
    public static final int REQUEST= 1;
    
    /**
     * 回复给代理节点阶段
     */
    public static final int  RESPONSE= 5;
    /**
     * ip消息回复回复阶段
     */
    public static final int  CLIENT_REPLAY = 6;

    /**
     * 请求视图(代理节点存在)
     */
    public static final int GET_VIEW = 7;

    /**
     * 请求视图（代理节点不存在）
     */
    public static final int INITI_VIEW = 8;
    
    /**
     * 变更视图
     */
    public static final int CHANGE_VIEW = 9;
}
