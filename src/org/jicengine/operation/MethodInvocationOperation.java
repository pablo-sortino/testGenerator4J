package org.jicengine.operation;

import java.util.Collection;

/**
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class MethodInvocationOperation extends InvocationOperation {

	private String methodName;

	public MethodInvocationOperation(String signature, Operation actor, String methodName, Operation[] parameters)
	{
		super(signature, actor, parameters);
		this.methodName = methodName;
	}

	public Object execute(Object actor, Object[] arguments) throws OperationException
	{
		try {
			if( actor instanceof Class ){
				// invoke a static method.
				return ReflectionUtils.invokeStaticMethod((Class)actor, this.methodName, arguments);
			}
			else {
				// invoke a method of the actor-instance.
				return ReflectionUtils.invokeMethod(actor, this.methodName, arguments);
			}
		} catch (java.lang.reflect.InvocationTargetException e){
			// InvocationTargetException means that the invoked method threw an exception
			// catch it and throw a CDLElementException that has the correct application-exception..
			throw new OperationException("'" + toString() + "' resulted in exception", e.getTargetException());
		} catch (Exception e2){
			throw new OperationException("'" + toString() + "': " + e2.getMessage(), e2);
		}
	}

}
