package de.koizumi.sleuth.util;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;

import de.koizumi.sleuth.annotation.CreateSleuthSpan;

public class SleuthAnnotationUtils {

	public static boolean isMethodAnnotated(Method method) {
		return findAnnotation(method) != null;
	}
	
	public static CreateSleuthSpan findAnnotation(Method method) {
		CreateSleuthSpan annotation = AnnotationUtils.findAnnotation(method, CreateSleuthSpan.class);
		if (annotation == null) {
			try {
				annotation = AnnotationUtils.findAnnotation(method.getDeclaringClass().getMethod(method.getName(), method.getParameterTypes()), CreateSleuthSpan.class);
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		}
		return annotation;
	}
}
