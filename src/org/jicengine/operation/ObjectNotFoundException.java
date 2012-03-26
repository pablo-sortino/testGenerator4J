package org.jicengine.operation;

/**
 * <p>
 * Thrown when an object is not found from a <code>Context</code>.
 * </p>
 *
 * @author timo laitinen
 */
public class ObjectNotFoundException extends OperationException {
	public ObjectNotFoundException(String message)
	{
		super(message);
	}
	public ObjectNotFoundException(String objectName,Context context)
	{
		super("Object '" + objectName + "' not found in context " + context);
	}
}
