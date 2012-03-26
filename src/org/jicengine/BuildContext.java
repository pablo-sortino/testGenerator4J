package org.jicengine;
import org.jicengine.io.Resource;
import java.util.Map;
import java.io.IOException;

/**
 * <p>
 * An interface for accessing the current build-context within
 * JIC-files. An instance of this class is available by default in
 * all JIC-files under the name 'buildContext'.
 * </p>
 * <p>
 * BuildContext makes it possible to reference the current JIC-file,
 * the build-attributes, etc. (in the future, this interface could be
 * used for letting the JIC-files also customize their environment.)
 * </p>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public interface BuildContext {

	public static final String VARIABLE_NAME = "jic::buildContext";

	public Resource getResource(String relativePath) throws IOException;

	public Resource getCurrentFile();

	/**
	 *
	 */
	public Map getParameters();

	/**
	 *
	 */
	public Object getParameter(String name) throws JICException;

}
