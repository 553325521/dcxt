package cn.wifiedu.ssm.util;

import java.util.Locale;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取Spring上下文及国际化
 * 
 * @author bingchuan -->www.vijun.com
 * 
 */
public final class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
	@SuppressWarnings("static-access")
    public void setApplicationContext(ApplicationContext contex) throws BeansException {
        SpringContextUtil.context = contex;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static String getMessage(String key) {
        return context.getMessage(key, null, Locale.getDefault());
    }
}