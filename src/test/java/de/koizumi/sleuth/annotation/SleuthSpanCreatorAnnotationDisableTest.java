package de.koizumi.sleuth.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(classes = SleuthAnnotationConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest(value = "spring.sleuth.annotation.enabled=false")
public class SleuthSpanCreatorAnnotationDisableTest {

	@Autowired(required = false)
	private SleuthSpanTagAnnotationHandler annotationSpanUtil;
	
	@Test
	public void shouldNotAutowireBecauseConfigIsDisabled() {
		assertThat(annotationSpanUtil).isNull();
	}
}
