package de.koizumi.sleuth.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.cloud.sleuth.Span;

import de.koizumi.sleuth.annotation.CreateSleuthSpan;

public interface SleuthSpanCreator {

	Span createSpan(ProceedingJoinPoint pjp, CreateSleuthSpan sleuthInstrumented);
		
}
