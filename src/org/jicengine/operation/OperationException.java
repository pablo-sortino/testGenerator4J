package org.jicengine.operation;

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

public class OperationException extends Exception {

	public OperationException(String message)
	{
		super(message);
	}

	public OperationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
