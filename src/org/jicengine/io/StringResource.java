package org.jicengine.io;

import java.io.*;

/**
 * <p>
 * A virtual resource whose content is a String, instead of a file on disc.
 * </p>
 * <p>
 * StringResource makes it possible to create resource-content dynamically
 * and feed it to applications like any other resource.
 * </p>
 *
 * <h4> Neighbouring resources of a StringResource </h4>
 * <p>
 * StringResource needs a 'locator resource' that specifies the 'location' of
 * this virtual resource. StringResource doesn't implement the getResource()
 * method by itself but forwards the call to its locator resource.
 * </p>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @version 1.0
 */

public class StringResource extends AbstractResource {

	private String text;
	private Resource locatorResource;

	/**
	 * @param locatorResource a resource that gives this virtual resource a location.
	 * if the StringResource is created from some source resources through some kind
	 * of a transformation, these source resources would be good candidates for
	 * a locatorResource.
	 */
	public StringResource(String identifier, String text, Resource locatorResource)
	{
		super(identifier);
		this.text = text;
		this.locatorResource = locatorResource;
	}

	public String getText()
	{
		return this.text;
	}

	public boolean isAvailable()
	{
		return true;
	}

	public InputStream getInputStream() throws java.io.IOException
	{
		return new StringBufferInputStream(getText());
	}

	public Reader getReader() throws java.io.IOException
	{
		return new StringReader(getText());
	}

	public Resource getResource(String relativePath) throws IOException
	{
		return this.locatorResource.getResource(relativePath);
	}

}
