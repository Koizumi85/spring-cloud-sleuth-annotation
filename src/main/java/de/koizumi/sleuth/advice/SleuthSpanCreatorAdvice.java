package de.koizumi.sleuth.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import de.koizumi.sleuth.annotation.CreateSleuthSpan;
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

	@Around("anyPublicOperation() && @within(sleuthInstrumented) && !@annotation(de.koizumi.sleuth.annotation.SleuthInstrumented)")
	public Object instrumentOnTypeAnnotation(ProceedingJoinPoint pjp, CreateSleuthSpan sleuthInstrumented) throws Throwable {
		Span span = null;
		try {
			span = spanCreator.createSpan(pjp, sleuthInstrumented);

			Object retVal = pjp.proceed();

			return retVal;
		} finally {
			if (span != null) {
				tracer.close(span);
			}
		}
	}

	@Around("anyPublicOperation() && @annotation(sleuthInstrumented)")
	public Object instrumentOnMethodAnnotation(ProceedingJoinPoint pjp, CreateSleuthSpan sleuthInstrumented) throws Throwable {
		Span span = null;
		try {
			span = spanCreator.createSpan(pjp, sleuthInstrumented);

			Object retVal = pjp.proceed();

			return retVal;
		} finally {
			if (span != null) {
				tracer.close(span);
			}
		}
	}

	
	
}
