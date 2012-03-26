/*  * This program is licensed under Common Public License Version 0.5. * * For License Information and conditions of use, see "LICENSE" in packaged *  */package org.jtestcase.core.digester;import java.io.FileInputStream;import java.io.FileNotFoundException;import java.io.IOException;import java.io.InputStream;import java.util.ArrayList;import java.util.Iterator;import java.util.List;import java.util.Vector;import org.jdom.Document;import org.jdom.Element;import org.jdom.JDOMException;import org.jdom.input.SAXBuilder;import org.jtestcase.JTestCase;import org.jtestcase.TestCaseInstance;import org.jtestcase.core.model.AssertGroupInstance;import org.jtestcase.core.model.AssertInstance;import org.jtestcase.core.model.AssertParamGroupInstance;import org.jtestcase.core.model.AssertParamInstance;import org.jtestcase.core.model.ParamGroupInstance;import org.jtestcase.core.model.ParamInstance;/** * Helper for handling the JTestCaseWizard XML. Reads data from the * JTestCaseWizard XML into internal data structures. *  * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli </a> *  * $Id: JTestCaseDigester.java,v 1.7 2005/11/22 09:53:39 faustothegrey Exp $ */public class JTestCaseDigester {	/**	 * Name of the JTestCaseWizard XML file	 */	private String fileName = "";	/**	 * Instance of the XQueryParser used	 */	private XQueryParser xqueryparser = new XQueryParserImpl_Jaxen();	/**	 * JTestCase to which this digester belongs	 */	private JTestCase jtestcase;	/**	 * JDom representation of the data xml file	 */	private Document jDomDocument;	/**	 * Standard constructor.	 * 	 * @param fileName	 *            name of the JTestCaseWizard XML file	 * @throws XQueryException	 * @throws FileNotFoundException	 */	public JTestCaseDigester(String fileName, JTestCase jtestcase)			throws FileNotFoundException, XQueryException {		this.fileName = fileName;		this.jtestcase = jtestcase;		this.jDomDocument = getDocument(fileName);	}	/**	 * Returns all test cases for one method element from the XML.	 * 	 * @param className	 *            the name of the class element	 * @param methodName	 *            the name of the method element the test cases haven to be read	 * @return list of all JTestCaseIdentifiers for this method element	 * @throws DigesterException	 *             in case of parsing errors	 */	public Vector getTestCasesPerMethod(String methodName)			throws DigesterException {		Vector vector = new Vector();		try {			String expression = "//tests/class[@name='"					+ jtestcase.getMClassName() + "']/method[@name='"					+ methodName + "']";			List testCasesList = xqueryparser.getElements(jDomDocument,					expression);			for (Iterator iter = testCasesList.iterator(); iter.hasNext();) {				Element element = (Element) iter.next();				if (element.getAttribute("test-case") != null) {					// get test case from attribute					vector.add(new TestCaseInstance(element							.getAttribute("name").getValue(), element							.getAttribute("test-case").getValue(), jtestcase));				}				else {					// get test case from test-case tag					List children = element.getChildren("test-case");					for (Iterator childrenIter = children.iterator(); childrenIter							.hasNext();) {						Element testcaseChild = (Element) childrenIter.next();						vector.add(new TestCaseInstance(element.getAttribute(								"name").getValue(), testcaseChild								.getAttributeValue("name"), jtestcase));					}				}			}			return vector;		} catch (Exception e) {			throw new DigesterException("Error parsing xml file : "					+ e.getMessage() + "  (in file : " + fileName + ")", e);		}	}	/**	 * Returns all test cases in one XML file. A test case is identifies by the	 * name of the test method and the name of the test case.	 * 	 * @param className	 *            the name of the class element	 * @return list of all JTestCaseWizard identifiers in this file	 * @throws DigesterException	 *             in case of parsing errors	 */	public Vector getTestCases(String className, JTestCase jtestcase)			throws DigesterException {		Vector vector = new Vector();		try {			String expression = "//tests/class[@name='" + className					+ "']/method";			List testCasesList = xqueryparser.getElements(jDomDocument,					expression);			for (Iterator iter = testCasesList.iterator(); iter.hasNext();) {				Element element = (Element) iter.next();				if (element.getAttribute("test-case") != null) {					// get test case from attribute					vector.add(new TestCaseInstance(element							.getAttribute("name").getValue(), element							.getAttribute("test-case").getValue(), jtestcase));				}				else {					// get test case from test-case tag					vector.add(new TestCaseInstance(element							.getAttribute("name").getValue(), element.getChild(							"test-case").getAttributeValue("name"), jtestcase));				}			}			return vector;		} catch (Exception e) {			throw new DigesterException("Error parsing xml file : "					+ e.getMessage() + "  (in file : " + fileName + ")", e);		}	}	/**	 * Returns a list of all assert instances for the given method and test case	 * 	 * @param className	 *            the name of the class element	 * @param methodName	 *            name of the method element	 * @param testCase	 *            name of the test case	 * @return a list of assert instances	 * @throws DigesterException	 *             in case of arsing errors	 */	public List getAssertsInstances(String className, String methodName,			String testCase) throws DigesterException {		List list = new ArrayList();		try {			List expectedElList = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "' and ./@test-case='" + testCase							+ "']/asserts/assert");			for (Iterator iter = expectedElList.iterator(); iter.hasNext();) {				Element expectedEl = (Element) iter.next();				AssertInstance assertion = getAssertInstanceFromElement(expectedEl);				list.add(assertion);			}			List expectedElList2 = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "']" + "/test-case[./@name='"							+ testCase + "']/asserts/assert");			for (Iterator iter = expectedElList2.iterator(); iter.hasNext();) {				Element expectedEl2 = (Element) iter.next();				AssertInstance assertion = getAssertInstanceFromElement(expectedEl2);				list.add(assertion);			}			return list;		} catch (Exception e) {			throw new DigesterException(e);		}	}	/**	 * Returns a list of all assertparam instances for the given method and test	 * case	 * 	 * @param className	 *            the name of the class element	 * @param methodName	 *            name of the method element	 * @param testCase	 *            name of the test case	 * @return a list of assert instances	 * @throws DigesterException	 *             in case of arsing errors	 */	public List getAssertsParamInstances(String className, String methodName,			String testCase) throws DigesterException {		List list = new ArrayList();		try {			List expectedElList = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "' and ./@test-case='" + testCase							+ "']/asserts/assertparam");			for (Iterator iter = expectedElList.iterator(); iter.hasNext();) {				Element expectedEl = (Element) iter.next();				AssertParamInstance assertion = getAssertParamInstanceFromElement(expectedEl);				list.add(assertion);			}			List expectedElList2 = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "']" + "/test-case[./@name='"							+ testCase + "']/asserts/assertparam");			for (Iterator iter = expectedElList2.iterator(); iter.hasNext();) {				Element expectedEl2 = (Element) iter.next();				AssertParamInstance assertion = getAssertParamInstanceFromElement(expectedEl2);				list.add(assertion);			}			return list;		} catch (Exception e) {			throw new DigesterException(e);		}	}	/**	 * Analyses a assert element from the XML and creates an assert instance.	 * 	 * @param element	 *            the XML assert element	 * @return an assert instance	 * @throws DigesterException	 *             in case of any errors	 */	private AssertParamInstance getAssertParamInstanceFromElement(			Element element) throws DigesterException {		AssertParamInstance assertion = AssertParamInstance				.createAssertParamInstance(element);		// now, if this parameter element has nested params,		// add the nested elements to this instance (recursively)		// 1) get the children of this element		List nestedElementList = element.getChildren();		// 2) add them to father, and for each, do it recursively		for (Iterator iter = nestedElementList.iterator(); iter.hasNext();) {			Element nestedElement = (Element) iter.next();			if (nestedElement.getName().equals("assert")) {				// recursively call this function				AssertParamInstance nestedInstance = getAssertParamInstanceFromElement(nestedElement);				// add the istance to the father param				assertion.addAssert(nestedInstance);			}			else				throw new DigesterException(						"assertparam can contain only nested assertparams tags ",						null);		}		return assertion;	}	/**	 * Analyses a assert element from the XML and creates an assert instance.	 * 	 * @param element	 *            the XML assert element	 * @return an assert instance	 * @throws DigesterException	 *             in case of any errors	 */	private AssertInstance getAssertInstanceFromElement(Element element)			throws DigesterException {		AssertInstance assertion = AssertInstance.createAssertInstance(element);		// now, if this parameter element has nested params,		// add the nested elements to this instance (recursively)		// 1) get the children of this element		List nestedElementList = element.getChildren();		// 2) add them to father, and for each, do it recursively		for (Iterator iter = nestedElementList.iterator(); iter.hasNext();) {			Element nestedElement = (Element) iter.next();			if (nestedElement.getName().equals("assert")) {				// recursively call this function				AssertInstance nestedInstance = getAssertInstanceFromElement(nestedElement);				// add the istance to the father param				assertion.addAssert(nestedInstance);			}			else if(nestedElement.getName().equals("jice")){				// ignore it !!!!!			}			else {				throw new DigesterException(						"assert can contain only nested assert tags ", null);			}		}		return assertion;	}	/**	 * Returns a list of assert group instances for a given method name and test	 * case.	 * 	 * @param className	 *            the name of the class element	 * @param methodName	 *            a method name	 * @param testCase	 *            a test case	 * @return a list of assert group instances	 * @throws DigesterException	 *             in case of any errors	 * @throws FileNotFoundException	 */	public List getAssertGroupInstances(String className, String methodName,			String testCase) throws DigesterException, FileNotFoundException {		List instances = new ArrayList();		try {			List paramElList = new ArrayList();			paramElList = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "' and ./@test-case='" + testCase							+ "']/asserts/assertgroup");			for (Iterator elements = paramElList.iterator(); elements.hasNext();) {				// top level paramgroup instances				Element item = (Element) elements.next();				AssertGroupInstance pgInstance = parseAssertGroup(item);				instances.add(pgInstance);			}			List paramElList2 = xqueryparser					.getElements(							jDomDocument,							"/tests/class[@name='"									+ className									+ "']/method[./@name='"									+ methodName									+ "']/test-case[./@name='testCase']/asserts/assertgroup");			for (Iterator elements = paramElList2.iterator(); elements					.hasNext();) {				// top level paramgroup instances				Element item = (Element) elements.next();				AssertGroupInstance pgInstance = parseAssertGroup(item);				instances.add(pgInstance);			}		} catch (XQueryException e) {			throw new DigesterException(e);		} catch (JDOMException jde) {			throw new DigesterException(jde);		}		return instances;	}	/**	 * Returns a list of assert group instances for a given method name and test	 * case.	 * 	 * @param className	 *            the name of the class element	 * @param methodName	 *            a method name	 * @param testCase	 *            a test case	 * @return a list of assert group instances	 * @throws DigesterException	 *             in case of any errors	 * @throws FileNotFoundException	 */	public List getAssertParamGroupInstances(String className,			String methodName, String testCase) throws DigesterException,			FileNotFoundException {		List instances = new ArrayList();		try {			List paramElList = new ArrayList();			paramElList = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "' and ./@test-case='" + testCase							+ "']/asserts/assertparamgroup");			for (Iterator elements = paramElList.iterator(); elements.hasNext();) {				// top level paramgroup instances				Element item = (Element) elements.next();				AssertParamGroupInstance pgInstance = parseAssertParamGroup(item);				instances.add(pgInstance);			}			List paramElList2 = xqueryparser					.getElements(							jDomDocument,							"/tests/class[@name='"									+ className									+ "']/method[./@name='"									+ methodName									+ "']/test-case[./@name='testCase']/asserts/assertgroup");			for (Iterator elements = paramElList2.iterator(); elements					.hasNext();) {				// top level paramgroup instances				Element item = (Element) elements.next();				AssertParamGroupInstance pgInstance = parseAssertParamGroup(item);				instances.add(pgInstance);			}		} catch (XQueryException e) {			throw new DigesterException(e);		} catch (JDOMException jde) {			throw new DigesterException(jde);		}		return instances;	}	/**	 * Parses an assert group element and creates an assert group instance.	 * 	 * @param item	 *            the assert group element	 * @return an assert group instance	 * @throws DigesterException	 *             in case of any errors	 */	private AssertGroupInstance parseAssertGroup(Element item)			throws DigesterException {		AssertGroupInstance pgInstance = createAssertGroupInstance(item);		// checking for inner contents		List childrenList = item.getChildren();		for (Iterator children = childrenList.iterator(); children.hasNext();) {			Element child = (Element) children.next();			// adding parameter instance			if (child.getName().equals("assert")) {				pgInstance.addAssertInstance(AssertInstance						.createAssertInstance(child));			}			// adding paramgroup instance			else if (child.getName().equals("assertgroup")) {				AssertGroupInstance nestedPGInstance = parseAssertGroup(child);				pgInstance.addAssertGroupNestedInstance(nestedPGInstance);			}			else {				// I should never get here anyway.				// just throw an exception by now ...				throw new DigesterException(						"paramgroup can contain only param or nested param group tags ",						null);			}		}		return pgInstance;	}	/**	 * Parses an assert group element and creates an assert group instance.	 * 	 * @param item	 *            the assert group element	 * @return an assert group instance	 * @throws DigesterException	 *             in case of any errors	 */	private AssertParamGroupInstance parseAssertParamGroup(Element item)			throws DigesterException {		AssertParamGroupInstance pgInstance = createAssertParamGroupInstance(item);		// checking for inner contents		List childrenList = item.getChildren();		for (Iterator children = childrenList.iterator(); children.hasNext();) {			Element child = (Element) children.next();			// adding parameter instance			if (child.getName().equals("assertparam")) {				pgInstance.addAssertParamInstance(AssertParamInstance						.createAssertParamInstance(child));			}			// adding paramgroup instance			else if (child.getName().equals("assertparamgroup")) {				AssertParamGroupInstance nestedPGInstance = parseAssertParamGroup(child);				pgInstance.addAssertParamGroupNestedInstance(nestedPGInstance);			}			else {				// I should never get here anyway.				// just throw an exception by now ...				throw new DigesterException(						"assertparamgroup can contain only assertparam or nested assertparamgroup tags ",						null);			}		}		return pgInstance;	}	/**	 * Initialize an assertparam group instance without nested elements	 * 	 * @param item	 *            the assert group element	 * @return an assert group instance	 */	private AssertGroupInstance createAssertGroupInstance(Element item) {		AssertGroupInstance pgInstance = new AssertGroupInstance(item				.getAttributeValue("name"));		return pgInstance;	}	/**	 * Initialize an assertparam group instance without nested elements	 * 	 * @param item	 *            the assert group element	 * @return an assert group instance	 */	private AssertParamGroupInstance createAssertParamGroupInstance(Element item) {		AssertParamGroupInstance pgInstance = new AssertParamGroupInstance(item				.getAttributeValue("name"));		return pgInstance;	}	/**	 * Returns a list of all param instances for the given method and test case	 * 	 * @param className	 *            the name of the class element	 * @param methodName	 *            name of the method element	 * @param testCase	 *            name of the test case	 * @return a list of param instances	 * @throws DigesterException	 *             in case of parsing errors	 */	public List getParamsInstances(String className, String methodName,			String testCase) throws DigesterException {		List list = new ArrayList();		try {			List paramElList = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "' and ./@test-case='" + testCase							+ "']/params/param");			for (Iterator iter = paramElList.iterator(); iter.hasNext();) {				Element paramEl = (Element) iter.next();				ParamInstance parameter = getParamInstanceFromElement(paramEl);				list.add(parameter);			}			List paramElList2 = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "']" + "/test-case[./@name='"							+ testCase + "']/params/param");			for (Iterator iter = paramElList2.iterator(); iter.hasNext();) {				Element paramEl2 = (Element) iter.next();				ParamInstance assertion = getParamInstanceFromElement(paramEl2);				list.add(assertion);			}			return list;		} catch (Exception e) {			throw new DigesterException(e);		}	}	/**	 * Analyses a param element from the XML and creates an param instance.	 * 	 * @param element	 *            the XML param element	 * @return an param instance	 * @throws DigesterException	 *             in case of any errors	 */	private ParamInstance getParamInstanceFromElement(Element element)			throws DigesterException {		ParamInstance parameter = createParamInstance(element);		// now, if this parameter element has nested params,		// add the nested elements to this instance (recursively)		// 1) get the children of this element		List nestedElementList = element.getChildren();		// 2) add them to father, and for each, do it recursively		for (Iterator iter = nestedElementList.iterator(); iter.hasNext();) {			Element nestedElement = (Element) iter.next();			if (nestedElement.getName().equals("param")) {				// recursively call this function				ParamInstance nestedInstance = getParamInstanceFromElement(nestedElement);				// add the istance to the father param				parameter.addParam(nestedInstance);			}			else if(nestedElement.getName().equals("jice")){				// ignore it !!!!!			}						else					throw new DigesterException(						"param can contain only nested param tags ", null);		}		return parameter;	}	/**	 * Creates a param instance from an param element. Creates the instance	 * without the nested instances.	 * 	 * @param param	 *            the param element	 * @return an param instance	 */	private ParamInstance createParamInstance(Element param) {		String param_name = param.getAttributeValue("name");		String param_type = param.getAttributeValue("type");		String param_jice = param.getAttributeValue("use-jice");		String param_key_type = param.getAttributeValue("key-type");		String param_value_type = param.getAttributeValue("value-type");		String param_content = param.getTextTrim();		ParamInstance parameter = null;		if (!"yes".equalsIgnoreCase(param_jice)) {			parameter = new ParamInstance(param_content,					param_name, param_type, param_key_type, param_value_type);		}		else {			parameter = new ParamInstance(param_content,					param_name, param_type, param_key_type, param_value_type, param.getChild("jice"));		}		return parameter;	}	/**	 * Returns a list of param group instances for a given method name and test	 * case.	 * 	 * @param className	 *            the name of the class element	 * @param methodName	 *            a method name	 * @param testCase	 *            a test case	 * @return a list of param group instances	 * @throws DigesterException	 *             in case of any errors	 * @throws FileNotFoundException	 */	public List getParamGroupInstances(String className, String methodName,			String testCase) throws DigesterException, FileNotFoundException {		List instances = new ArrayList();		try {			List paramElList = new ArrayList();			paramElList = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "' and ./@test-case='" + testCase							+ "']/params/paramgroup");			for (Iterator elements = paramElList.iterator(); elements.hasNext();) {				// top level paramgroup instances				Element item = (Element) elements.next();				ParamGroupInstance pgInstance = parseParamGroup(item);				instances.add(pgInstance);			}			List paramElList2 = xqueryparser.getElements(jDomDocument,					"/tests/class[@name='" + className + "']/method[./@name='"							+ methodName + "']" + "/test-case[./@name='"							+ testCase + "']/params/paramgroup");			for (Iterator iter = paramElList2.iterator(); iter.hasNext();) {				Element item = (Element) iter.next();				ParamGroupInstance pgInstance = parseParamGroup(item);				instances.add(pgInstance);			}		} catch (XQueryException e) {			throw new DigesterException(e);		} catch (JDOMException jde) {			throw new DigesterException(jde);		}		return instances;	}	/**	 * Parses a param group element and creates a param group instance.	 * 	 * @param item	 *            the param group element	 * @return an param group instance	 * @throws DigesterException	 *             in case of any errors	 */	private ParamGroupInstance parseParamGroup(Element item)			throws DigesterException {		ParamGroupInstance pgInstance = createParamGroupInstance(item);		// checking for inner contents		List childrenList = item.getChildren();		for (Iterator children = childrenList.iterator(); children.hasNext();) {			Element child = (Element) children.next();			// adding parameter instance			if (child.getName().equals("param")) {				pgInstance.addParamInstance(createParamInstance(child));			}			// adding paramgroup instance			else if (child.getName().equals("paramgroup")) {				ParamGroupInstance nestedPGInstance = parseParamGroup(child);				pgInstance.addParamGroupNestedInstance(nestedPGInstance);			}			else {				// I should never get here anyway.				// just throw an exception by now ...				throw new DigesterException(						"paramgroup can contain only param or nested param group tags ",						null);			}		}		return pgInstance;	}	/**	 * Initialize a param group instance without nested elements	 * 	 * @param item	 *            the param group element	 * @return an param group instance	 */	private ParamGroupInstance createParamGroupInstance(Element item) {		ParamGroupInstance pgInstance = new ParamGroupInstance(item				.getAttributeValue("name"));		return pgInstance;	}	/**	 * Returns all control params .	 * 	 * @return list of ParamInstance	 * @throws DigesterException	 *             in case of parsing errors	 */	public List getTestCaseControlParams() throws DigesterException {		List list = new ArrayList();		try {			List expectedElList = xqueryparser.getElements(jDomDocument,					"/tests/params/param");			for (Iterator iter = expectedElList.iterator(); iter.hasNext();) {				Element expectedEl = (Element) iter.next();				list.add(createParamInstance(expectedEl));			}			return list;		} catch (Exception e) {			throw new DigesterException(e);		}	}	/**	 * Returns all control paramgroup .	 * 	 * @return list of ParamInstance	 * @throws DigesterException	 *             in case of parsing errors	 * @throws FileNotFoundException	 */	public List getTestCaseControlParamGroups() throws DigesterException,			FileNotFoundException {		List instances = new ArrayList();		try {			List paramElList = new ArrayList();			paramElList = xqueryparser.getElements(jDomDocument,					"/tests/params/paramgroup");			for (Iterator elements = paramElList.iterator(); elements.hasNext();) {				// top level paramgroup instances				Element item = (Element) elements.next();				ParamGroupInstance pgInstance = parseParamGroup(item);				instances.add(pgInstance);			}		} catch (XQueryException e) {			throw new DigesterException(e);		} catch (JDOMException jde) {			throw new DigesterException(jde);		}		return instances;	}	/**	 * Returns all global params for given class.	 * 	 * @param className	 *            the name of the enclosing tag class	 * @return list of ParamInstance	 * @throws DigesterException	 *             in case of parsing errors	 */	public List getTestCaseGlobalParams(String className)			throws DigesterException, FileNotFoundException {		List list = new ArrayList();		List expectedElList;		try {			expectedElList = xqueryparser.getElements(jDomDocument,					"//tests/class[@name='" + className + "']/params/param");			for (Iterator iter = expectedElList.iterator(); iter.hasNext();) {				Element expectedEl = (Element) iter.next();				list.add(createParamInstance(expectedEl));			}		} catch (XQueryException e) {			throw new DigesterException(e);		} catch (JDOMException jde) {			throw new DigesterException(jde);		}		return list;	}	/**	 * Returns all global paramgroup's for given class.	 * 	 * @param className	 *            the name of the enclosing tag class	 * @return list of ParamInstance	 * @throws DigesterException	 *             in case of parsing errors	 * @throws FileNotFoundException	 */	public List getTestCaseGlobalParamGroups(String className)			throws DigesterException, FileNotFoundException {		List instances = new ArrayList();		try {			List paramElList = new ArrayList();			paramElList = xqueryparser.getElements(jDomDocument,					"//tests/class[@name='" + className							+ "']/params/paramgroup");			for (Iterator elements = paramElList.iterator(); elements.hasNext();) {				// top level paramgroup instances				Element item = (Element) elements.next();				ParamGroupInstance pgInstance = parseParamGroup(item);				instances.add(pgInstance);			}		} catch (XQueryException e) {			throw new DigesterException(e);		} catch (JDOMException jde) {			throw new DigesterException(jde);		}		return instances;	}	public Document getDocument(String fileName) throws FileNotFoundException,			XQueryException {		Document doc;		InputStream is = null;		try {			is = new FileInputStream(fileName);		} catch (FileNotFoundException e) {			// fails absolute path, will try relative path based on classpath		}		if (is == null) {			is = getClass().getClassLoader().getResourceAsStream(fileName);		}		if (is == null)			throw new FileNotFoundException();		try {			SAXBuilder builder = new SAXBuilder();			doc = builder.build(is);		} catch (JDOMException e) {			String msg = "***Error: " + e.getMessage();			throw new XQueryException(msg);		} catch (IOException e) {			e.printStackTrace();			throw new FileNotFoundException();		}		return doc;	}}