package org.jtestcase.core.asserter;

import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Iterator;

import org.jtestcase.JTestCaseException;
import org.jtestcase.TestCaseInstance;
import org.jtestcase.core.digester.DigesterException;
import org.jtestcase.core.digester.JTestCaseDigester;
import org.jtestcase.core.model.AssertGroupInstance;
import org.jtestcase.core.model.AssertInstance;
import org.jtestcase.core.model.ParamGroupInstance;
import org.jtestcase.core.model.ParamInstance;
import org.jtestcase.core.type.ComplexTypeConverter;
import org.jtestcase.core.type.TypeConversionException;
import org.jtestcase.util.MultiKeyHashtable;

public class Asserter {

	/**
	 * The digester used for parsing the XML
	 */
	private JTestCaseDigester digester;

	/**
	 * Type convert facility. This class is used to map "string" representation
	 * of type to contrete instances of the type represented
	 */
	private ComplexTypeConverter typeConverter;

	public Asserter(JTestCaseDigester digester,
			ComplexTypeConverter typeConverter) {
		this.digester = digester;
		this.typeConverter = typeConverter;
	}

	/**
	 * <p>
	 * Asserts a given varible's value against its expected value by using
	 * expected action.
	 * </p>
	 *
	 * <pre>
	 *
	 *                         Following actions are defined. Expected action in data file should fall into one of them:
	 *                         ISNULL, ISNOTNULL, EQUALS, NOTEQUALS, GT (greater than), NOTGT (not greater than),
	 *                         LT (less than), NOTLT (not less than), and TRUE (expression is true).
	 *
	 *                         The expected asserting results are defined in data file like this:
	 *                         ...
	 *                         &lt;asserts&gt;
	 *                         &lt;assert name=&quot;var1&quot; action=&quot;EQUALS&quot; type=&quot;int&quot;&gt;100&lt;/assert&gt;
	 *                         &lt;assert name=&quot;var2&quot; action=&quot;NOTNULL&quot;/&gt;
	 *                         &lt;/asserts&gt;
	 *                         ...
	 * </pre>
	 *
	 * <p>
	 * Note: <code> &quot;GT&quot; means &quot;real value&quot; greater than
	 * &quot;expected value&quot;</code>
	 * This is similar to others.
	 * </p>
	 *
	 * @param varName -
	 *            name of varible. Should match the name defined in data file in
	 *            /test/class/method/asserts/assert@name.
	 * @param varValue -
	 *            actually value of this varible.
	 * @return boolean <code>true</code> if assertion is true,
	 *         <code>false</code> elseways.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 *
	 */
	public boolean assertParam(String className, String method,
			String testcase, String varName, Object varValue,
			TestCaseInstance tcInstance) throws JTestCaseException {

		MultiKeyHashtable assertActions = null;
		MultiKeyHashtable assertValues = null;

		try {
			assertActions = getTestCaseAssertActions(className, method,
					testcase);

			assertValues = getTestCaseAssertValues(className, method, testcase);

		} catch (InstantiationException e) {
			throw new JTestCaseException(e);
		} catch (IllegalAccessException e) {
			throw new JTestCaseException(e);
		}

		Iterator keysIter = assertActions.keySet().iterator();

		tcInstance.getAssertFailureReason().setTestCaseInstance(tcInstance.getTestCaseName());

		tcInstance.getAssertFailureReason().setVariable( varName);


		if(!keysIter.hasNext())
			return true ; // no assertion are TRUE

		boolean returnValue = true;


		// FIXME: ("no match for assert : " + varName);

		while (keysIter.hasNext() && returnValue) {
			// and empty assert is considered a fail

			String[] keys = (String[]) keysIter.next();
			if (keys[0].compareTo(varName) == 0) {

				String action = keys[1];

				// TODO: maybe a failing test may suffice

				if (action == null) {
					throw new JTestCaseException("***Error: variable "
							+ varName
							+ " not defined in data file for this test case.");
				}

				tcInstance.getAssertFailureReason().setCriteria(action);

				if (action.equalsIgnoreCase("ISNULL")) {
					tcInstance.
					getAssertFailureReason().setNotNull(true);
					returnValue = (varValue == null);
					continue;
				}
				// passed this point, the asserted value must be not null
				// else the failure is issued
				else if (varValue == null) {
					tcInstance.
					getAssertFailureReason().setNotNullOnNullObject(true);
					// bad enough to consider the assertion failed
					return false;
				} else if (action.equalsIgnoreCase("ISNOTNULL")) {
					tcInstance.
					getAssertFailureReason().setNullOnNonNullObject(true);

					returnValue = (varValue != null);
					continue;
				} else if (action.equalsIgnoreCase("ISTRUE")) {
					try {
						returnValue = ((Boolean) varValue).booleanValue();
					} catch (ClassCastException cce) {
						throw new JTestCaseException(
								"ClassCastExcepton : assert ISTRUE only works with boolean values ");
					}
					continue;
				}

				// Next assert need to get the expected value

				Object assertParameter = assertValues.get(keys);


				// Intialize the failure reason
				tcInstance.getAssertFailureReason().setCriteria(action);
				tcInstance.getAssertFailureReason().setTestValue(varValue.toString());
				tcInstance.getAssertFailureReason().setExpectedValue(assertParameter.toString());


				if (action.equalsIgnoreCase("EQUALS")) {
					returnValue = varValue.equals(assertParameter);
				} else if (action.equalsIgnoreCase("NOTEQUALS")) {
					returnValue = !varValue.equals(assertParameter);
				}

				else if (action.equalsIgnoreCase("GT")) {
					returnValue = ((new Double(varValue.toString()))
							.doubleValue() > (new Double(assertParameter
							.toString())).doubleValue());
				} else if (action.equalsIgnoreCase("NOTGT")) {
					returnValue = ((new Double(varValue.toString()))
							.doubleValue() <= (new Double(assertParameter
							.toString())).doubleValue());
				} else if (action.equalsIgnoreCase("LT")) {
					returnValue = ((new Double(varValue.toString()))
							.doubleValue() < (new Double(assertParameter
							.toString())).doubleValue());
				} else if (action.equalsIgnoreCase("NOTLT")) {
					returnValue = ((new Double(varValue.toString()))
							.doubleValue() >= (new Double(assertParameter
							.toString())).doubleValue());
				} else if (action.equalsIgnoreCase("ISTYPEOF")) {
					String valueClassName = varValue.getClass()
							.getName();
					String expectedClassName = assertParameter.getClass()
							.getName();
					returnValue = (valueClassName
							.equalsIgnoreCase(expectedClassName));
				} else {
					throw new JTestCaseException(
							"***Error: asserting action is not valid!");
				}
			}
		}
		return returnValue;
	}

	/**
	 * Get all assert values for a given method and its test case value into
	 * Hashtable. Hashed key is assert param's name, value is String value of
	 * assert param.
	 *
	 * Key is assert param's name defined in
	 * /tests/class/method/asserts/assert@name, value is assert param's Object
	 * value with type as indecated in /test/class/method/asserts/assert@type.
	 * If not "type" specified in data file, then "String" is default type.
	 *
	 * @return MultiKeyHashtable.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public MultiKeyHashtable getTestCaseAssertValues(String className,
			String method, String testcase) throws JTestCaseException,
			InstantiationException, IllegalAccessException {
		MultiKeyHashtable assertValues = new MultiKeyHashtable();
		try {
			Iterator assertInstances = digester.getAssertsInstances(className,
					method, testcase).iterator();
			while (assertInstances.hasNext()) {
				AssertInstance assertInstance = (AssertInstance) assertInstances
						.next();
				String[] key = new String[2];
				key[0] = assertInstance.getName();
				key[1] = assertInstance.getAction();
				// TODO: the following is VERY UGLY PATCH
				Object value = null;
				if (!key[1].equalsIgnoreCase("ISTRUE"))
					value = typeConverter._convertType(assertInstance);
				else
					value = new Object(); // i will not use this
				assertValues.put(key, value);
			}
			Iterator assertGroupInstances = digester.getAssertGroupInstances(
					className, method, testcase).iterator();
			while (assertGroupInstances.hasNext()) {
				AssertGroupInstance assertGroupInstance = (AssertGroupInstance) assertGroupInstances
						.next();
				_getAssertValuesFromGroupInstance(
						assertGroupInstance.getName(), assertValues,
						assertGroupInstance);
			}
		} catch (TypeConversionException tce) {
			throw new JTestCaseException("Error converting assert to Java type");
		} catch (DigesterException e) {
			throw new JTestCaseException("Error retrieving asserts.", e);
		} catch (FileNotFoundException de) {
			throw new JTestCaseException("Cannot read xml file : \n"
					+ de.getMessage());
		}
		return assertValues;
	}

	protected MultiKeyHashtable getTestCaseAssertActions(String className,
			String method, String testcase) throws JTestCaseException {
		MultiKeyHashtable assertActions = new MultiKeyHashtable();
		try {
			Iterator assertInstances = digester.getAssertsInstances(className,
					method, testcase).iterator();
			while (assertInstances.hasNext()) {
				AssertInstance assertInstance = (AssertInstance) assertInstances
						.next();
				String[] key = new String[2];
				key[0] = assertInstance.getName();
				key[1] = assertInstance.getAction();
				assertActions.put(key, assertInstance.getAction());
			}
			Iterator assertGroupInstances = digester.getAssertGroupInstances(
					className, method, testcase).iterator();
			while (assertGroupInstances.hasNext()) {
				AssertGroupInstance assertGroupInstance = (AssertGroupInstance) assertGroupInstances
						.next();
				_getAssertActionsFromGroupInstance(assertGroupInstance
						.getName(), assertActions, assertGroupInstance);
			}
		} catch (DigesterException e) {
			throw new JTestCaseException("Error retrieving asserts.");
		} catch (FileNotFoundException de) {
			throw new JTestCaseException("Cannot read xml file : \n"
					+ de.getMessage());
		}

		return assertActions;
	}

	/**
	 * Extracts the assert actions from a AssertGroupInstance
	 *
	 * @param path
	 *            the path so far
	 * @param flatAsserts
	 *            the hashtable with the asserts
	 * @param assertGroupInstance
	 *            the AssertGroupInstance object to be analysed
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 */
	protected void _getAssertActionsFromGroupInstance(String path,
			MultiKeyHashtable flatAsserts,
			AssertGroupInstance assertGroupInstance) throws JTestCaseException {
		Iterator assertIter = assertGroupInstance.getAssertInstances()
				.iterator();
		while (assertIter.hasNext()) {
			AssertInstance asert = (AssertInstance) assertIter.next();
			String[] key = new String[2];
			key[0] = path + "/" + asert.getName();
			key[1] = asert.getAction();
			flatAsserts.put(key, asert.getAction());
		}
		Iterator assertGroupIter = assertGroupInstance
				.getAssertGroupInstances().iterator();
		while (assertGroupIter.hasNext()) {
			AssertGroupInstance assertGroup = (AssertGroupInstance) assertGroupIter
					.next();
			_getAssertActionsFromGroupInstance(path + "/"
					+ assertGroup.getName(), flatAsserts, assertGroup);
		}
	}

	/**
	 * Extracts the param types from a ParamGroupInstance
	 *
	 * @param path
	 *            the path so far
	 * @param flatParams
	 *            the hashtable with the params
	 * @param paramGroupInstance
	 *            the ParamGroupInstance object to be analysed
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 */
	protected void _getParamTypesFromGroupInstance(String path,
			Hashtable flatParams, ParamGroupInstance paramGroupInstance)
			throws JTestCaseException {
		Iterator paramIter = paramGroupInstance.getParamInstances().iterator();
		while (paramIter.hasNext()) {
			ParamInstance param = (ParamInstance) paramIter.next();
			flatParams.put(path + "/" + param.getName(), param.getType());
		}
		Iterator paramGroupIter = paramGroupInstance.getParamGroupInstances()
				.iterator();
		while (paramGroupIter.hasNext()) {
			ParamGroupInstance paramGroup = (ParamGroupInstance) paramGroupIter
					.next();
			_getParamTypesFromGroupInstance(path + "/" + paramGroup.getName(),
					flatParams, paramGroup);
		}
	}

	/**
	 * Extracts the assert values from a AssertGroupInstance
	 *
	 * @param path
	 *            the path so far
	 * @param flatAsserts
	 *            the hashtable with the asserts
	 * @param assertGroupInstance
	 *            the AssertGroupInstance object to be analysed
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected void _getAssertValuesFromGroupInstance(String path,
			MultiKeyHashtable flatAsserts,
			AssertGroupInstance assertGroupInstance) throws JTestCaseException,
			InstantiationException, IllegalAccessException {
		Iterator assertIter = assertGroupInstance.getAssertInstances()
				.iterator();
		try {
			while (assertIter.hasNext()) {
				AssertInstance asert = (AssertInstance) assertIter.next();
				Object value = typeConverter._convertType(asert);
				String[] key = new String[2];
				key[0] = path + "/" + asert.getName();
				key[1] = asert.getAction();
				flatAsserts.put(key, value);
			}
			Iterator assertGroupIter = assertGroupInstance
					.getAssertGroupInstances().iterator();
			while (assertGroupIter.hasNext()) {
				AssertGroupInstance assertGroup = (AssertGroupInstance) assertGroupIter
						.next();
				_getAssertValuesFromGroupInstance(path + "/"
						+ assertGroup.getName(), flatAsserts, assertGroup);
			}
		} catch (TypeConversionException tce) {

			throw new JTestCaseException("Error converting param to Java type");

		}
	}

	/**
	 * Extracts the assert types from a AssertGroupInstance
	 *
	 * @param path
	 *            the path so far
	 * @param flatAsserts
	 *            the hashtable with the asserts
	 * @param assertGroupInstance
	 *            the AssertGroupInstance object to be analysed
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 */
	protected void _getAssertTypesFromGroupInstance(String path,
			MultiKeyHashtable flatAsserts,
			AssertGroupInstance assertGroupInstance) throws JTestCaseException {
		Iterator assertIter = assertGroupInstance.getAssertInstances()
				.iterator();
		while (assertIter.hasNext()) {
			AssertInstance asert = (AssertInstance) assertIter.next();
			String[] key = new String[2];
			key[0] = path + "/" + asert.getName();
			key[1] = asert.getAction();
			flatAsserts.put(key, asert.getType());
		}
		Iterator assertGroupIter = assertGroupInstance
				.getAssertGroupInstances().iterator();
		while (assertGroupIter.hasNext()) {
			AssertGroupInstance assertGroup = (AssertGroupInstance) assertGroupIter
					.next();
			_getAssertTypesFromGroupInstance(
					path + "/" + assertGroup.getName(), flatAsserts,
					assertGroup);
		}
	}

}
