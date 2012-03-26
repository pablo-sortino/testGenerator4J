package org.jicengine.builder;

import org.jicengine.Instructions;
import org.jicengine.BuildContext;
import org.jicengine.JICException;
import org.jicengine.io.Resource;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

/**
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author    .timo
 */

public class BuildContextImpl implements BuildContext {

	private Instructions instructions;

	public BuildContextImpl(Instructions instructions)
	{
		this.instructions = instructions;
	}

	public Resource getResource(String relativePath) throws IOException
	{
		return getCurrentFile().getResource(relativePath);
	}

	public Resource getCurrentFile()
	{
		return this.instructions.getFile();
	}

	public Map getParameters()
	{
		return this.instructions.getParameters();
	}

	public Object getParameter(String name) throws JICException
	{
		Map parameters = getParameters();
		if( parameters != null ){
			Object parameter = parameters.get(name);
			if( parameter != null ){
				return parameter;
			}
			else {
				throw new JICException("Parameter '" + name + "' not found. available parameters: " + parameters);
			}
		}
		else {
			throw new JICException("Parameter '" + name + "' not found. (0 parameters available)");
		}
	}
}
