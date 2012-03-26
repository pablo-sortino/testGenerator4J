package org.jicengine.expression;

import org.jicengine.operation.Operation;

/**
 * for parsing string expressions into executable
 * operations.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public interface Parser {

	public static char OPERATION_SEPARATOR = new String(".").charAt(0);
	public static char METHOD_PARAMS_START = new String("(").charAt(0);
	public static char METHOD_PARAMS_END = new String(")").charAt(0);
	public static char ARRAY_LENGTH_START = new String("[").charAt(0);
	public static char ARRAY_LENGTH_END = new String("]").charAt(0);

	public static String BOOLEAN_TRUE = new String("true");
	public static String BOOLEAN_FALSE = new String("false");
	public static char STRING_MARKER = new String("'").charAt(0);

	/**
	 * @return Parsers may return null in order to signal 'abort' i.e.
	 *         if the syntax of the expression is not understood by
	 *         the parser implementation. i.e. NumberParser returns null
	 *         if the expression is a string-expression..
	 *
	 * @throws ExpressionException if the syntax of the expression looked
	 * like okay, but the parsing failed for some reason.
	 */
	public Operation parse(String expression) throws SyntaxException;

}
