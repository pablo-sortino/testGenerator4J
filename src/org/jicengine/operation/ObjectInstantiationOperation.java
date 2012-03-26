package org.jicengine.operation;

import java.util.*;

/**
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public class ObjectInstantiationOperation extends InvocationOperation {

	public ObjectInstantiationOperation(String signature, Operation instantiatedClass, Operation[] parameters)
	{
		super(signature, instantiatedClass, parameters);
	}

	protected Object execute(Object actor, Object[] arguments) throws OperationException
	{
		try {
			return instantiate((Class) actor, arguments);
		} catch (RuntimeException e){
			throw e;
		} catch (Exception e){
			throw new OperationException(e.toString(), e);
		}
	}

	/**
	 * instantiates a class.
	 */
	private Object instantiate(Class instantiatedClass, Object[] arguments) throws Exception {
		try {
			return org.jicengine.operation.ReflectionUtils.instantiate(instantiatedClass, arguments);
		} catch (java.lang.reflect.InvocationTargetException e){
			throw (Exception) e.getTargetException();
		}
	}

}
