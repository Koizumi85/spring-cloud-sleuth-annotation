package de.koizumi.sleuth.annotation;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class CreateSleuthSpanAnnotationAdvice implements MethodInterceptor {
	
	private SleuthSpanCreator spanCreator;
	private Tracer tracer;

	@Autowired
	public CreateSleuthSpanAnnotationAdvice(SleuthSpanCreator spanCreator, Tracer tracer) {
		this.spanCreator = spanCreator;
		this.tracer = tracer;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		
		Method mostSpecificMethod = AopUtils.getMostSpecificMethod(method, invocation.getThis().getClass());

		CreateSleuthSpan annotation = SleuthAnnotationUtils.findAnnotation(mostSpecificMethod);

		if (annotation == null) {
			return invocation.proceed();
		}

		Span span = null;
		try {
			span = spanCreator.createSpan(invocation, annotation);

			Object retVal = invocation.proceed();

			return retVal;
		} finally {
			if (span != null) {
				tracer.close(span);
			}
		}
	}

}
