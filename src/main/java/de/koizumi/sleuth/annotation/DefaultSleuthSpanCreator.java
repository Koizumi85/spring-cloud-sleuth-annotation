package de.koizumi.sleuth.annotation;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class DefaultSleuthSpanCreator implements SleuthSpanCreator {
	
	private Tracer tracer;
	private SleuthSpanTagAnnotationHandler annotationSpanHandler;

	@Autowired
	public DefaultSleuthSpanCreator(Tracer tracer, SleuthSpanTagAnnotationHandler annotationSpanHandler) {
		this.tracer = tracer;
		this.annotationSpanHandler = annotationSpanHandler;
	}

	@Override
	public Span createSpan(MethodInvocation methodInvocation, CreateSleuthSpan createSleuthSpanAnnotation) {
		if (tracer.isTracing()) {
			String key = StringUtils.isNotEmpty(createSleuthSpanAnnotation.name()) ? createSleuthSpanAnnotation.name() : methodInvocation.getMethod().getDeclaringClass().getSimpleName() + "/" + methodInvocation.getMethod().getName();
			Span span = tracer.createSpan(key, tracer.getCurrentSpan());
			annotationSpanHandler.addAnnotatedParameters(methodInvocation);
			return span;
		}
		return null;
	}

}
