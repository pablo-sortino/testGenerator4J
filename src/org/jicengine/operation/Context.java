package org.jicengine.operation;


/**
 * Context : a namespace for objects.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public interface Context {

	/**
	 * finds an object from the context.
	 *
	 * @return  the object. NOTE: currently no null values supported.
	 * @throws ObjectNotFoundException  if the context doesn't have an object
	 * with the given name.
	 */
	public Object getObject(String name) throws ObjectNotFoundException;

	public boolean hasObject(String name);

	/**
	 * If addObject success, the object stored will be returned by getObject
	 * with the same name.
	 *
	 * @throws DuplicateNameException if there already is another object with the
	 * same name in the context.
	 */
	public void addObject(String name, Object object) throws DuplicateNameException;

	/**
	 * Returns a copy of this context.
	 *
	 * @return Context
	 */
	public Context replicate();
}
