package cc.weno.dao.pbft;

import cc.weno.dao.bean.DbDao;
import cn.hutool.db.Db;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicLongMap;
import lombok.Data;

import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

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
 * @data: 2020/2/14 上午10:54
 * @description: 这个是进行PBFT算法保存的消息
 * 使用单例模式进行设计
 */
@Data
public class MsgCollection {

    private static MsgCollection msgCollection = new MsgCollection();

    /**
     * 空的构造方法
     */
    private MsgCollection() {
    }

    public static MsgCollection getInstance() {
        return msgCollection;
    }

    /**
     * 进行处理的消息队列，don‘t care about what is , just get it ！！
     */
    private BlockingQueue<PbftMsg> msgQueue = new LinkedBlockingQueue<PbftMsg>();

    /**
     * 这个是在初始化视图集合
     */
    private AtomicLongMap<Integer> viewNumCount = AtomicLongMap.create();
    /**
     * 消息定时器
     */
    private ConcurrentHashMap<String, TimerTask> timerQueue = new  ConcurrentHashMap<>(32);

    /**
     * 切换视图集合
     */
    private AtomicLongMap<Integer> changeViewNumCount = AtomicLongMap.create();

    /**
     * 参与认证的节点 
     */
    private CopyOnWriteArrayList<DbDao> dbDaos = new CopyOnWriteArrayList<DbDao>();
    
    /**
     * 不赞同视图的数量
     */
    private AtomicLong disagreeViewNum = new AtomicLong();


    /**
     * 预准备阶段
     */
    private Set<String> votePrePrepare = Sets.newConcurrentHashSet();

    /**
     * 准备阶段
     */
    private AtomicLongMap<String> agreePrepare = AtomicLongMap.create();

    /**
     * commit阶段
     */
    private AtomicLongMap<String> agreeCommit = AtomicLongMap.create();
    
    /**
     * reply阶段
     */
    private AtomicLongMap<String> agreeReply = AtomicLongMap.create();


}
