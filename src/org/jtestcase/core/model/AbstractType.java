/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.model;

import java.util.List;

import org.jdom.Element;

/**
 * Common interface for param and assert instances.
 * 
 * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli</a>
 * 
 * $Id: AbstractType.java,v 1.2 2005/11/22 09:54:09 faustothegrey Exp $
 */
public interface AbstractType {

    /**
     * Returns the name of the param or assert
     * @return the name of the param or assert
     */
	public String getName();
		
    /**
     * Returns the type of the param or assert
     * @return the type of the param or assert
     */
	public String getType();
	
    /**
     * Returns the content of the param or assert element
     * @return the content of the param or assert element
     */
	public String getContent();
	
    /**
     * Returns the type of the keys for complex types in param or assert elements
     * @return the type of the keys for complex types in param or assert elements
     */
	public String getKey_type();
	
    /**
     * Returns the type of the value for complex types in param or assert elements
     * @return the type of the value for complex types in param or assert elements
     */
	public String getValue_type();
	
    /**
     * Returns the list of nested instances in the param or assert
     * @return the list of nested instances in the param or assert
     */
	public List getNestedInstances();
	
	/**
	 * Returns the jice element held within 
	 * @return the jice element held within
	 */
	public Element getJice();
}
