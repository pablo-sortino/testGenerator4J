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

public class StaticValue extends ParameterlessOperation {

	Object value;

	public StaticValue(Object value)
	{
		this.value = value;
	}

	public Object execute(Context context)
	{
		return value;
	}
}
