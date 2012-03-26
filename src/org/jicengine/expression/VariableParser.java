package org.jicengine.expression;

import org.jicengine.operation.VariableValueOperation;
import org.jicengine.operation.Operation;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public class VariableParser implements Parser {

	public Operation parse(String expression) throws SyntaxException
	{
		char[] chars = expression.toCharArray();
		char character;

		if(Character.isJavaIdentifierStart(chars[0])) {
			for(int i = 1; i < chars.length; i++) {
				if(!Character.isJavaIdentifierPart(chars[i])) {
					return null;
				}
			}
			return new VariableValueOperation(expression);
		}
		else {
			return null;
		}


		/*
		if(Character.isLetter(chars[0]) && chars[0] != '-' ) {
			for(int i = 1; i < chars.length; i++) {
				character = chars[i];
				if(character == OPERATION_SEPARATOR) {
					return null;
				}
				else if(character == METHOD_PARAMS_START || character == METHOD_PARAMS_END) {
					return null;
				}
				else {
					// continue..
				}
			}
			return new VariableValueOperation(expression);
		}
		else {
			return null;
		}
		*/
	}
}
