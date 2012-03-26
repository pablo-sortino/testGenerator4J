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
public class TestClass implements IUINode{

	private IUINodeSupport uiSupport = null;
	
	private String name = "";
	
	private ArrayList testMethods = null;
	
	private boolean GUIsupported = false;
	
	public TestClass ()
	{
		instantiateUISupport();
	}
	
	public TestClass(String name){
		this();
		this.name = name;
		testMethods = new ArrayList(); 
	}
	
	public void addMethod(TestMethod testMethod)
	{
		testMethods.add(testMethod);
		if(isGUIsupported())uiSupport.add((IUINode)testMethod);
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
		addMethod((TestMethod) toAdd);
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
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see treemodel.intf.IUINode#getNodeImage()
	 */
	public String getNodeImage() {
		// TODO Auto-generated method stub
		return "movingBox.gif";
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


	public String getName() {
		return name;
	}
	


	public void setName(String name) {
		this.name = name;
	}

	public boolean isGUIsupported() {
		return GUIsupported;
	}
	

	public void setGUIsupported(boolean isupported) {
		GUIsupported = isupported;
	}
	
	protected TestClass	 createTestClassInstance(String testClassName){
		return new TestClass(testClassName);
	}
	
	

}
