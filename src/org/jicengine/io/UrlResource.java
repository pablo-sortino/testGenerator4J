package org.jicengine.io;

import java.io.*;
import java.net.URL;

/**
 * A resource that is read through an Url.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @version 1.0
 */

public class UrlResource extends AbstractResource implements UrlReadable {

	private URL url;

	public UrlResource(URL url)
	{
		super(url.toString());
		this.url = url;
	}

	public UrlResource(String url) throws java.net.MalformedURLException
	{
		this(new URL(url));
	}

	public URL getUrl()
	{
		return this.url;
	}

	public InputStream getInputStream() throws java.io.IOException
	{
		return getUrl().openStream();
	}

	public Resource getResource(String relativePath) throws IOException
	{
		return new UrlResource(new URL(getUrl(), relativePath));
	}
}
