package cn.wifiedu.ssm.task;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WxStartListener implements ServletContextListener {

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
		  timer.schedule(task, 0, 60 * 1000);  
		}
}
