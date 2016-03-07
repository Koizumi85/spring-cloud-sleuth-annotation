package de.koizumi.sleuth.util;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import de.koizumi.sleuth.annotation.CreateSleuthSpan;

public class DefaultSleuthSpanCreator implements SleuthSpanCreator {
	
	private Tracer tracer;
	private SleuthAnnotationSpanUtil annotationSpanUtil;

	@Autowired
	public DefaultSleuthSpanCreator(Tracer tracer, SleuthAnnotationSpanUtil annotationSpanUtil) {
		this.tracer = tracer;
		this.annotationSpanUtil = annotationSpanUtil;
	}

	@Override
	public Span createSpan(JoinPoint pjp, CreateSleuthSpan sleuthInstrumented) {
		if (tracer.isTracing()) {
			String key = StringUtils.isNotEmpty(sleuthInstrumented.name()) ? sleuthInstrumented.name() : pjp.getSignature().getDeclaringType().getSimpleName() + "/" + pjp.getSignature().getName();
			Span span = tracer.createSpan(key, tracer.getCurrentSpan());
			annotationSpanUtil.addAnnotatedParameters(pjp);
			return span;
		}
		return null;
	}

}
