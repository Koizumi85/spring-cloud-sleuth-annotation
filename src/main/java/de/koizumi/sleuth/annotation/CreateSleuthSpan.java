package de.koizumi.sleuth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This method can be put on a public method or a class.
 * For each public method in an annotated class, or self annotated method, a new Span will be created.
 * Method parameters can be annotated with {@link SleuthSpanTag}, which will add the parameter value as a tag to the span
 * 
 * @author Christian Schwerdtfeger
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = { ElementType.METHOD, ElementType.TYPE })
public @interface CreateSleuthSpan {

	/**
	 * The name of the span which will be created. Default is "classname/methodname"
	 * @return
	 */
	String name() default "";

}
