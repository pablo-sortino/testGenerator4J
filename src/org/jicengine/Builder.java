package org.jicengine;
import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * <p>
 * Builder instances read JIC files and execute the construction instructions
 * in the files. Builder instances are obtained from the <code>JICEngine</code>
 * class.
 * </p>
 *
 * <p>
 * Currently, you don't have to operate with Builder instances in order to
 * process JIC files, you can use the static build-methods of the class
 * <code>org.jicengine.JICEngine</code>. This class is needed only if there would be a need
 * to configure the Builder instances somehow before processing. At the moment,
 * Builders can't be configured in any way.
 * </p>
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @see org.jicengine.JICEngine
 */

public interface Builder {

	/**
	 * <p>
	 * Processes a JIC file and returns the result.
	 * </p>
	 *
	 *
	 * @param instructions  instructions on how to build the application.
	 *                      path of the JIC file + optionally some parameters.
	 * @return the constructed object. It will be the element instance of the
	 * root JIC element. Null is returned if the root JIC element is an action
	 * element.
	 *
	 * @throws IOException  if there are problems reading the JIC file.
	 * @throws SAXException if the content in the JIC file is not well-formed XML.
	 * @throws JICException in case of any other exception. Exceptions may occur
	 * because the JIC instructions are not valid or because some method call
	 * resulted in application exception.
	 */
	public Object build(Instructions instructions) throws IOException, SAXException, JICException;

}
