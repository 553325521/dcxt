package cn.wifiedu.ssm.task;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import cn.wifiedu.ssm.controller.InterfaceController;
import cn.wifiedu.ssm.util.StringDeal;

public class WxComponentTokenTask extends TimerTask {

	private static Logger logger = Logger.getLogger(WxComponentTokenTask.class);
	
	private ApplicationContext wac;
	
	/**
	 * @param wac
	 */
	public WxComponentTokenTask(ApplicationContext wac) {
		this.wac = wac;
	}

	@Override
	public void run() {
		try {
			// 每小时获取一次token保存到redis
			InterfaceController inter = (InterfaceController) wac.getBean("interfaceController");
			String token = inter.getComponentToken();
			logger.info("token:" + token + ", getTime:" + StringDeal.getStringDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
