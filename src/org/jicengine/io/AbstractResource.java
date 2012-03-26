package org.jicengine.io;

import java.io.*;

/**
 * implements the most general methods of the Resource-interface.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @version 1.0
 */

public abstract class AbstractResource implements Resource {

	private String identifier;
	private String mimeType;

	/**
	 *
	 */
	protected AbstractResource(String identifier)
	{
		this.identifier = identifier;
	}

	public abstract InputStream getInputStream() throws IOException;

	/**
	 * <p>
	 * Simple implementation of <code>getReader()</code> that
	 * wraps the <code>InputStream</code> obtained from <code>getInputStream()</code>
	 *  with a <code>InputStreamReader</code>.
	 * </p>
	 *
	 * <p>
	 * Override this method if a better alternative for creating the <code>Reader</code>
	 * is available.
	 * </p>
	 */
	public Reader getReader() throws IOException
	{
		return new InputStreamReader(getInputStream());
	}

	/**
	 * <p>
	 * Determines the availability of the Resource by calling
	 * <code>getInputStream()</code>: resource is available if the method doesn't
	 * throw an exception.
	 * </p>
	 *
	 * <p>
	 * Override this if a more efficient way for determining
	 * the availability is possible.
	 * </p>
	 */
	public boolean isAvailable()
	{
		try {
			getInputStream();
			return true;
		}	catch (IOException e){
			return false;
		}
	}

	public void writeTo(OutputStream out) throws IOException
	{
		InputStream in = getInputStream();
		int i;
		while ((i = in.read()) != -1) {
			out.write(i);
		}
		out.close();
		in.close();
	}

	public void writeTo(Writer writer) throws IOException
	{
		Reader reader = getReader();
		BufferedReader buffReader;
		if( reader instanceof BufferedReader ){
			buffReader = (BufferedReader) reader;
		}
		else {
			buffReader = new BufferedReader(reader);
		}

		BufferedWriter buffWriter;
		if( writer instanceof BufferedWriter ){
			buffWriter = (BufferedWriter) writer;
		}
		else {
			buffWriter = new BufferedWriter(writer);
		}

		String line;
		while ((line = buffReader.readLine()) != null) {
			buffWriter.write(line);
			buffWriter.write("\n");
		}
		buffWriter.close();
		buffReader.close();
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

	/**
	 * Returns the mime-type set with setMimeType().
	 */
	public String getMimeType()
	{
		return this.mimeType;
	}

	public abstract Resource getResource(String relativePath) throws IOException;

	/**
	 * @return  the identifier
	 */
	public String toString()
	{
		return getIdentifier();
	}
}
