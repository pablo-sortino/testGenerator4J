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

import org.jtestcase.plugin.guimodel.intf.IUINode;
import org.jtestcase.plugin.guimodel.intf.IUINodeSupport;


/**
 * Represents an assert group from the JTestCaseWizard XML data.
 * 
 * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli</a>
 * 
 * $Id: AssertGroupInstance.java,v 1.1 2005/10/12 20:20:08 faustothegrey Exp $
 */

public class AssertGroupInstance 
implements IUINode
{
	
    /**
     * Name of the assert group
     */
    private String name = "";

    /**
     * List of nested assert group instances
     */
	private List assertGroupNestedInstances;

    /**
     * List of nested assert instances
     */
	private List assertNestedInstances;

    /**
     * Standard constructor
     * @param name name of the assert group
     */
	
	private IUINodeSupport uiSupport = null;

	private boolean isGUIsupported = false;
	
	public AssertGroupInstance(String name) {
		this.name = name;
		assertNestedInstances = new ArrayList();
		assertGroupNestedInstances = new ArrayList();
		try {
			uiSupport = (IUINodeSupport) Class.forName(IUINodeSupport.IMPL)
					.newInstance();
			uiSupport.setNode(this);
			setGUIsupported(true);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			//System.err.println("WARN : NOT USING UI SUPPORT");
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			//System.err.println("WARN : NOT USING UI SUPPORT");
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//System.err.println("WARN : NOT USING UI SUPPORT");
			// e.printStackTrace();
		}
		
	}

	public Object getUISupport () {
		return uiSupport;
	}
    /**
     * Gets the name of the assert group
     * @return the name of the assert group
     */
	public String getName() {
		return this.name;
	}

    /**
     * Sets the name of the assert group
     * @param name the name of the assert group
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Adds a assert instance to the list of nested assert instances
     * @param assertInstance an assert instance
     */
	public void addAssertInstance(AssertInstance assertInstance) {
		assertNestedInstances.add(assertInstance);
	}

    /**
     * Gets the list of nested assert instanes
     * @return the list of nested assert instances
     */
	public List getAssertInstances() {
		return assertNestedInstances;
	}

    /**
     * Adds a assert group instance to the list of nested assert group instances
     * @param assertGroupInstance an assert group instance
     */
	public void addAssertGroupNestedInstance(AssertGroupInstance assertGroupInstance) {
		assertGroupNestedInstances.add(assertGroupInstance);
	}
	
    /**
     * Gets the list of nestd assert group instances
     * @return the list of nested assert group instances
     */
	public List getAssertGroupInstances (){
		return assertGroupNestedInstances;
	}
	
	public String getNodeImage() {
		return "newBook.gif";
	}
	public String getNodeLabel() {
		return getName();
	}

	public void add(IUINode toAdd) {
		uiSupport.add(toAdd);
	}
	
	public void remove(IUINode toRemove) {
		uiSupport.remove(toRemove);
	}
	public int size() {
		return uiSupport.size();
	}
	
	public IUINode getNode() {
		return uiSupport.getNode();
	}
	
	public List getNodes() {
		return uiSupport.getNodes();
	}
    /**
     * @see java.lang.Object#toString()
     */
	public String toString() {

		String retval = new String("<assertgroup> name " + this.name  + "\n");
		for (Iterator iter = assertGroupNestedInstances.iterator(); iter.hasNext();) {
			AssertGroupInstance assertGroupInstance = (AssertGroupInstance) iter.next();
			retval+=("\t " + assertGroupInstance.toString() + "\n");
		}
		for (Iterator iter = assertNestedInstances.iterator(); iter.hasNext();) {
			AssertInstance assertInstance = (AssertInstance) iter.next();
			retval+=("\t " + assertInstance.toString() + "\n");
		}
		retval+=("</assertgroup>");

		return retval;
	}

	public boolean isGUIsupported() {
		return isGUIsupported;
	}
	

	public void setGUIsupported(boolean isGUIsupported) {
		this.isGUIsupported = isGUIsupported;
	}
	
}
