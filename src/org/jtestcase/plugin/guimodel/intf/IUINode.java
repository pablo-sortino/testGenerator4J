/*
 * Created on 1-lug-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jtestcase.plugin.guimodel.intf;

import java.util.List;

/**
 * @author Fausto
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IUINode {
	
	public List getNodes() ;

	public void remove(IUINode toRemove) ;

	public void add(IUINode toAdd) ;
	
	public IUINode getNode();

	public String getNodeLabel();
	
	public String getNodeImage();

	public int size();
	
	public Object getUISupport();
	
	public boolean isGUIsupported();
	/**
	 * @see XMLModel#accept(ModelVisitorI, Object)
	 */
	//public void accept(IXMLModelVisitor visitor, Object passAlongArgument);

}
