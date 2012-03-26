package org.jicengine.element;

import org.jicengine.JICException;

/**
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class ElementException extends JICException {

	public ElementException(String message, String elementName, Location location)
	{
		super(decorateMessage(message, elementName, location));
	}

	public ElementException(String message, String elementName, String attributeName, Location location)
	{
		super(message + " (<" + elementName + ">//" + attributeName + " at " + location + ")");
	}

	public ElementException(String message, Throwable cause, String elementName, Location location)
	{
		super(decorateMessage(message, elementName, location), cause);
	}

	public ElementException(Throwable cause, String elementName, Location location)
	{
		super(decorateMessage(cause.getMessage(), elementName, location), cause);
	}

	public ElementException(Throwable cause, String elementName, String attributeName, Location location)
	{
		super(cause.getMessage() + " (<" + elementName + ">//" + attributeName + " at " + location + ")", cause);
	}

	private static String decorateMessage(String message, String elementName, Location location)
	{
		return message + " (<" + elementName + "> at " + location + ")";
	}
}
