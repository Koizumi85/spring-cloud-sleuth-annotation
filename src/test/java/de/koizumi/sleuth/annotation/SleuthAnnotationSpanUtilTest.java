package de.koizumi.sleuth.annotation;

import static org.assertj.core.api.Assertions.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.koizumi.sleuth.annotation.SleuthAnnotationConfiguration;
import de.koizumi.sleuth.annotation.SleuthAnnotationSpanUtil;
import de.koizumi.sleuth.annotation.SleuthSpanCreator;
import de.koizumi.sleuth.annotation.SleuthSpanTag;
import de.koizumi.sleuth.annotation.SleuthTagValueResolver;
import de.koizumi.sleuth.annotation.SleuthAnnotationSpanUtilTest.TestConfiguration;

@SpringApplicationConfiguration(classes = TestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SleuthAnnotationSpanUtilTest {

	@Autowired
	private SleuthAnnotationSpanUtil spanUtil;
	
	@Autowired
	private SleuthTagValueResolver tagValueResolver;
	
	@Test
	public void shouldUseCustomTagValueResolver() throws NoSuchMethodException, SecurityException {
		Method method = AnnotationMockClass.class.getMethod("getAnnotationForTagValueResolver", String.class);
		Annotation annotation = method.getParameterAnnotations()[0][0];
		if (annotation instanceof SleuthSpanTag) {
			String resolvedValue = spanUtil.resolveTagValue((SleuthSpanTag) annotation, "test");
			assertThat(resolvedValue).isEqualTo("Value from myCustomTagValueResolver");
			Mockito.verify(tagValueResolver).resolveTagValue("test");
		} else {
			fail("Annotation was not SleuthSpanTag");
		}
	}
	
	@Test
	public void shouldUseTagValueExpression() throws NoSuchMethodException, SecurityException {
		Method method = AnnotationMockClass.class.getMethod("getAnnotationForTagValueExpression", String.class);
		Annotation annotation = method.getParameterAnnotations()[0][0];
		if (annotation instanceof SleuthSpanTag) {
			String resolvedValue = spanUtil.resolveTagValue((SleuthSpanTag) annotation, "test");
			
			assertThat(resolvedValue).isEqualTo("4 characters");
		} else {
			fail("Annotation was not SleuthSpanTag");
		}
	}
	
	@Test
	public void shouldReturnArgumentToString() throws NoSuchMethodException, SecurityException {
		Method method = AnnotationMockClass.class.getMethod("getAnnotationForArgumentToString", String.class);
		Annotation annotation = method.getParameterAnnotations()[0][0];
		if (annotation instanceof SleuthSpanTag) {
			String resolvedValue = spanUtil.resolveTagValue((SleuthSpanTag) annotation, "test");
			assertThat(resolvedValue).isEqualTo("test");
		} else {
			fail("Annotation was not SleuthSpanTag");
		}
	}
	
	protected static class AnnotationMockClass {
		
		public void getAnnotationForTagValueResolver(@SleuthSpanTag(value = "test", tagValueResolverBeanName = "myCustomTagValueResolver") String test) {
		}
		
		public void getAnnotationForTagValueExpression(@SleuthSpanTag(value = "test", tagValueExpression = "length() + ' characters'") String test) {
		}
		
		public void getAnnotationForArgumentToString(@SleuthSpanTag(value = "test") String test) {
		}
	}
	
	@Configuration
	@Import({ TraceAutoConfiguration.class, CreateSleuthTestConfiguration.class, SleuthAnnotationConfiguration.class })
	protected static class TestConfiguration {
		
	}
	
	@Configuration
	protected static class CreateSleuthTestConfiguration {
		
		@Bean(name = "myCustomTagValueResolver")
		public SleuthTagValueResolver tagValueResolver() {
			return Mockito.spy(new SleuthTagValueResolver() {
				
				@Override
				public String resolveTagValue(Object parameter) {
					return "Value from myCustomTagValueResolver";
				}
			});
		}

		@Bean
		public SleuthSpanCreator sleuthSpanCreator() {
			return Mockito.mock(SleuthSpanCreator.class);
		}
		
	}
}
