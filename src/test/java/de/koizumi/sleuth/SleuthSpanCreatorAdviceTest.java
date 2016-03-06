package de.koizumi.sleuth;

import org.aspectj.lang.ProceedingJoinPoint;
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

import de.koizumi.sleuth.SleuthSpanCreatorAdviceTest.TestConfiguration;
import de.koizumi.sleuth.annotation.CreateSleuthSpan;
import de.koizumi.sleuth.config.SleuthAnnotationConfiguration;
import de.koizumi.sleuth.util.SleuthSpanCreator;

@SpringApplicationConfiguration(classes = TestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SleuthSpanCreatorAdviceTest {
	
	@Autowired
	private TestBeanI testBean;
	
	@Autowired
	private SleuthSpanCreator spanCreator;
	
	@Test
	public void shouldCreateSpanWhenAnnotationOnInterfaceMethod() {
		testBean.testMethod();
		
		Mockito.verify(spanCreator).createSpan(Mockito.<ProceedingJoinPoint> any(), Mockito.<CreateSleuthSpan> any());
		Mockito.reset(spanCreator);
	}
	
	@Test
	public void shouldCreateSpanWhenAnnotationOnClassMethod() {
		testBean.testMethod2();
		
		Mockito.verify(spanCreator).createSpan(Mockito.<ProceedingJoinPoint> any(), Mockito.<CreateSleuthSpan> any());
		Mockito.reset(spanCreator);
	}
	
	protected static interface TestBeanI {
		
		@CreateSleuthSpan
		void testMethod();
		
		void testMethod2();
	}
	
	protected static class TestBean implements TestBeanI {

		@Override
		public void testMethod() {
		}

		@CreateSleuthSpan
		@Override
		public void testMethod2() {
		}
		
	}
	
	@Configuration
	@Import({ TraceAutoConfiguration.class, CreateSleuthTestConfiguration.class, SleuthAnnotationConfiguration.class })
	protected static class TestConfiguration {
		
	}
	
	@Configuration
	protected static class CreateSleuthTestConfiguration {

		@Bean
		public TestBeanI testBean() {
			return new TestBean();
		}
		
		@Bean
		public SleuthSpanCreator sleuthSpanCreator() {
			return Mockito.mock(SleuthSpanCreator.class);
		}
		
	}
}
