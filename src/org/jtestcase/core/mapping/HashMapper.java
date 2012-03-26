package org.jtestcase.core.mapping;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

import org.jtestcase.JTestCaseException;
import org.jtestcase.core.digester.DigesterException;
import org.jtestcase.core.digester.JTestCaseDigester;
import org.jtestcase.core.model.AssertParamGroupInstance;
import org.jtestcase.core.model.AssertParamInstance;
import org.jtestcase.core.model.ParamGroupInstance;
import org.jtestcase.core.model.ParamInstance;
import org.jtestcase.core.type.ComplexTypeConverter;
import org.jtestcase.core.type.TypeConversionException;
import org.jtestcase.util.MultiKeyHashtable;

public class HashMapper {

	/**
	 * The digester used for parsing the XML
	 */
	private JTestCaseDigester digester;

	/**
	 * Type convert facility. This class is used to map "string" representation
	 * of type to contrete instances of the type represented
	 */
	private ComplexTypeConverter typeConverter;

	public HashMapper(JTestCaseDigester digester,
			ComplexTypeConverter typeConverter) {
		this.digester = digester;
		this.typeConverter = typeConverter;
	}

	/**
	 * <p>
	 * Get all params for a given TestCaseInstance. Hashed key is param's name,
	 * value is value of param. This method is normally used with
	 * getNameOfTestCases().
	 * </p>
	 * <p>
	 * HashMap's key is param's name defined in
	 * /tests/class/method/params/param@name,
	 * </p>
	 * <p>
	 * HashMap's value is param's Object value with type as indicated in
	 * /test/class/method/params/param@type.
	 * </p>
	 * 
	 * @return HashMap.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public HashMap getTestCaseParams(String className, String method,
			String testcase) throws JTestCaseException, InstantiationException, IllegalAccessException {
		HashMap paramValues = new HashMap();
		try {
			Iterator paramInstances = digester.getParamsInstances(className,
					method, testcase).iterator();
			while (paramInstances.hasNext()) {
				ParamInstance paramInstance = (ParamInstance) paramInstances
						.next();
				Object value = typeConverter._convertType(paramInstance);
				paramValues.put(paramInstance.getName(), value);
			}
			Iterator paramGroupInstances = digester.getParamGroupInstances(
					className, method, testcase).iterator();
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
		} catch (FileNotFoundException de) {
			throw new JTestCaseException("Cannot read xml file : \n"
					+ de.getMessage());
		}

		return paramValues;
	}

	/**
	 * Extracts the param values from a ParamGroupInstance
	 * 
	 * @param path
	 *            the path so far
	 * @param flatParams
	 *            the hashtable with the params
	 * @param paramGroupInstance
	 *            the ParamGroupInstance object to be analysed
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void _getParamValuesFromGroupInstance(String path,
			HashMap flatParams, ParamGroupInstance paramGroupInstance)
			throws JTestCaseException, InstantiationException, IllegalAccessException {
		Iterator paramIter = paramGroupInstance.getParamInstances().iterator();
		try {
			while (paramIter.hasNext()) {
				ParamInstance param = (ParamInstance) paramIter.next();
				Object value = typeConverter._convertType(param);
				flatParams.put(path + "/" + param.getName(), value);
			}
			Iterator paramGroupIter = paramGroupInstance
					.getParamGroupInstances().iterator();
			while (paramGroupIter.hasNext()) {
				ParamGroupInstance paramGroup = (ParamGroupInstance) paramGroupIter
						.next();
				_getParamValuesFromGroupInstance(path + "/"
						+ paramGroup.getName(), flatParams, paramGroup);
			}
		} catch (TypeConversionException tce) {
			throw new JTestCaseException(tce.getMessage());
		}
	}

	/**
	 * Get all assert params for a given method and its test case value into
	 * Hashtable. Hashed key is assert param's name, value is String value of
	 * assert param. This method is normally used with getNameOfTestCases().
	 * 
	 * @param method -
	 *            name of tested method. Defined in data file in
	 *            /tests/class/method@test-case.
	 * @param testcase -
	 *            name of test case. Defined in data file in
	 *            /tests/class/method@test-case. Should be unique for the given
	 *            "methodName".
	 * @return Hashtable. Key is assert param's name defined in
	 *         /tests/class/method/asserts/assertparm@name, value is assert
	 *         param's Object value with type as indecated in
	 *         /test/class/method/asserts/assert@type. If not "type" specified
	 *         in data file, then "String" is default type.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws FileNotFoundException
	 */
	public MultiKeyHashtable getTestCaseAssertParams(String className, String method, String testcase)
			throws JTestCaseException, InstantiationException, IllegalAccessException {
		MultiKeyHashtable assertValues = new MultiKeyHashtable();
		try {
			Iterator assertInstances = digester.getAssertsParamInstances(
					className, method, testcase).iterator();
			while (assertInstances.hasNext()) {
				AssertParamInstance assertInstance = (AssertParamInstance) assertInstances
						.next();
				String[] key = new String[2];
				key[0] = assertInstance.getName();
				key[1] = assertInstance.getAction();
				Object value = typeConverter._convertType(assertInstance);
				assertValues.put(key, value);
			}
			Iterator assertParamGroupInstances = digester
					.getAssertParamGroupInstances(className, method, testcase)
					.iterator();
			while (assertParamGroupInstances.hasNext()) {
				AssertParamGroupInstance assertParamGroupInstance = (AssertParamGroupInstance) assertParamGroupInstances
						.next();
				_getAssertParamValuesFromGroupInstance(assertParamGroupInstance
						.getName(), assertValues, assertParamGroupInstance);
			}
		} catch (TypeConversionException tce) {
			throw new JTestCaseException("Error converting assert to Java type");
		} catch (DigesterException e) {
			throw new JTestCaseException("Error retrieving asserts.");
		} catch (FileNotFoundException de) {
			throw new JTestCaseException("Cannot read xml file : \n"
					+ de.getMessage());
		}
		return assertValues;
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
	protected void _getAssertParamValuesFromGroupInstance(String path,
			MultiKeyHashtable flatAsserts,
			AssertParamGroupInstance assertParamGroupInstance)
			throws JTestCaseException, InstantiationException, IllegalAccessException {
		Iterator assertIter = assertParamGroupInstance
				.getAssertParamInstances().iterator();
		try {
			while (assertIter.hasNext()) {
				AssertParamInstance asert = (AssertParamInstance) assertIter
						.next();
				Object value = typeConverter._convertType(asert);
				String[] key = new String[2];
				key[0] = path + "/" + asert.getName();
				key[1] = asert.getAction();
				flatAsserts.put(key, value);
			}
			Iterator assertGroupIter = assertParamGroupInstance
					.getAssertParamGroupInstances().iterator();
			while (assertGroupIter.hasNext()) {
				AssertParamGroupInstance assertGroup = (AssertParamGroupInstance) assertGroupIter
						.next();
				_getAssertParamValuesFromGroupInstance(path + "/"
						+ assertGroup.getName(), flatAsserts, assertGroup);
			}
		} catch (TypeConversionException tce) {

			throw new JTestCaseException("Error converting param to Java type");

		}
	}

}
