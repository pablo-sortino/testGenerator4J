package org.jicengine.element;

/**
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class Location {

	int lineNumber;
	String document;
	int depth;

	/**
	 * @param depth  the depth of the element in the element-tree.
	 */
	public Location(int lineNumber, String document, int depth)
	{
		this.lineNumber = lineNumber;
		this.document = document;
		this.depth = depth;
	}

	public int getDepth()
	{
		return this.depth;
	}
	public String toString()
	{
		return "line " + this.lineNumber + " in " + this.document;
	}

}
