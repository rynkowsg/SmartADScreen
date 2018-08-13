package cn.com.smartadscreen.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器
 * 
 * @author Robin
 * @time 2016年11月1日
 */
public class TimerUtils {

	private final static Map<String, Timer> timers = new HashMap<>();

	/**
	 * 安排延迟执行任务
	 * 
	 * @param task
	 *            任务
	 * @param delay
	 *            执行延迟时间（微秒）
	 */
	public synchronized static void schedule(String key, TimerTask task, long delay) {

		close(key);
		Timer t = new Timer();
		t.schedule(task, delay);
		timers.put(key, t);
	}

	/**
	 * 延迟安排定时执行任务
	 * 
	 * @param task
	 * @param delay
	 * @param period
	 */
	public synchronized static void schedule(String key, TimerTask task, long delay, long period) {
		close(key);
		Timer t = new Timer();
		t.schedule(task, delay, period);
		timers.put(key, t);

	}

	public static void close(String key) {
		Timer timer = null;
		synchronized (timers) {
			timer = timers.remove(key);
		}
		if (timer != null) {
			timer.cancel();
		}
	}

	public static Timer getTimerByKey(String key){
		return timers.get(key);
	}

}
