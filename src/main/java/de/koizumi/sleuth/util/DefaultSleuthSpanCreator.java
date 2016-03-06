package de.koizumi.sleuth.util;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import de.koizumi.sleuth.annotation.CreateSleuthSpan;

public class DefaultSleuthSpanCreator implements SleuthSpanCreator {
	
	private Tracer tracer;

	@Autowired
	public DefaultSleuthSpanCreator(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public Span createSpan(ProceedingJoinPoint pjp, CreateSleuthSpan sleuthInstrumented) {
		if (tracer.isTracing()) {
			String key = StringUtils.isNotEmpty(sleuthInstrumented.name()) ? sleuthInstrumented.name() : pjp.getSignature().getDeclaringType().getSimpleName() + "/" + pjp.getSignature().getName();
			Span span = tracer.createSpan(key, tracer.getCurrentSpan());
			SleuthAnnotationSpanUtil.addAnnotatedParameters(pjp, span);
			return span;
		}
		return null;
	}

}
