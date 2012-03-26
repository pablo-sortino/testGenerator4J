package org.jicengine.operation;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class UnexecutableOperation implements Operation {
	String explanation;

	public UnexecutableOperation()
	{
		this("execution not allowed.");
	}

	public UnexecutableOperation(String explanation) {
		this.explanation = explanation;
	}


	public boolean needsParameters() {
		return false;
	}

	public boolean needsParameter(String name)
	{
		return false;
	}

	public Object execute(Context context) throws OperationException
	{
		throw new OperationException(this.explanation);
	}
}
