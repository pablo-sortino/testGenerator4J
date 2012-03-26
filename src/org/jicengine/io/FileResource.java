package org.jicengine.io;

import java.io.*;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @version 1.0
 */

public class FileResource extends AbstractResource implements UrlReadable {

	private File file;

	public FileResource(File file)
	{
		super(file.getAbsolutePath());
		this.file = file;
	}

	public FileResource(String filePath)
	{
		this(new File(filePath));
	}

	public File getFile()
	{
		return this.file;
	}

	public File toFile()
	{
		return getFile();
	}

	public java.net.URL getUrl() throws IOException
	{
		try {
			return getFile().toURL();
		} catch (java.net.MalformedURLException e){
			throw new IOException("Failed to transform FileResource to URL: " + e);
		}
	}

	public boolean isAvailable()
	{
		return getFile().canRead();
	}

	public InputStream getInputStream() throws java.io.IOException
	{
		return new FileInputStream(getFile());
	}

	public Resource getResource(String relativePath)
	{
		File baseFile = getFile();
		if( !baseFile.isDirectory() ){
			baseFile = baseFile.getParentFile();
		}
		return new FileResource(new File(baseFile, relativePath));
	}
}
