/*
 * Created on 8-lug-2005
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
public interface IUINodeSupport {

	public static final String IMPL = "org.jtestcase.plugin.guimodel.intf.UINodeSupport";
	
	public abstract IUINode getNode();

	public abstract List getNodes();

	public abstract void remove(IUINode toRemove);

	public abstract void add(IUINode toAdd);
	
	public abstract void setNode(IUINode inode);

	/**
	 * Answer the total number of items the receiver contains.
	 */
	public abstract int size();
}