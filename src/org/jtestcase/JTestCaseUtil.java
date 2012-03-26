/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" packaged
 * 
 */
package org.jtestcase;

import java.util.Vector;

import org.jtestcase.core.digester.DigesterException;
import org.jtestcase.core.digester.JTestCaseDigester;
import org.jtestcase.core.type.ComplexTypeConverter;

/**
 * Utility class for JTestCase instances
 * 
 * 
 * @author <a href="mailto:faustothegrey@sourceforge.net">Fausto Lelli</a>
 * @author <a href="mailto:yuqingwang_99@yahoo.com">Yuqing Wang</a>
 * @author <a href="mailto:ckoelle@sourceforge.net">Christian Koelle</a>
 * 
 * $Id: JTestCaseUtil.java,v 1.4 2005/10/15 12:27:55 faustothegrey Exp $
 */

public class JTestCaseUtil {

	/**
	 * The digester used for parsing the XML
	 */
	private JTestCaseDigester digester;

	/**
	 * Type convert facility. This class is used to map "string" representation
	 * of type to contrete instances of the type represented
	 */
	private ComplexTypeConverter typeConverter;

	/**
	 * Type convert facility. This class is used to map "string" representation
	 * of type to contrete instances of the type represented
	 */
	private JTestCase jtestcase;

	/**
	 * The class name which is used to find the class tag in XML
	 */
	private String mClassName = null;

	public JTestCaseUtil(JTestCase jtestcase ) {
		super();
		digester = jtestcase.getDigester();
		typeConverter = jtestcase.getTypeConverter();
		this.jtestcase = jtestcase;
	}

	/**
	 * Returns the identifiers of all testcases (in all methods) in this
	 * JTestCase instance
	 * 
	 * 
	 * @return all test case identifiers in a vector
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 */
	public Vector getNameOfTestCases(JTestCase jtestcase) throws JTestCaseException {
		Vector testcases;
		try {
			testcases = digester.getTestCases(mClassName, jtestcase);
		} catch (DigesterException e) {
			throw new JTestCaseException(e.getMessage());
		}
		return testcases;
	}

	/**
	 * Get all test cases' name for a given method into Vector. This method
	 * requires that for a given method, each test case should be named
	 * uniquely.
	 * 
	 * @deprecated
	 * @param methodName -
	 *            name of tested method. Defined in data file in
	 *            /tests/class/method@test-case.
	 * @return Vector of names of test cases that are defined for this method in
	 *         data file in /tests/class/method@test-case.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 */
	public Vector getNameOfTestCases(String methodName)
			throws JTestCaseException {
		Vector testcases;
		try {
			testcases = digester.getTestCasesPerMethod(methodName);
		} catch (DigesterException e) {
			throw new JTestCaseException(e.getMessage());
		}
		return testcases;
	}
	
	/**
	 * Get all params for a given method and its test case value into Hashtable.
	 * Hashed key is param's name, value is value of param. This method is
	 * normally used with getNameOfTestCases().
	 * 
	 * @param methodName -
	 *            name of tested method. Defined in data file in
	 *            /tests/class/method@name.
	 * @param testCase -
	 *            name of test case. Defined in data file in
	 *            /tests/class/method@test-case. Should be unique for the given
	 *            "methodName".
	 * @return Hashtable. Key is param's name defined in
	 *         /tests/class/method/params/param@name, value is param's Object
	 *         value with type as indecated in
	 *         /test/class/method/params/param@type. If not "type" specified in
	 *         data file, then "String" is default type.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	public Hashtable getTestCaseParams(String methodName, String testCase)
			throws JTestCaseException {
		Hashtable paramValues = new Hashtable();
		try {
			Iterator paramInstances = digester.getParamsInstances(mClassName,
					methodName, testCase).iterator();
			while (paramInstances.hasNext()) {
				ParamInstance paramInstance = (ParamInstance) paramInstances
						.next();
				Object value = typeConverter._convertType(paramInstance);
				paramValues.put(paramInstance.getName(), value);
			}
			Iterator paramGroupInstances = digester.getParamGroupInstances(
					mClassName, methodName, testCase).iterator();
			while (paramGroupInstances.hasNext()) {
				ParamGroupInstance paramGroupInstance = (ParamGroupInstance) paramGroupInstances
						.next();
				_getParamValuesFromGroupInstance(paramGroupInstance.getName(),
						paramValues, paramGroupInstance);
			}
		} catch (TypeConversionException tce) {
			throw new JTestCaseException(tce.getMessage());
		} catch (DigesterException de) {
			throw new JTestCaseException(
					"Error retrieving params in xml file : \n"
							+ de.getMessage());
		}
		return paramValues;
	}

	 */

	/**
	 * Get types of all params for a given method and its test case value into
	 * Hashtable. Hashed key is param's name, value is String value of param
	 * type, which conforms to java class name. This method is normally used
	 * with getNameOfTestCases().
	 * 
	 * @param methodName -
	 *            name of tested method. Defined in data file in
	 *            /tests/class/method@name.
	 * @param testCase -
	 *            name of test case. Defined in data file in
	 *            /tests/class/method@test-case. Should be unique for the given
	 *            "methodName".
	 * @return Hashtable. Key is param's name defined in
	 *         /tests/class/method/params/param@name, value is param's type
	 *         value defined in /test/class/method/params/param@type.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 
	private Hashtable getTestCaseParamTypes(String methodName, String testCase)
			throws JTestCaseException {
		Hashtable paramTypes = new Hashtable();
		try {
			Iterator paramInstances = digester.getParamsInstances(mClassName,
					methodName, testCase).iterator();
			while (paramInstances.hasNext()) {
				ParamInstance paramInstance = (ParamInstance) paramInstances
						.next();
				paramTypes
						.put(paramInstance.getName(), paramInstance.getType());
			}
			Iterator paramGroupInstances = digester.getParamGroupInstances(
					mClassName, methodName, testCase).iterator();
			while (paramGroupInstances.hasNext()) {
				ParamGroupInstance paramGroupInstance = (ParamGroupInstance) paramGroupInstances
						.next();
				_getParamTypesFromGroupInstance(paramGroupInstance.getName(),
						paramTypes, paramGroupInstance);
			}
		} catch (DigesterException e) {
			throw new JTestCaseException("Error retrieving params.");
		}
		return paramTypes;
	}

	*/
	
	/**
	 * Get the value of the specific paramater given his name for a specific
	 * methodName and a specific testCase.
	 * 
	 * @param methodName -
	 *            name of tested method. Defined in data file in
	 *            /tests/class/method@name.
	 * @param testCase -
	 *            name of test case. Defined in data file in
	 *            /tests/class/method@test-case. Should be unique for the given
	 *            "methodName".
	 * @param parameter
	 *            the name of the parameter to be read.
	 * @return the value of the specific parameter for the specific method and
	 *         testCase.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @since 2.1.3
	
	public Object getTestCaseParameter(String methodName, String testCase,
			String parameter) throws JTestCaseException {
		Hashtable paramValues = getTestCaseParams(methodName, testCase);
		return paramValues.get(parameter);
	}
	*/ 
	
	/**
	 * Get types of assert values for a given method and its test case value
	 * into Hashtable. Hashed key is assert param's name, value is String value
	 * of type of assert param. This type is name of java class. This method
	 * does not return the types of assert values in complex data types. This
	 * means if you have an assert value of type "java.util.Hashtable" this
	 * method returns exactly this string and not a hashtable with the data
	 * types of the items in the hashtable. This method is normally used with
	 * getNameOfTestCases().
	 * 
	 * @param methodName -
	 *            name of tested method. Defined in data file in
	 *            /tests/class/method@test-case.
	 * @param testCase -
	 *            name of test case.Defined in data file in
	 *            /tests/class/method@test-case. Should be unique for the given
	 *            "methodName".
	 * @return Hashtable. Key is assert param's name defined in
	 *         /tests/class/method/asserts/assert@name, value is assert param's
	 *         type value as indecated in
	 *         /test/class/method/asserts/assert@type.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	
	public MultiKeyHashtable getTestCaseAssertTypes(String methodName,
			String testCase) throws JTestCaseException {
		MultiKeyHashtable assertTypes = new MultiKeyHashtable();
		try {
			Iterator assertInstances = digester.getAssertsInstances(mClassName,
					methodName, testCase).iterator();
			while (assertInstances.hasNext()) {
				AssertInstance assertInstance = (AssertInstance) assertInstances
						.next();
				String[] key = new String[2];
				key[0] = assertInstance.getName();
				key[1] = assertInstance.getAction();
				assertTypes.put(key, assertInstance.getType());
			}
			Iterator assertGroupInstances = digester.getAssertGroupInstances(
					mClassName, methodName, testCase).iterator();
			while (assertGroupInstances.hasNext()) {
				AssertGroupInstance assertGroupInstance = (AssertGroupInstance) assertGroupInstances
						.next();
				_getAssertTypesFromGroupInstance(assertGroupInstance.getName(),
						assertTypes, assertGroupInstance);
			}
		} catch (DigesterException e) {
			throw new JTestCaseException("Error retrieving asserts.");
		}
		return assertTypes;
	}

	 */
	

}
