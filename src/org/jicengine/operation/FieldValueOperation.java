package org.jicengine.operation;


/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public class FieldValueOperation implements Operation {

  
	private Operation actor;
	private String field;
	private String signature;

	public FieldValueOperation(String signature, Operation actor, String field)
	{
		this.signature = signature;
		this.actor = actor;
		this.field = field;
	}

	public boolean needsParameters()
	{
		return this.actor.needsParameters();
	}

	public boolean needsParameter(String name)
	{
		return this.actor.needsParameter(name);
	}

	public Object execute(Context context) throws OperationException
	{
		Object actorObject = this.actor.execute(context);

		try {
			if( actorObject instanceof Class ){
				// invoke a static method.
       return ReflectionUtils.getFieldValue(null, (Class)actorObject, this.field);  
			}
			else {
				// invoke a method of the actor-instance.
				return ReflectionUtils.getFieldValue(actorObject, actorObject.getClass(), this.field);
			}
		} catch (java.lang.reflect.InvocationTargetException e){
			// InvocationTargetException means that the invoked method threw an exception
			// catch it and throw a CDLElementException that has the correct application-exception..
			throw new OperationException("'" + toString() + "' resulted in exception", e.getTargetException());
		} catch (Exception e2){
			throw new OperationException("'" + toString() + "': " + e2.getMessage(), e2);
		}
	}

	public String toString()
	{
		return this.signature;
	}
}
