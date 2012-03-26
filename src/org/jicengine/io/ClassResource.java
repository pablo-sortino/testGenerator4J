package org.jicengine.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;

/**
 * Resource of some Class. The resource is read by calling
 * [resource-owner-class].getResourceAsStream([resource-name])
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @version 1.0
 * @see java.lang.Class
 * @see java.lang.ClassLoader
 * @see java.lang.Class#getResourceAsStream
 * @see java.lang.Class#getResource
 */

public class ClassResource extends AbstractResource implements UrlReadable {

	private Class resourceOwnerClass;
	private String resourceName;

	/**
	 * @param resourceOwnerClass  the Class that own the resource. specifies the
	 * location of the actual file.
	 * @param resourceName        The name of the resource, given for method
	 *                            <code>Class.getResourceAsStream()</code>.
	 *
	 * @see java.lang.Class#getResourceAsStream
	 * @see java.lang.Class#getResource
	 */
	public ClassResource(Class resourceOwnerClass, String resourceName)
	{
		super("class://" + resourceOwnerClass.getName() + "/" + resourceName);
		this.resourceName = resourceName;
		this.resourceOwnerClass = resourceOwnerClass;
	}

	public Class getResourceOwnerClass()
	{
		return this.resourceOwnerClass;
	}

	/**
	 * not same as the getIdentifier(), which is prefixed with the
	 * name of the owner class..
	 */
	public String getResourceName()
	{
		return this.resourceName;
	}

	public java.net.URL getUrl() throws IOException
	{
		java.net.URL url = getResourceOwnerClass().getResource(getResourceName());
		if( url != null ){
			return url;
		}
		else {
			throw new java.io.FileNotFoundException("Resource '" + getResourceName() + "' of class '" + getResourceOwnerClass().getName() + "' not available.");
		}
	}

	public boolean isAvailable()
	{
		if( getResourceOwnerClass().getResource(getResourceName()) != null ){
			return true;
		}
		else {
			return false;
		}
	}

	public InputStream getInputStream() throws java.io.IOException
	{
		InputStream stream = this.resourceOwnerClass.getResourceAsStream(getResourceName());
		if( stream != null ){
			return stream;
		}
		else {
			throw new java.io.FileNotFoundException("Resource '" + getResourceName() + "' of class '" + getResourceOwnerClass().getName() + "' not available.");
		}
	}

	public Resource getResource(String relativePath) throws java.io.IOException
	{
		return new ClassResource(getResourceOwnerClass(), PathResolver.getRealPath(getResourceName(), relativePath));
	}

}
