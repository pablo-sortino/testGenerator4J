package org.jicengine.io;

import java.io.*;

/**
 * <p>
 * Provides a common interface for reading resources and resolving relative
 * path-references.
 * </p>
 * <p>
 * A resource is typically a file that contains data to be used by an
 * application. Java provides various ways for reading resources: java.io.File,
 * java.net.URL, java.lang.Class.getResourceAsStream(),
 * java.lang.ClassLoader.getResourceAsStream(), etc.
 * </p>
 *
 * <p>
 * This class encapsulates all these under a common interface.
 * </p>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author    .timo
 * @version   1.0
 */

public interface Resource {

	/**
	 * A primary way reading the resource.
	 *
	 * @return              InputStream
	 * @throws IOException  if the reading fails - if the resource doesn't exist,
	 *                      for example.
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Alternative way for reading text-based resources.
	 *
	 * @return              A Reader that returns data from this Resource.
	 * @throws IOException  if the reading fails - if the resource doesn't exist,
	 *                      for example.
	 */
	public Reader getReader() throws IOException;

	/**
	 * Writes the content of this resource into an OutputStream. This is an
	 * alternative way for obtaining the data of this Resource.
	 *
	 * @throws IOException  if the content of this Resource isn't available - if
	 *                      the resource doesn't exist, for example.
	 */
	public void writeTo(OutputStream out) throws IOException;

	/**
	 * <p>
	 * Writes the content of this resource into a Writer. This is an
	 * alternative way for obtaining the data of this Resource.
	 * </p>
	 *
	 * @throws IOException  if the content of this Resource isn't available - if
	 *                      the resource doesn't exist, for example.
	 */
	public void writeTo(Writer writer) throws IOException;


	/**
	 * <p>
	 * Tests whether the resource is available i.e. can be read.
	 * </p>
	 * <p>
	 * If this method returns true, the attempt to read this Resource (with getInputStream()
	 * or other methods) is not likely to fail. Unless the resource is somehow
	 * removed between the two method calls.
	 * </p>
	 * <p>
	 * If a false is return, the resource is not available.
	 * </p>
	 * <p>
	 * NOTE: testing whether a resource is available can be as
	 * resource-intensive as actually reading the resource.
	 * </p>
	 */
	public boolean isAvailable();

	/**
   * <p>
	 * Returns the identifier of this resource.
   * </p>
	 *
   * <p>
	 * Depending on the kind of resource, the
	 * identifier could be a file-path, url, etc.
   * </p>
	 * <p>
	 * The identifier is descriptive - it will help a human to find out what kind
	 * of resource is in question. It CAN NOT be used for creating new Resources or
   * for reading resources. the format of the identifier varies and may be changed
	 * in the future.
   * </p>
	 *
	 */
	public String getIdentifier();

	/**
	 * <p>
	 * Returns the http mime-type of this Resource, if this Resource has one.
	 * </p>
	 * <p>
	 * Mime-type information is an easy way to find out something about the
	 * type of content in a Resource. it also makes it easier to write Resource
	 * data into http-responses.
	 * </p>
	 *
	 * @return   NOTE: this property is optional! null is returned if the mime-type
	 * information is not available. and most in cases it probably isn't.
	 */
	public String getMimeType();

	/**
	 * <p>
	 * Locates another Resource whose path is defined relative to this Resource.
	 * </p>
	 * <p>
	 * the path scheme used with files and urls is used for specifying relative
	 * paths.
	 * </p>
	 * <p>
	 * the method generally returns instances of the same Resource-subclass than
	 * the current instance, but this is not obligatory.
	 * </p>
	 * <p>
	 * NOTE:
	 * </p>
	 * <p>
	 * NOTE: there is no support for absolute paths. yet.
	 * </p>
	 * </p>
	 *
	 * @param relativePath  name of the neighbouring resource. only relative paths
	 *      are allowed, don't put the root mark '/' in the beginning. notations
	 *      like '../' can be used (in most of the cases, at least) Windows-like
	 *      paths '\joku\jotain.txt' won't work.
	 * @return              a new Resource. The returned Resource is most likely of
	 *      the same type as this resource (although there's no guarantee). In
	 *      other words, if this Resource is a FileResource, the returned Resource
	 *      will also be a FileResource.
	 *      NOTE: this method doesn't necessary check the availability of the relative
	 * resource, because that may be too slow. if you want to make sure that the
	 * returned resource is available, you must examine the availability of the
	 * returned resource by your self.
	 *
	 * @throws IOException  if a reference to the neighbouring resource couldn't be
	 * created.
	 *
	 */
	public Resource getResource(String relativePath) throws IOException;

}
