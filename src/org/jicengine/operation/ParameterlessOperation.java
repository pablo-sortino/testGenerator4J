package org.jicengine.operation;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public abstract class ParameterlessOperation implements Operation {

	public boolean needsParameters()
	{
		return false;
	}

	public boolean needsParameter(String name)
	{
		return false;
	}


}
