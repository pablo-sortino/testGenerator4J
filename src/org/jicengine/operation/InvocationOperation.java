package org.jicengine.operation;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>
 * Either a method or constructor invocation.
 * </p>
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public abstract class InvocationOperation implements Operation {

	Operation actor;
	Operation[] parameters;

	String signature;

	public InvocationOperation(String signature, Operation actor, Operation[] parameters)
	{
		this.actor = actor;
		this.parameters = parameters;
		this.signature = signature;
	}

	public boolean needsParameters()
	{
		return this.parameters.length > 0;
	}

	public boolean needsParameter(String name)
	{
		if( this.actor.needsParameter(name) ){
			return true;
		}

		for (int i = 0; i < this.parameters.length; i++) {
			if( this.parameters[i].needsParameter(name) ){
				return true;
			}
		}

		return false;
	}

	public Object execute(Context context) throws OperationException
	{
		Object actorObject = this.actor.execute(context);
		Object[] arguments = evaluateParameters(this.parameters, context);
		return execute(actorObject, arguments);
	}

	public String toString()
	{
		return this.signature;
	}

	protected abstract Object execute(Object actor, Object[] arguments) throws OperationException;

	public static Object[] evaluateParameters(Operation[] parameters, Context context) throws OperationException
	{
		Object[] params = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			params[i] = parameters[i].execute(context);
		}
		return params;
	}

}
