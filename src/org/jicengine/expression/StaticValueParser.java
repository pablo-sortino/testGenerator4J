package org.jicengine.expression;

import org.jicengine.operation.StaticValue;

import org.jicengine.operation.Operation;

/**
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public abstract class StaticValueParser implements Parser {

	public final Operation parse(String expression) throws SyntaxException
	{
		Object value = parseValue(expression);
		if( value != null ){
			return new StaticValue(value);
		}
		else {
			return null;
		}
	}

	protected abstract Object parseValue(String expression) throws SyntaxException;
}
