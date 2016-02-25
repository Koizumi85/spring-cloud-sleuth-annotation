package de.koizumi.sleuth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.koizumi.sleuth.advice.SleuthSpanCreatorAdvice;
import de.koizumi.sleuth.util.DefaultSleuthSpanCreator;
import de.koizumi.sleuth.util.SleuthSpanCreator;

@Configuration
public class SleuthAnnotationConfiguration {
	
	@Autowired
	private Tracer tracer;

	@Bean
	SleuthSpanCreator spanCreator() {
		return new DefaultSleuthSpanCreator(tracer);
	}
	
	@Bean
	SleuthSpanCreatorAdvice sleuthAopAdvice() {
		return new SleuthSpanCreatorAdvice(spanCreator(), tracer);
	}
}
