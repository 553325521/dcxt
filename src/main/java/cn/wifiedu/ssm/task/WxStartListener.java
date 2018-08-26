package cn.wifiedu.ssm.task;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WxStartListener implements ServletContextListener {

	private Timer wxComponentTokenTimer;
	
	/**
	 * 监听开始销毁
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("contextDestroyed");
	}

	/**
	 * 监听开始执行
	 */
	public void contextInitialized(ServletContextEvent sce) {
		
		// 当监听开始执行时,设置一个TIME
		Timer timer = new Timer();
		WxStartTask task = new WxStartTask();
		timer.schedule(task, 0, 3600 * 1000);
		
//		wxComponentTokenTimer = new Timer();
//		timer.schedule(new WxComponentTokenTask(sce), 0, 3600 * 1000);
	}
}
