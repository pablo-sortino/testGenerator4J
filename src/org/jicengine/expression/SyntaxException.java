package org.jicengine.expression;


/**
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class SyntaxException extends Exception {

	public SyntaxException(String message)
	{
		super(message);
	}

	public SyntaxException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
