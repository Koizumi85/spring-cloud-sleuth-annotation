package de.koizumi.sleuth.annotation;
import java.lang.reflect.Parameter;

public class SleuthAnnotatedParameterContainer {

	private int parameterIndex;
	private SleuthSpanTag annotation;
	private Object argument;
	private Parameter parameter;

	public SleuthSpanTag getAnnotation() {
		return annotation;
	}

	public void setAnnotation(SleuthSpanTag annotation) {
		this.annotation = annotation;
	}

	public Object getArgument() {
		return argument;
	}

	public void setArgument(Object argument) {
		this.argument = argument;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public void setParameterIndex(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

}
