/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.jtestcase.core.asserter.Asserter;
import org.jtestcase.core.asserter.AssertFailureReason;
import org.jtestcase.core.mapping.HashMapper;
import org.jtestcase.util.MultiKeyHashtable;

/**
 * Object that holds the unique identifier for a test case in the sense of
 * JTestCase. The test case can be described by the method name and the test
 * case name.
 * 
 * @author <a href="mailto:faustothegrey@sourceforge.net">Fausto Lelli</a>
 * @author <a href="mailto:ckoelle@sourceforge.net">Christian Koelle</a>
 * 
 */
public class TestCaseInstance {

	/**
	 * The class name which is used to find the class tag in XML
	 */
	private String mClassName = "";

	/**
	 * The name of the method
	 */
	private String method = "";

	/**
	 * The name of the testcase
	 */
	private String testcase = "";
	
	/**
	 * JTestCase main class
	 */
	private JTestCase jTestCase;

	/**
	 * Hash mapper
	 */
	private HashMapper mapper;

	/**
	 * Asserter
	 */
	private Asserter asserter;

	/**
	 * AsserterFailureReason
	 */

	public AssertFailureReason assertFailureReason ;

	public AssertFailureReason getAssertFailureReason() {
		return assertFailureReason;
	}

	public void setAssertFailureReason(AssertFailureReason assertFailureReason) {
		this.assertFailureReason = assertFailureReason;
	}


	/**
	 * <p>
	 * Constructor with initialization of the member variables.
	 * </p>
	 * <p>
	 * A test case instance <b> should not </b> be instantiated directly. Access
	 * TestCaseInstances using the getTestCasesInstancesInMethod() method of
	 * JTestCase class.
	 * </p>
	 * 
	 * @param method
	 *            the name of the method
	 * @param testcase
	 *            the name of the testcase
	 */
	public TestCaseInstance(String method, String testcase, JTestCase jtestcase) {
		this.method = method;
		this.testcase = testcase;
		this.jTestCase = jtestcase;
		this.assertFailureReason = new AssertFailureReason();
		if (jTestCase != null) {
			this.mClassName = jtestcase.getMClassName();
			this.mapper = jtestcase.getMapper();
			this.asserter = jtestcase.getAsserter();
		}
	}

	
	/**
	 * In case of failure, reports the test variables info that caused this test 
	 * to fail.
	 * 
	 * @return String
	 */
	public String getFailureReason() {
		return assertFailureReason.toString();
	}

	/**
	 * Sets the method member variable.
	 * 
	 * @param method
	 *            the name of the method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the name of the method.
	 * 
	 * @return the name of the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the testcase member variable.
	 * 
	 * @param testcase
	 *            the name of the testcase
	 */
	protected void setTestCaseName(String testcase) {
		this.testcase = testcase;
	}

	/**
	 * Gets the name of the testcase.
	 * 
	 * @return the name of the testcase
	 */
	public String getTestCaseName() {
		return testcase;
	}

	/**
	 * Test for equality
	 * 
	 * @param other
	 *            other JTestCaseIdentifier
	 * @return true if both objects are equal
	 */
	public boolean equals(TestCaseInstance other) {
		if (this.method.compareTo(other.method) != 0
				|| this.testcase.compareTo(other.testcase) != 0) {
			return false;
		}
		return true;
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
	 */

	public HashMap getTestCaseParams() throws JTestCaseException {
		try {
			return mapper.getTestCaseParams(mClassName, method, testcase);
		}  catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new JTestCaseException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new JTestCaseException(e);
		}
	}

	/**
	 * Asserts a given varible's value against its expected value by using
	 * expected action.
	 * 
	 * 
	 *                 
	 * Following actions are defined. Expected action in data file should fall into one of them:
	 * ISNULL, ISNOTNULL, EQUALS, NOTEQUALS, GT (greater than), NOTGT (not greater than), 
	 * LT (less than), NOTLT (not less than), and TRUE (expression is true).
	 *                
	 * The expected asserting results are defined in data file like this:
	 * <pre>...
	 *       &lt;asserts&gt;
	 *         &lt;assert name=&quot;var1&quot; action=&quot;EQUALS&quot; type=&quot;int&quot;&gt;100&lt;/assert&gt;
	 *         &lt;assert name=&quot;var2&quot; action=&quot;NOTNULL&quot;/&gt;
	 *       &lt;/asserts&gt;
	 *      ...
	 * </pre>               
	 * Note: &quot;GT&quot; means &quot;real value&quot; greater than &quot;expected value&quot;. Simular to others.
	 *                
	 * @param varName - name of varible. Defined in data file in /test/class/method/asserts/assert@name.
	 * @param varValue - actually value of this varible.
	 * @return boolean. &quot;true&quot; is assertion is true, else &quot;false&quot;.
	 * @throws JTestCaseException if an internal error occurs
	 *              
	 *
	 */
	public boolean assertTestVariable(String varName, Object varValue)
			throws JTestCaseException {
		return asserter.assertParam(mClassName, method, testcase, varName,
				varValue,this);
	}

	/**
	 * Get all assert params for a given method and its test case value into
	 * Hashtable. Hashed key is assert param's name, value is String value of
	 * assert param. This method is normally used with getNameOfTestCases().
	 * 
	 * @return Hashtable. Key is assert param's name defined in
	 *         /tests/class/method/asserts/assertparm@name, value is assert
	 *         param's Object value with type as indecated in
	 *         /test/class/method/asserts/assert@type. If not "type" specified
	 *         in data file, then "String" is default type.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @throws FileNotFoundException
	 */

	public MultiKeyHashtable getTestCaseAssertParams()
			throws JTestCaseException {
		try {
			return mapper.getTestCaseAssertParams(mClassName, method, testcase);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new JTestCaseException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new JTestCaseException(e);
		}
	}

	/**
	 * Get all assert values for a given method and its test case value into
	 * Hashtable. Hashed key is assert param's name, value is String value of
	 * assert param. This method is normally used with getNameOfTestCases().
	 * 
	 * @return Hashtable. Key is assert param's name defined in
	 *         /tests/class/method/asserts/assert@name, value is assert
	 *         param's Object value with type as indecated in
	 *         /test/class/method/asserts/assert@type. If not "type" specified
	 *         in data file, then "String" is default type.
	 * @throws JTestCaseException
	 *             if an internal error occurs
	 * @throws FileNotFoundException
	 */

	public MultiKeyHashtable getTestCaseAssertValues()
			throws JTestCaseException {
		try {
			return asserter.getTestCaseAssertValues(mClassName, method,
					testcase);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new JTestCaseException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new JTestCaseException(e);
		}
	}
}
