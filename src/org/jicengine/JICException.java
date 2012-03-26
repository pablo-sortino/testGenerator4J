package org.jicengine;

/**
 *
 *
 *
 *
 * @author .timo
 *
 */

public class JICException extends Exception {

	public JICException(String message)
	{
		super(message);
	}

	public JICException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public JICException(Throwable cause)
	{
		super(cause);
	}
}