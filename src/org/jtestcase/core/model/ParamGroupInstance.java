/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Represents an param group from the JTestCaseWizard XML data.
 * 
 * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli</a>
 * 
 * $Id: ParamGroupInstance.java,v 1.1 2005/10/12 20:20:08 faustothegrey Exp $
 */
public class ParamGroupInstance 
{

    /**
     * Name of the param group
     */
	private String name = "";

    /**
     * List of nested param group instances
     */
	private List paramGroupNestedInstances;

    /**
     * List of nested param instances
     */
	private List paramNestedInstances;

    /**
     * Standard constructor
     * @param name name of the param group
     */
	public ParamGroupInstance(String name) {
		this.name = name;
		paramNestedInstances = new ArrayList();
		paramGroupNestedInstances = new ArrayList();
	}

    /**
     * Gets the name of the param group
     * @return the name of the param group
     */
	public String getName() {
		return this.name;
	}

    /**
     * Sets the name of the param group
     * @param name the name of the param group
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Adds a param instance to the list of nested param instances
     * @param paramInstance an param instance
     */
	public void addParamInstance(ParamInstance paramInstance) {
		paramNestedInstances.add(paramInstance);
	}

    /**
     * Gets the list of nested param instanes
     * @return the list of nested param instances
     */
	public List getParamInstances() {
		return paramNestedInstances;
	}

    /**
     * Adds a param group instance to the list of nested param group instances
     * @param paramGroupInstance an param group instance
     */
	public void addParamGroupNestedInstance(ParamGroupInstance paramGroupInstance) {
		paramGroupNestedInstances.add(paramGroupInstance);
	}

    /**
     * Gets the list of nested param group instances
     * @return the list of nested param group instances
     */
	public List getParamGroupInstances (){
		return paramGroupNestedInstances;
	}

    /**
     * @see java.lang.Object#toString()
     */
	public String toString() {
		String retval = new String("<paramgroup> name " + this.name  + "\n");
		for (Iterator iter = paramGroupNestedInstances.iterator(); iter.hasNext();) {
			ParamGroupInstance paramGroupInstance = (ParamGroupInstance) iter.next();
			retval+=("\t " + paramGroupInstance.toString() + "\n");
		}
		for (Iterator iter = paramNestedInstances.iterator(); iter.hasNext();) {
			ParamInstance paramInstance = (ParamInstance) iter.next();
			retval+=("\t " + paramInstance.toString() + "\n");
		}
		retval+=("</paramgroup>");
		return retval;
	}
}
