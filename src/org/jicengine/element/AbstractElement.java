package org.jicengine.element;

/**
 * implements the 'name' and the 'location' properties.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author    .timo
 */

public abstract class AbstractElement implements Element {

	private String name;
	private Location location;

	/**
	 * these two parameters shouldn't change during the lifetime of
	 * the Element, so given in the constructor.
	 *
	 * @param location  Element interface doesn't include the location, but
	 *                  on the implementation level it is good that every
	 *                  Element knows its location.
	 */
	public AbstractElement(String name, Location location)
	{
		this.name = name;
		this.location = location;
	}

	public String getName()
	{
		return this.name;
	}

	public Location getLocation()
	{
		return this.location;
	}
}
