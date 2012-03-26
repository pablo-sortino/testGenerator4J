package org.jicengine.operation;


/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public abstract class AbstractContext implements Context {

	String name;

	protected AbstractContext()
	{
		name = "unknown";
	}

	protected AbstractContext(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public String toString()
	{
		return "{" + getName() + "}";
	}

	protected static String getName(Context context, String defaultName)
	{
		if( context != null && context instanceof AbstractContext ){
			return ((AbstractContext)context).getName();
		}
		else {
			return defaultName;
		}
	}

	public Context replicate()
	{
		java.util.Map objects = new java.util.HashMap();
		copyObjectsTo(objects);
		return new SimpleContext(getName(), objects);
	}

	protected abstract void copyObjectsTo(java.util.Map map);

}
