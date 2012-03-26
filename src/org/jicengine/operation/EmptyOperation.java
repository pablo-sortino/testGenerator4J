package org.jicengine.operation;


/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public class EmptyOperation implements Operation {

	public static final EmptyOperation INSTANCE = new EmptyOperation();

	public EmptyOperation()
	{
	}

	public boolean needsParameters()
	{
		return false;
	}

	public boolean needsParameter(String name)
	{
		return false;
	}

	public Object execute(Context context) throws OperationException
	{
		return null;
	}
}
