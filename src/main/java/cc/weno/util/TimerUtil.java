package cc.weno.util;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {
    private static final Timer t = new Timer();

    public static TimerTask schedule(final Runnable r, long delay) {
       final TimerTask task = new TimerTask() { public void run() { r.run(); }};
       t.schedule(task, delay);
       return task;
    }
    
}
