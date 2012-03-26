/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.digester;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * Implementation of the XQueryParser with the use of JDOM and Jaxen.
 * 
 * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli</a>
 * 
 * $Id: XQueryParserImpl_Jaxen.java,v 1.5 2005/10/25 14:42:23 faustothegrey Exp $
 */
public class XQueryParserImpl_Jaxen implements XQueryParser {

	/**
	 * @deprecated
	 * @see org.jtestcase.core.digester.XQueryParser#getElements(String, String)
	 */
	public List getElements(String fileName, String xpathexpr)
			throws FileNotFoundException, XQueryException {
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
			Document doc;
			doc = builder.build(is);
			return XPath.selectNodes(doc, xpathexpr);
		} catch (JDOMException e) {
			String msg = "***Error: " + e.getMessage();
			throw new XQueryException(msg);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileNotFoundException();
		}
	}

	/**
	 * @see org.jtestcase.core.digester.XQueryParser
	 */
	public List getElements(Document doc, String xpathexpr)
			throws FileNotFoundException, XQueryException, JDOMException {
		return XPath.selectNodes(doc, xpathexpr);
	}

}
