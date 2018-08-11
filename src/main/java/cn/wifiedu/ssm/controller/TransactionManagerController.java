package cn.wifiedu.ssm.controller;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import cn.wifiedu.ssm.util.StringDeal;

/**
 * @author kqs
 * @time 2018年8月11日 - 下午5:47:21
 * @description:事务
 */
@Controller
@Scope("prototype")
public class TransactionManagerController {
	
	private static Logger logger = Logger.getLogger(TransactionManagerController.class);
	
	@Resource
	public PlatformTransactionManager txManager;

	private TransactionStatus status;
	
	// 类被实例化的时候 创建事务对象
	public TransactionManagerController() {
		logger.info("init txManager");
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		status = txManager.getTransaction(def);
		logger.info("create status:" + status);
	}
	
	// 提交事务
	public void commit() {
		logger.debug("Committing JDBC start:" + StringDeal.getStringDate());
		txManager.commit(status);
		logger.debug("Committing JDBC over:" + StringDeal.getStringDate());
	}
	
	// 回滚事务
	public void rollback() {
		logger.debug("Rolling JDBC start:" + StringDeal.getStringDate());
		txManager.rollback(status);
		logger.debug("Rolling JDBC over:" + StringDeal.getStringDate());
	}

}
