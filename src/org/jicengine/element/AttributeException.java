package org.jicengine.element;

/**
 * <p>
 * Signals an error in an attribute: the attribute is illegal, the value is
 * illegal, etc. </p>
 *
 *
 * @author timo laitinen
 */
public class AttributeException extends ElementException {
	public AttributeException(String message, String elementName, Location location)
	{
		super(message, elementName, location);
	}

	public AttributeException(String message, Throwable cause, String elementName, Location location)
	{
		super(message, cause, elementName, location);
	}

	public AttributeException(Throwable cause, String elementName, Location location)
	{
		super(cause, elementName, location);
	}

	public AttributeException(Throwable cause, String elementName, String attributeName, Location location)
	{
		super(cause, elementName, attributeName, location);
	}
}
