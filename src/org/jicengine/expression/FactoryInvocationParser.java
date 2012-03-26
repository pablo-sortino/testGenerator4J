package org.jicengine.expression;

import org.jicengine.operation.*;
import org.jicengine.element.impl.FactoryElementCompiler;
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
public class FactoryInvocationParser implements Parser {

	public static final String FACTORY_NAME_PREFIX = "jic::";

	private Parser argumentsParser;

	public FactoryInvocationParser(Parser argumentsParser)
	{
		this.argumentsParser = argumentsParser;
	}

	/**
	 * @param expression String
	 */
	public Operation parse(String expression) throws SyntaxException
	{
		if( expression.startsWith(FACTORY_NAME_PREFIX) ){
			// looks promising..
			int parameterStart = expression.indexOf(InvocationParser.METHOD_PARAMS_START);
			int parameterEnd = expression.indexOf(InvocationParser.METHOD_PARAMS_END);

			String factoryName = expression.substring(0,parameterStart);
			String argumentsExpression = expression.substring(parameterStart+1,parameterEnd);

			Operation[] arguments = InvocationParser.parseArguments(this.argumentsParser, argumentsExpression);

			return new FactoryInvocationOperation(factoryName, arguments);
		}
		else {
			return null;
		}
	}

}
