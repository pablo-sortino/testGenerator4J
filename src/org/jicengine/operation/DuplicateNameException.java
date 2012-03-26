/**
 * 
 */
package org.jicengine.operation;

public class DuplicateNameException extends java.lang.RuntimeException {
	public DuplicateNameException(String name,Object old, Object newObject, Context context)
	{
		super("'" + name + "' already reserved to '" + old + "' (" + old.getClass().getName() + "), can't add '" + newObject + "' (" + newObject.getClass().getName() + ") into context " + context);
	}
}