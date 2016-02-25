package de.koizumi.sleuth.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cloud.sleuth.Span;

import de.koizumi.sleuth.annotation.SleuthSpanTag;

public class SleuthAnnotationSpanUtil {

	public static void addAnnotatedParameters(ProceedingJoinPoint pjp, Span span) {
		try {
			Signature signature = pjp.getStaticPart().getSignature();
			if (signature instanceof MethodSignature) {
				MethodSignature ms = (MethodSignature) signature;
				Method method = ms.getMethod();

				List<SleuthAnnotatedParameterContainer> annotatedParametersIndices = findAnnotatedParameters(method, pjp.getArgs());
				addAnnotatedArguments(span, annotatedParametersIndices);
			}

		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private static void addAnnotatedArguments(Span span, List<SleuthAnnotatedParameterContainer> toBeAdded) {
		for (SleuthAnnotatedParameterContainer container : toBeAdded) {
			try {
				String tagValue = resolveTagValue(container.getAnnotation(), container.getArgument());
				span.tag(container.getAnnotation().value(), tagValue);
			} catch (Exception e) {
			}
		}
	}
	
	private static String resolveTagValue(SleuthSpanTag annotation, Object argument) {
		if (StringUtils.isNotBlank(annotation.tagValueResolverBeanName())) {
			// TODO resolve application context. Take the bean from there and then resolve the value
		} else if (StringUtils.isNotBlank(annotation.tagValueExpression())) {
			// TODO resolve value using a SpEL expression
		}
		return argument.toString();
	}

	private static List<SleuthAnnotatedParameterContainer> findAnnotatedParameters(Method method, Object[] args) {
		Parameter[] parameters = method.getParameters();
		List<SleuthAnnotatedParameterContainer> result = new ArrayList<>();

		int i = 0;
		for (Parameter parameter : parameters) {
			if (parameter.isAnnotationPresent(SleuthSpanTag.class)) {
				SleuthSpanTag annotation = parameter.getAnnotation(SleuthSpanTag.class);
				SleuthAnnotatedParameterContainer container = new SleuthAnnotatedParameterContainer();
				container.setAnnotation(annotation);
				container.setArgument(args[i]);
				container.setParameter(parameter);
				result.add(container);
			}
			i++;
		}
		return result;
	}

}
