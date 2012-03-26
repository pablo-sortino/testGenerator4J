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
public class BuildParameterParser implements Parser {

	public static final String PREFIX = "param::";

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
		if (expression.startsWith(PREFIX)) {
			char[] chars = expression.substring(PREFIX.length()+2).toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if( !Character.isLetterOrDigit(chars[i]) && chars[i] != '.' && chars[i] != '-' ){
					return null;
				}
			}
			return new VariableValueOperation(expression);
		}
		else {
			return null;
		}
	}
}
