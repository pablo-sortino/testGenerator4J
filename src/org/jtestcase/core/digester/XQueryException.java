/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.digester;

/**
 * Exception class for exceptions occuring during evaluation of the XPath expression..
 * 
 * @author <a href="mailto:faustothegrey@users.sourceforge.net">Fausto Lelli</a>
 * 
 * $Id: XQueryException.java,v 1.2 2005/11/05 11:20:23 faustothegrey Exp $
 */
public class XQueryException extends Exception {

  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see java.lang.Exception#Exception(java.lang.String, java.lang.Throwable) 
     */
    public XQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    /** 
     * @see java.lang.Exception#Exception(java.lang.Throwable) 
     */
    public XQueryException(Throwable cause) {
        super(cause);
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.String) 
     */
    public XQueryException(String message) {
        super(message);
    }

}
