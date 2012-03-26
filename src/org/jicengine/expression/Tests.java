package org.jicengine.expression;

import org.jicengine.expression.Utils;
import org.jicengine.operation.SimpleContext;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class Tests {

	public static class LJETest extends TestCase{
		protected void expressionEquals(String expression, Object expected) throws Exception
		{
			Context dummyContext = new SimpleContext("empty-context");
			Operation resultOp = LJEParser.getInstance().parse(expression);
			Object result = resultOp.execute(dummyContext);
			assertEquals(expected, result);
		}

		public void testNumbers() throws Exception
		{
			expressionEquals("123", new Integer(123));
			expressionEquals("-1", new Integer(-1));
			expressionEquals("0", new Integer(0));
			expressionEquals("1.23", new Double(1.23));
			expressionEquals("-1.01", new Double(-1.01));
			expressionEquals("0.0", new Double(0.0));
			expressionEquals("12345l", new Long(12345l));
			expressionEquals("-1L", new Long(-1L));
			expressionEquals("-11f", new Float(-11f));
		}

		public void testStrings() throws Exception
		{
			expressionEquals("'hello' ", "hello");
			expressionEquals("'hello()'", "hello()");
			expressionEquals("'(hel,lo)'", "(hel,lo)");
			expressionEquals("'h,e,l,l,o'", "h,e,l,l,o");
			expressionEquals("'h e l l o'", "h e l l o");
			expressionEquals("'-123'", "-123");
			expressionEquals("''", "");
		}

		public void testBoolean() throws Exception
		{
			expressionEquals("true ", new Boolean(true));
			expressionEquals("false ", new Boolean(false));
		}

	}

	public static class UtilsTest extends TestCase{
		/*
		public void doParseSignatureTest(String signature, String expectedType, String[] exprectedParams) throws Exception
		{
			Object[] result = Utils.parseSignature(signature);
			assertEquals("Testing the type in signature '" + signature + "'.","type", result[0]);
			Object[] params = (Object[]) result[1];
			java.util.List paramsList = java.util.Arrays.asList(params);
			java.util.List expectedParamsList = java.util.Arrays.asList(exprectedParams);
			assertEquals("Expected " + expectedParamsList.size() + " params " + expectedParamsList + ", got " + paramsList.size() + " params " + paramsList,exprectedParams.length, params.length );

			for (int i = 0; i < params.length; i++) {
				assertEquals("Testing param " + (i+1) + " in '" + signature + "'", exprectedParams[i], params[i]);
			}
		}
		*/

		/*
		public void testParseSignature() throws Exception
		{
			doParseSignatureTest(
				"type(param1,param2)",
				"type",
				new String[]{"param1","param2"}
			);

			doParseSignatureTest(
				"type('p()aram1',param2)",
				"type",
				new String[]{"'p()aram1'","param2"}
			);

			doParseSignatureTest(
				"type('a,b,c',param2)",
				"type",
				new String[]{"'a,b,c'","param2"}
			);

		}
		*/
	}

	public static Test createTests()
	{
		TestSuite suite = new TestSuite();

		// add here all the TestCase-classes that are to
		// be run.
		Class[] testClasses = new Class[]{
			LJETest.class,
			UtilsTest.class
		};

		for (int i = 0; i < testClasses.length; i++) {
			suite.addTestSuite(testClasses[i]);
		}


		return suite;
	}

	public static void main(String[] args) {
		//RunTests runTests1 = new RunTests();
		junit.textui.TestRunner.run(createTests());
	}
}
