package org.jicengine.operation;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class VariableValueOperation implements Operation {

	String name;

	public VariableValueOperation(String objectName)
	{
		// todo: check that the name is valid?
		this.name = objectName;
	}

	public String getName()
	{
		return this.name;
	}

	public boolean needsParameters()
	{
		return true;
	}

	public boolean needsParameter(String name)
	{
		return (this.name.equals(name));
	}

	public static Object lookup(String name, Context context) throws OperationException
	{
		try {
			return context.getObject(name);
		} catch (Exception e2){
			throw new OperationException("Failed to find object '" + name + "'.",e2);
		}
	}

	public Object execute(Context context) throws OperationException
	{
		Object value = lookup(getName(), context);
		if( value != null ){
			return value;
		}
		else {
			throw new OperationException("Object named '" + getName() + "' not found in context " + context);
		}
	}

	public String toString()
	{
		return getName();
	}
}
