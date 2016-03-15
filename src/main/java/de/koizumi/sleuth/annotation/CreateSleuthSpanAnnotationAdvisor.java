package de.koizumi.sleuth.annotation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateSleuthSpanAnnotationAdvisor extends AbstractPointcutAdvisor {

	private static final long serialVersionUID = -1350246883930913569L;
	
	private CreateSleuthSpanAnnotationAdvice advice;
	
	@Autowired
	public CreateSleuthSpanAnnotationAdvisor(CreateSleuthSpanAnnotationAdvice advice) {
		this.advice = advice;
	}

	@Override
	public Pointcut getPointcut() {
		AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, CreateSleuthSpan.class);
		return pointcut;
	}

	@Override
	public Advice getAdvice() {
		return advice;
	}

}
