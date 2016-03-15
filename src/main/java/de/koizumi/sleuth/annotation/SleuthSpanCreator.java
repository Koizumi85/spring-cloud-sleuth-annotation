package de.koizumi.sleuth.annotation;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cloud.sleuth.Span;

public interface SleuthSpanCreator {

	Span createSpan(MethodInvocation methodInvocation, CreateSleuthSpan sleuthInstrumented);
		
}
