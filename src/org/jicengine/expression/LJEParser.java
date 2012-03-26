package org.jicengine.expression;

import org.jicengine.operation.Operation;

/**
 * 'Limited Java Expression'-parser.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class LJEParser implements Parser {

	private static LJEParser instance = new LJEParser();

	public static LJEParser getInstance()
	{
		return instance;
	}

	Parser parser;

	private LJEParser() {
		// the order of the sub-parsers matters!

		// these atoms can be part of a FieldValue- or
		// MethodInvocation-expressions
		Parser atomParser = new CompositeParser(new Parser[]{
			new VariableParser(),
			new ClassParser()
		});

		// the negated expression may be any expression.
		// therefore we give the negationparser a
		// CompositeParser instance.

		Parser negationParser = new NegationParser(
			new CompositeParser(new Parser[]{
				new BuildParameterParser(),
				new VariableParser(),
				// note: no class-parser here
				new FieldValueParser(atomParser),
				new InvocationParser(atomParser)
			}));

		Parser factoryInvocationParser = new FactoryInvocationParser(new VariableParser());

		// these parsers define the actual LJE-expressions.
		parser = new CompositeParser(new Parser[]{
			negationParser,
			new BuildParameterParser(),
			factoryInvocationParser,
			new VariableParser(),
			// note: no class-parser here
			new FieldValueParser(atomParser),
			new InvocationParser(atomParser)
		});
	}

	public Operation parse(String expression) throws SyntaxException
	{
		return this.parser.parse(expression);
	}
}
