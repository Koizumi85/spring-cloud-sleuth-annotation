package de.koizumi.sleuth.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SleuthSpanTagAnnotationHandler {

	private ApplicationContext context;

	private Tracer tracer;

	@Autowired
	public SleuthSpanTagAnnotationHandler(ApplicationContext context, Tracer tracer) {
		this.context = context;
		this.tracer = tracer;
	}

	public void addAnnotatedParameters(MethodInvocation methodInvocation) {
		try {
			Method method = methodInvocation.getMethod();
			Method mostSpecificMethod = AopUtils.getMostSpecificMethod(method, methodInvocation.getThis().getClass());

			List<SleuthAnnotatedParameterContainer> annotatedParametersIndices = findAnnotatedParameters(
					mostSpecificMethod, methodInvocation.getArguments());
			if (!method.equals(mostSpecificMethod)) {
				List<SleuthAnnotatedParameterContainer> annotatedParametersIndicesForActualMethod = findAnnotatedParameters(
						method, methodInvocation.getArguments());
				mergeAnnotatedParameterContainers(annotatedParametersIndices,
						annotatedParametersIndicesForActualMethod);
			}

			addAnnotatedArguments(annotatedParametersIndices);

		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private void mergeAnnotatedParameterContainers(List<SleuthAnnotatedParameterContainer> annotatedParametersIndices,
			List<SleuthAnnotatedParameterContainer> annotatedParametersIndicesForActualMethod) {
		for (SleuthAnnotatedParameterContainer container : annotatedParametersIndicesForActualMethod) {
			final int index = container.getParameterIndex();
			boolean parameterContained = annotatedParametersIndices.stream()
					.anyMatch(new Predicate<SleuthAnnotatedParameterContainer>() {

						@Override
						public boolean test(SleuthAnnotatedParameterContainer t) {
							return t.getParameterIndex() == index;
						}
					});
			if (!parameterContained) {
				annotatedParametersIndices.add(container);
			}
		}
	}

	private void addAnnotatedArguments(List<SleuthAnnotatedParameterContainer> toBeAdded) {
		for (SleuthAnnotatedParameterContainer container : toBeAdded) {
			String tagValue = resolveTagValue(container.getAnnotation(), container.getArgument());
			tracer.addTag(container.getAnnotation().value(), tagValue);
		}
	}

	String resolveTagValue(SleuthSpanTag annotation, Object argument) {
		if (argument == null) {
			return "null";
		}
		if (StringUtils.isNotBlank(annotation.tagValueResolverBeanName())) {
			SleuthTagValueResolver tagValueResolver = context.getBean(annotation.tagValueResolverBeanName(),
					SleuthTagValueResolver.class);
			if (tagValueResolver != null) {
				return tagValueResolver.resolveTagValue(argument);
			}
		} else if (StringUtils.isNotBlank(annotation.tagValueExpression())) {
			try {
				ExpressionParser expressionParser = new SpelExpressionParser();

				Expression expression = expressionParser.parseExpression(annotation.tagValueExpression());
				return expression.getValue(argument, String.class);
			} catch (Exception e) {
			}
		}
		return argument.toString();
	}

	private List<SleuthAnnotatedParameterContainer> findAnnotatedParameters(Method method, Object[] args) {
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
				container.setParameterIndex(i);
				result.add(container);
			}
			i++;
		}
		return result;
	}

}
