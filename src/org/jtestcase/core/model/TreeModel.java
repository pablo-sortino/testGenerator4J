/**
 * 
 */
package org.jtestcase.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jtestcase.core.digester.DigesterException;
import org.jtestcase.core.digester.XQueryException;
import org.jtestcase.core.digester.XQueryParser;
import org.jtestcase.core.digester.XQueryParserImpl_Jaxen;
import org.jtestcase.plugin.guimodel.intf.IUINode;

/**
 * @author fausto
 * 
 */
public class TreeModel {

	/**
	 * Name of the JTestCaseWizard XML file
	 */
	private String fileName = "";

	/**
	 * Name of the JTestCaseWizard XML file
	 */
	private Document jDomDocument = null;

	/**
	 * Instance of the XQueryParser used
	 */
	private XQueryParser xqueryparser = new XQueryParserImpl_Jaxen();

	/**
	 * Standard constructor.
	 * 
	 * @param fileName
	 *            name of the JTestCaseWizard XML file public
	 * @throws XQueryException 
	 * @throws FileNotFoundException 
	 */

	public TreeModel(String fileName) throws FileNotFoundException, XQueryException {
		this.fileName = fileName;
		this.jDomDocument = getDocument(fileName);
	}


	/**
	 * Standart contructor
	 * 
	 * @param document
	 */
	public TreeModel(Document document) {
		this.jDomDocument = document;
	}

	/**
	 * Returns all test cases for one method element from the XML.
	 * 
	 * @return list of all JTestCaseIdentifiers for this method element
	 * @throws DigesterException
	 *             in case of parsing errors
	 */
	public IUINode getTree() throws DigesterException {
		Tests tests = new Tests();
		try {
			String expression = "//tests/class";
			List testCasesList = xqueryparser.getElements(fileName, expression);
			for (Iterator iter = testCasesList.iterator(); iter.hasNext();) {
				Element element = (Element) iter.next();
				TestClass testClass = new TestClass(element
						.getAttributeValue("name"));
				getMethods(testClass, element);
				tests.addClass(testClass);
			}

		} catch (Exception e) {
			throw new DigesterException("Error parsing xml file : "
					+ e.getMessage() + "  (in file : " + fileName + ")", e);
		}
		return tests;
	}

	private void getMethods(TestClass parent, Element testClassElement) {
		Iterator iter = testClassElement.getChildren().iterator();
		while (iter.hasNext()) {
			Element methodElement = (Element) iter.next();
			if ("method".equals(methodElement.getName())) {
				TestMethod testMethod = new TestMethod(methodElement
						.getAttributeValue("name"));
				parent.addMethod(testMethod);
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException, XQueryException {

		File file = new File("data/test-data.xml");
		try {
			System.out.println(file.getCanonicalFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if (!file.exists()) {
			try {
				System.out.println("file " + file.getCanonicalPath()
						+ " does not exists");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			return;
		}
		// TreeModel treedigester = new TreeModel()

		TreeModel treedigester = new TreeModel("data/test-data.xml");
		try {
			treedigester.getTree();
		} catch (DigesterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Document getDocument(String fileName) throws FileNotFoundException,
			XQueryException {

		Document doc;

		InputStream is = null;
		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// fails absolute path, will try relative path based on classpath
		}

		if (is == null) {
			is = getClass().getClassLoader().getResourceAsStream(fileName);
		}

		if (is == null)
			throw new FileNotFoundException();

		try {

			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(is);

		} catch (JDOMException e) {
			String msg = "***Error: " + e.getMessage();
			throw new XQueryException(msg);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileNotFoundException();
		}

		return doc;
	}

	private void print(String toPrint) {
		System.out.println(toPrint);
	}

}
