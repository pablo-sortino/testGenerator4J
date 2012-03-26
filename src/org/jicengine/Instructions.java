package org.jicengine;
import java.util.Map;
import java.io.File;
import org.jicengine.io.Resource;

/**
 * <p>
 * The instructions given to a builder when it 
 * is asked to construct a graph of Java objects. 
 * </p>
 * 
 * <p>
 * The instructions consists of:
 * </p>
 * <ul>
 *   <li>The location of the <dfn>JIC file</dfn>, in form of a 
 *    <code>org.jicengine.io.Resource</code> instance.</li>
*    <li>The build-parameters, in form of a <code>java.util.Map</code>.</li>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @see org.jicengine.io.Resource
 * @see org.jicengine.Builder
 */

public class Instructions {

	private Resource jicFile;
	private Map parameters;

	/**
	 * Creates an Instructions without parameters.
	 * @throws IllegalArgumentException if jicFile is null.
	 */
	public Instructions(Resource jicFile)
	{
		this(jicFile, null);
	}

	/**
	 * @param parameters may be null, if there are no parameters.
	 * @throws IllegalArgumentException if jicFile is null.
	 */
	public Instructions(Resource jicFile, Map parameters)
	{
		this.jicFile = jicFile;

		if( jicFile == null ){
			throw new IllegalArgumentException("JIC file null, can't create Instructions.");
		}

		if( parameters != null ){
			// modifying the parameters would lead into weird errors..
			this.parameters = java.util.Collections.unmodifiableMap(parameters);
		}
		else {
			this.parameters = java.util.Collections.EMPTY_MAP;
		}
	}

	public Resource getFile()
	{
		return this.jicFile;
	}

	/**
	 * @return may be null if there are no parameters.
	 */
	public Map getParameters()
	{
		return this.parameters;
	}
}
