package org.jicengine.operation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Utilities for manipulating beans through reflection.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */
public class BeanUtils extends ReflectionUtils {
	private static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * Sets the value of a property.
	 *
	 * @param instance             the instance whose property is set.
	 * @param propertyName         the name of the property to be set.
	 * @param value                the new value of the property.
	 */
	public static void setProperty(Object instance, String propertyName, Object value) throws java.lang.NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		String setterName = toSetterMethodName(propertyName);
		invokeMethod(instance, setterName, new Object[]{value});
	}

	/**
	 * Transforms a property-name to a corresponding getter-method name.
	 * for example 'name' -> 'getName'
	 */
	public static String toGetterMethodName(String property){
		return "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
	}

	/**
	 * Transforms a property-name to a corresponding setter-method name.
	 * for example 'name' -> 'setName'
	 */
	public static String toSetterMethodName(String property){
		return "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
	}

	/**
	 * Returns the value of a property.
	 * throws NoSuchMethodException if the property doesn't exist.
	 */
	public static Object getProperty(Object instance, String propertyName) throws java.lang.NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String getterName = toGetterMethodName(propertyName);
		return invokeMethod(instance, getterName, EMPTY_ARRAY);
	}
}
