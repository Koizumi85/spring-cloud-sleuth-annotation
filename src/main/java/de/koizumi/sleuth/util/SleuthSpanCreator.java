package de.koizumi.sleuth.util;

import org.aspectj.lang.JoinPoint;
import org.springframework.cloud.sleuth.Span;

import de.koizumi.sleuth.annotation.CreateSleuthSpan;

public interface SleuthSpanCreator {

	Span createSpan(JoinPoint pjp, CreateSleuthSpan sleuthInstrumented);
		
}
