package org.jicengine.expression;

import org.jicengine.operation.*;

/**
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * @author timo laitinen
 */
public class NegationParser implements Parser {
	private Parser parser;
	public NegationParser(Parser subParser)
	{
		this.parser = subParser;
	}

	/**
	 * @return Parsers may return null in order to signal 'abort' i.e.
	 *
	 * @return Parsers may return null in order to signal 'abort' i.e. if the
	 *   syntax of the expression is not understood by the parser
	 *   implementation. i.e. NumberParser returns null if the expression is a
	 *   string-expression..
	 * @throws SyntaxException
	 * @param expression String
	 */
	public Operation parse(String expression) throws SyntaxException
	{
		if( expression.startsWith("!") ){
			// looks promising..
			Operation operation = this.parser.parse(expression.substring(1));
			if( operation != null ){
				// looks still ok.
				return new NegationOperation(operation);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
}
