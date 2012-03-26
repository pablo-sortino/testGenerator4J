package org.jicengine.expression;

import org.jicengine.operation.Operation;

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

public class CompositeParser implements Parser {

	private Parser[] parsers;
	private boolean unparsedExpressionIsError = true;

	public CompositeParser(Parser[] parsers)
	{
		this.parsers = parsers;
	}

	public Operation parse(String expression) throws SyntaxException {
		if( expression == null ){
			throw new SyntaxException("Expression was null");
		}

		expression = expression.trim();
		if( expression.length() == 0){
			throw new SyntaxException("Empty expression can't be evaluated.");
		}

		// try the parsers one at a time.
		// we assume that the first parser to return a non-null value
		// was the right parser.
		Parser parser;
		Operation result = null;
		for (int i = 0; i < parsers.length; i++) {
			parser = parsers[i];
			try {
				result = parser.parse(expression);
				if( result != null ){
					// ok
					break;
				}
			} catch (SyntaxException e){
				throw e;
			} catch (Exception e2){
				throw new SyntaxException("Problems parsing '" + expression + "' with parser " + parser, e2);
			}
		}

		if( result == null ){
			if( this.unparsedExpressionIsError ){
				throw new SyntaxException("Illegal expression: '" + expression + "'");
			}
			else {
				return null;
			}
		}
		else {
			return result;
		}

	}
}
