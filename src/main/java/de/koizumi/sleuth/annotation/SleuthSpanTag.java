package de.koizumi.sleuth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method parameter which is annotated with this annotation, will be added as a tag which name of
 * "value" property, using the toString() representation of the parameter as tag-value
 * 
 * @author Christian Schwerdtfeger
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = { ElementType.PARAMETER })
public @interface SleuthSpanTag {

	/**
	 * The name of the tag which should be created
	 * @return
	 */
	String value();
	
	String tagValueExpression() default "";
	
	String tagValueResolverBeanName() default "";

}
