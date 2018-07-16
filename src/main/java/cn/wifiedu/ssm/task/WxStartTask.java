package cn.wifiedu.ssm.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.controller.ProgressController;
import cn.wifiedu.ssm.util.WxUtil;

public class WxStartTask extends TimerTask {

	private OpenService openService;

	public OpenService getOpenService() {
		return this.openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	private static Logger logger = Logger.getLogger(ProgressController.class);
	private int num = 1;

	@Override
	public void run() {

		try {
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");// 可以方便地修改日期格式
			String hehe = dateFormat.format(now);
			String str[] = hehe.split(":");

			/**
			 * 随着项目启动加载配置
			 */
			// 每小时获取一次token保存到数据库
			if (num == 1 || str[1].equals("00")) {
				WxUtil.setToken();

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		num++;
	}

}
