package org.jicengine.expression;

import org.jicengine.operation.FieldValueOperation;

import org.jicengine.operation.Operation;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public class FieldValueParser implements Parser {

	Parser elementParser;

	public FieldValueParser(Parser elementParser)
	{
		this.elementParser = elementParser;
	}

	public Operation parse(String expression) throws SyntaxException
	{
		char[] chars = expression.toCharArray();
		int fieldNameSeparatorIndex = -1;
		for (int i = chars.length-1; 0 <= i; i--) {
			if( chars[i] == METHOD_PARAMS_END || chars[i] == METHOD_PARAMS_START || chars[i] == ARRAY_LENGTH_START || chars[i] == ARRAY_LENGTH_END){
				// the existence of method-param-brackets signals  that this isn't
				// a field-value-expression.
				break;
			}
			else if( chars[i] == OPERATION_SEPARATOR ){
				// we found the field-name
				fieldNameSeparatorIndex = i;
				break;
			}
			else {
				continue;
			}
		}

		if( fieldNameSeparatorIndex != -1 ){
			// extract the actor-part (= a name of a variable or a class ).
			String actorName = expression.substring(0, fieldNameSeparatorIndex);
			String fieldName = expression.substring(fieldNameSeparatorIndex+1);

			Operation actor = elementParser.parse(actorName);

			return new FieldValueOperation(expression, actor, fieldName);
		}
		else {
			// no field name found, I guess the
			// expression wasn't a field-value expression.
			return null;
		}
	}
}
