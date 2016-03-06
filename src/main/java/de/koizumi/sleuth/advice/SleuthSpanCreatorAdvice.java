package de.koizumi.sleuth.advice;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import de.koizumi.sleuth.annotation.CreateSleuthSpan;
import de.koizumi.sleuth.util.SleuthAnnotationUtils;
import de.koizumi.sleuth.util.SleuthSpanCreator;

@Aspect
public class SleuthSpanCreatorAdvice {

	private SleuthSpanCreator spanCreator;
	private Tracer tracer;

	@Autowired
	public SleuthSpanCreatorAdvice(SleuthSpanCreator spanCreator, Tracer tracer) {
		this.spanCreator = spanCreator;
		this.tracer = tracer;
	}

	@Pointcut("execution(public * *(..))")
	private void anyPublicOperation() {
	}

	@Around("anyPublicOperation()")
	public Object instrumentOnMethodAnnotation(ProceedingJoinPoint pjp) throws Throwable {
		Method method = getMethod(pjp);
		if (method == null) {
			return pjp.proceed();
		}

		Method mostSpecificMethod = AopUtils.getMostSpecificMethod(method, pjp.getTarget().getClass());

		CreateSleuthSpan annotation = SleuthAnnotationUtils.findAnnotation(mostSpecificMethod);

		if (annotation == null) {
			return pjp.proceed();
		}

		Span span = null;
		try {
			span = spanCreator.createSpan(pjp, annotation);

			Object retVal = pjp.proceed();

			return retVal;
		} finally {
			if (span != null) {
				tracer.close(span);
			}
		}
	}
	
	private Method getMethod(ProceedingJoinPoint pjp) {
		Signature signature = pjp.getStaticPart().getSignature();

		if (signature instanceof MethodSignature) {
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();

			return method;
		}
		return null;
	}

}
