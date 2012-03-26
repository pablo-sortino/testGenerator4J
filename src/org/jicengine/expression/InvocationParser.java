package org.jicengine.expression;

import org.jicengine.operation.MethodInvocationOperation;
import org.jicengine.operation.ObjectInstantiationOperation;
import org.jicengine.operation.ArrayConstructionOperation;
import org.jicengine.operation.Operation;
import java.util.*;

/**
 * <p>
 * Parses both method and constructor invocations.
 * </p>
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public class InvocationParser implements Parser {

	private static final String NEW_OPERATOR = "new ";
	private static final String ARGUMENTS_PART_START = "(";
	private static final String ARGUMENTS_PART_END = ")";
	private static final String ARRAY_BRACKET_START = "[";
	private static final String ARRAY_BRACKET_END = "]";
	private static final String DOT = ".";

	private Parser elementParser;

	public InvocationParser(Parser elementParser)
	{
		this.elementParser = elementParser;
	}

	public Operation parse(String expression) throws SyntaxException
	{
		int argumentsPartStart;

		if( expression.startsWith(NEW_OPERATOR) ){
			// a 'new' operation, either:
			// a) 'new full.class.name(parameters)'
			// b) 'new full.class.name[arraySize]'

			argumentsPartStart = expression.indexOf(ARGUMENTS_PART_START);

			if ( argumentsPartStart != -1 ){
				// not an array
				// extract class-name, like 'java.util.Date' from expression 'new java.util.Date()'.
				String className = expression.substring(4,argumentsPartStart);
				String argumentsList = extractArguments(expression,argumentsPartStart);
				return new ObjectInstantiationOperation(expression, elementParser.parse(className), parseArguments(this.elementParser, argumentsList));
			}
			else {
				int arrayLengthStart = expression.indexOf(ARRAY_BRACKET_START);

				if( arrayLengthStart != -1 ){
					// looks like an array creation..
					String componentType = expression.substring(4,arrayLengthStart);
					String length = expression.substring(arrayLengthStart+1, expression.lastIndexOf(ARRAY_BRACKET_END));
					return new ArrayConstructionOperation(expression, elementParser.parse(componentType), elementParser.parse(length));
				}
				else {
					throw new SyntaxException("expected 'new full.class.name(parameters)' or 'new full.class.name[arraySize]', got '" + expression + "'");
				}
			}
		}
		else if( (argumentsPartStart = expression.indexOf(ARGUMENTS_PART_START) ) != -1 ){
			// if the 'new' operator is not used, then some method is called, either a static
			// or a method of some instance.

			// extract the part before "()" marks.
			String beforeMethodArguments = expression.substring(0, argumentsPartStart);

			// extract the actor-part, a name of a variable or a class
			int lastDotIndex = beforeMethodArguments.lastIndexOf(DOT);
			String actorName = beforeMethodArguments.substring(0,lastDotIndex);
			String method = beforeMethodArguments.substring(lastDotIndex+1);

			Operation actor = elementParser.parse(actorName);

			Operation[] arguments = parseArguments(this.elementParser, extractArguments(expression, argumentsPartStart));

			return new MethodInvocationOperation(expression, actor, method, arguments);
		}
		else {
			return null;
		}
	}

	public static Operation[] parseArguments(Parser parser, String argumentsExpression) throws SyntaxException {

		String[] params = Utils.parseArgumentlist(argumentsExpression);
		Operation[] paramsAsOperations = new Operation[params.length];
		for (int i = 0; i < params.length; i++) {
			paramsAsOperations[i] = parser.parse(params[i]);
		}
		return paramsAsOperations;
	}

		/**
		 * extracts arguments-list from an expression.
		 * Like 'param1,param2' from expression new full.package.name.ClassName(param1,param2)'
		 */
		private static String extractArguments(String expression, int argumentsPartStart) throws SyntaxException
		{
			try {
				return expression.substring(argumentsPartStart+1, expression.lastIndexOf(ARGUMENTS_PART_END));
			} catch (StringIndexOutOfBoundsException e){
				throw new SyntaxException("Expression '" + expression + "' doesn't have valid arguments-part enclosed inside ()");
			}
		}

}
