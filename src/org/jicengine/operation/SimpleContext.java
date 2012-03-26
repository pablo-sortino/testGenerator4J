package org.jicengine.operation;

import java.util.*;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */
public class SimpleContext extends AbstractContext {

	private Map objects;

	public SimpleContext()
	{
		this("");
	}

	public SimpleContext(String name)
	{
		this(name,new java.util.HashMap());
	}

	public SimpleContext(String name, Map objects)
	{
		super(name);
		this.objects = objects;
	}

	public boolean hasObject(String name)
	{
		return this.objects.containsKey(name);
	}

	public Object getObject(String name) throws ObjectNotFoundException
	{
		Object object = this.objects.get(name);
		if( object != null ){
			return object;
		}
		else {
			throw new ObjectNotFoundException(name,this);
		}
	}

	public void addObject(String name, Object object)
	{
		Object previous = this.objects.put(name, object);
		if( previous != null ){
			throw new DuplicateNameException(name,previous, object, this);
		}
	}

	protected void copyObjectsTo(Map map)
	{
		map.putAll(this.objects);
	}

}
