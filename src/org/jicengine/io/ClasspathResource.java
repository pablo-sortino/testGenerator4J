package org.jicengine.io;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;

/**
 * A resource found in the classpath. The classloader of this
 * ClassPathResource is used for reading the resource.
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

public class ClasspathResource extends AbstractResource implements UrlReadable {

	private ClassLoader classLoader;
	private String resourceName;

	/**
	 * The name of the resource, for method ClassLoader.getResourceAsStream().
	 * @see java.lang.Class#getResourceAsStream
	 * @see java.lang.Class#getResource
	 */
	public ClasspathResource(String resourceName)
	{
		super("classpath://" + resourceName);
		this.classLoader = getClass().getClassLoader();
		this.resourceName = resourceName;
	}

	public ClasspathResource(ClassLoader classLoader, String resourceName)
	{
		super("classpath://" + resourceName);
		this.classLoader = classLoader;
		this.resourceName = resourceName;
	}

	public ClassLoader getClassLoader()
	{
		return this.classLoader;
	}

	public String getResourceName()
	{
		return this.resourceName;
	}

	public java.net.URL getUrl() throws IOException
	{
		java.net.URL url = getClassLoader().getResource(getResourceName());
		if( url != null ){
			return url;
		}
		else {
			throw new FileNotFoundException("Resource '" + getResourceName() + "' not available in classpath.");
		}
	}

	public boolean isAvailable()
	{
		if( getClassLoader().getResource(getResourceName()) != null ){
			return true;
		}
		else {
			return false;
		}
	}

	public InputStream getInputStream() throws java.io.IOException
		{
		InputStream stream = getClassLoader().getResourceAsStream(getResourceName());

		if( stream != null ){
			return stream;
		}
		else {
			throw new java.io.IOException("Resource '" + getResourceName() + "' not available in classpath.");
		}
	}

	public Resource getResource(String relativePath) throws java.io.IOException
	{
		return new ClasspathResource(PathResolver.getRealPath(getResourceName(), relativePath));
	}
}
