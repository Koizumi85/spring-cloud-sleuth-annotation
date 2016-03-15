package de.koizumi.sleuth.annotation;

import java.lang.reflect.Method;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class CreateSleuthSpanBeanPostProcessor implements BeanPostProcessor {

	private CreateSleuthSpanAnnotationAdvisor advisor;

	@Autowired
	public CreateSleuthSpanBeanPostProcessor(CreateSleuthSpanAnnotationAdvisor advisor) {
		this.advisor = advisor;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		boolean atLeastOneMethodAnnotated = false;

		for (Method method : bean.getClass().getMethods()) {
			if (SleuthAnnotationUtils.isMethodAnnotated(method)) {
				atLeastOneMethodAnnotated = true;
				break;
			}
		}
		
		if (!atLeastOneMethodAnnotated && (AopUtils.isAopProxy(bean) || AopUtils.isCglibProxy(bean) || AopUtils.isJdkDynamicProxy(bean))) {
			Class<?> beanTargetClass = AopUtils.getTargetClass(bean);
			for (Method method : beanTargetClass.getMethods()) {
				if (SleuthAnnotationUtils.isMethodAnnotated(method)) {
					atLeastOneMethodAnnotated = true;
					break;
				}
			}
		}
		

		if (!atLeastOneMethodAnnotated) {
			return bean;
		}
		
		ProxyFactory factory = new ProxyFactory(bean);
		factory.addAdvisor(advisor);
		return factory.getProxy();
	}

}
