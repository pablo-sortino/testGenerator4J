/**
 * 
 */
package org.jtestcase.core.model;

import java.util.ArrayList;
import java.util.List;

import org.jtestcase.plugin.guimodel.intf.IUINode;
import org.jtestcase.plugin.guimodel.intf.IUINodeSupport;


/**
 * @author fausto
 * 
 */
public class Tests implements IUINode {

	/**
	 * The list of nested instances in the param
	 */
	private List nestedClasses;

	private IUINodeSupport uiSupport = null;

	private boolean isGUIsupported = false;
	
	public Tests() {
		nestedClasses = new ArrayList();
		instantiateUISupport();
	}
	
	public void addClass(TestClass testClass) {
		nestedClasses.add(testClass);
		if(isGUIsupported())uiSupport.add((IUINode) testClass);
	}


	private void instantiateUISupport() {
		try {
			uiSupport = (IUINodeSupport) Class.forName(IUINodeSupport.IMPL)
					.newInstance();
			uiSupport.setNode(this);
			setGUIsupported(true);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			System.err.println("WARN : NOT USING UI SUPPORT");
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.err.println("WARN : NOT USING UI SUPPORT");
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("WARN : NOT USING UI SUPPORT");
			// e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#getNodes()
	 */
	public List getNodes() {
		// TODO Auto-generated method stub
		return uiSupport.getNodes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#remove(treemodel.intf.IUINode)
	 */
	public void remove(IUINode toRemove) {
		uiSupport.remove(toRemove);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#add(treemodel.intf.IUINode)
	 */
	public void add(IUINode toAdd) {
		// TODO Auto-generated method stub
		// call business delegate method
		addClass((TestClass) toAdd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#getNode()
	 */
	public IUINode getNode() {
		// TODO Auto-generated method stub
		return uiSupport.getNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#getNodeLabel()
	 */
	public String getNodeLabel() {
		// TODO Auto-generated method stub
		return "tests";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#getNodeImage()
	 */
	public String getNodeImage() {
		// TODO Auto-generated method stub
		return "book.gif";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return uiSupport.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#getUISupport()
	 */
	public Object getUISupport() {
		// TODO Auto-generated method stub
		return uiSupport;
	}

	public boolean isGUIsupported() {
		return isGUIsupported;
	}
	

	public void setGUIsupported(boolean isGUIsupported) {
		this.isGUIsupported = isGUIsupported;
	}
	

}
