package de.koizumi.sleuth.annotation;

import org.aspectj.lang.JoinPoint;
import org.springframework.cloud.sleuth.Span;

public interface SleuthSpanCreator {

	Span createSpan(JoinPoint pjp, CreateSleuthSpan sleuthInstrumented);
		
}
