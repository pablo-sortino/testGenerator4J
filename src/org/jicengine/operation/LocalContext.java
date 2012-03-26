package org.jicengine.operation;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * local context is a context inside a larger context (parent context).
 * objects in the local context override objects of the parent context.
 * if an object is not found from the local context, the parent context
 * is searched.
 * </p>
 *
 * <p>
 * new objects are added only to the local context.
 * </p>
 * <p>
 * the local context is represented by another Local-instance. this way
 * its implementation can be changed.
 * </p>
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public class LocalContext extends AbstractContext {

	private Context local;
	private Context parent;

	/**
	 *
	 */
	public LocalContext(Context local, Context parent)
	{
		super(local  + " + " + parent);
		this.local = local;
		this.parent = parent;
	}

	public LocalContext(String name, Context parent)
	{
		this(name, new SimpleContext(name), parent);
	}


	public LocalContext(String name, Context local, Context parent)
	{
		super(name);
		this.local = local;
		this.parent = parent;
	}

	public Context getParent()
	{
		return this.parent;
	}

	public Object getObject(String name) throws ObjectNotFoundException
	{
		if( this.local.hasObject(name) ){
			return this.local.getObject(name);
		}
		else {
			try {
				return getFromParent(name);
			} catch (ObjectNotFoundException e){
				throw new ObjectNotFoundException(name, this);
			}
		}
	}

	protected Object getFromParent(String name) throws ObjectNotFoundException
	{
		return getParent().getObject(name);
	}

	public boolean hasObject(String name) {
		return (this.local.hasObject(name)) || (hasInParent(name));
	}

	protected boolean hasInParent(String name)
	{
		return getParent().hasObject(name);
	}

	public void addObject(String name, Object object)
	{
		this.local.addObject(name,object);
	}

	protected void copyObjectsTo(Map map)
	{
		// first the parent
		((AbstractContext)this.parent).copyObjectsTo(map);
		// now the child so it may override the objects in the parent
		((AbstractContext)this.local).copyObjectsTo(map);
	}

}
