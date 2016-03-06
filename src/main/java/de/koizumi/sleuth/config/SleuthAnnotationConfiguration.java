package de.koizumi.sleuth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.koizumi.sleuth.advice.SleuthSpanCreatorAdvice;
import de.koizumi.sleuth.config.SleuthSpanCreateBeanPostProcessor;
import de.koizumi.sleuth.util.DefaultSleuthSpanCreator;
import de.koizumi.sleuth.util.SleuthSpanCreator;

@Configuration
public class SleuthAnnotationConfiguration {
	
	@Autowired
	private Tracer tracer;

	@ConditionalOnMissingBean(SleuthSpanCreator.class)
	@Bean
	SleuthSpanCreator spanCreator() {
		return new DefaultSleuthSpanCreator(tracer);
	}
	
	@Bean
	public SleuthSpanCreateBeanPostProcessor sleuthSpanCreateBeanPostProcessor(SleuthSpanCreator spanCreator) {
		SleuthSpanCreateBeanPostProcessor postProcessor = new SleuthSpanCreateBeanPostProcessor(new SleuthSpanCreatorAdvice(spanCreator, tracer));
		return postProcessor;
	}
	
}
