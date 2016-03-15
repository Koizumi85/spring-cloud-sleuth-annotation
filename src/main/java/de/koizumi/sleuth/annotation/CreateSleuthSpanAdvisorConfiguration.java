package de.koizumi.sleuth.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.sleuth.annotation.enabled", matchIfMissing = true)
public class CreateSleuthSpanAdvisorConfiguration {
	
	@Bean
	public SleuthSpanTagAnnotationHandler spanTagAnnotationHandler(ApplicationContext context, Tracer tracer) {
		return new SleuthSpanTagAnnotationHandler(context, tracer);
	}
	
	@ConditionalOnMissingBean(SleuthSpanCreator.class)
	@Bean
	public SleuthSpanCreator spanCreator(Tracer tracer, SleuthSpanTagAnnotationHandler annotationSpanHandler) {
		return new DefaultSleuthSpanCreator(tracer, annotationSpanHandler);
	}
	
	@Bean
	public CreateSleuthSpanAnnotationAdvice advice(SleuthSpanCreator spanCreator, Tracer tracer) {
		return new CreateSleuthSpanAnnotationAdvice(spanCreator, tracer);
	}

	@Bean
	public CreateSleuthSpanAnnotationAdvisor advisor(CreateSleuthSpanAnnotationAdvice advice) {
		return new CreateSleuthSpanAnnotationAdvisor(advice);
	}
	
	@Bean
	public CreateSleuthSpanBeanPostProcessor beanPostProcessor() {
		return new CreateSleuthSpanBeanPostProcessor(advisor(null));
	}
}
