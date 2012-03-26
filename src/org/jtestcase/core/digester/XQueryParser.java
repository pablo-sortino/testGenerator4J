/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.digester;

import java.io.FileNotFoundException;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Interface of the XQueryParser used to retrieve the XML data for JTestCaseWizard.
 * There are several possible implementations for this interface.
 * 
 * @author <a href="mailto:faustothegrey@users.sourceforge.net">Fausto Lelli</a>
 * 
 * $Id: XQueryParser.java,v 1.4 2005/10/25 14:42:23 faustothegrey Exp $
 */
public interface XQueryParser {

    /**
     * @deprecated
     * Gets a list of elements from the XML file with the use of a XPath expression
     * @param fileName name of the XML file
     * @param xpathexpr the XPath expression
     * @return list of elements
     * @throws XQueryException in case of any errors during the processing
     */
	public List getElements(String fileName, String xpathexpr) throws FileNotFoundException, XQueryException ;

    /**
     * Gets a list of elements from the XML file with the use of a XPath expression
     * @param fileName name of the XML file
     * @param xpathexpr the XPath expression
     * @return list of elements
     * @throws XQueryException in case of any errors during the processing
     * @throws JDOMException 
     */
	public List getElements(Document doc, String xpathexpr) throws FileNotFoundException, XQueryException, JDOMException ;

}
