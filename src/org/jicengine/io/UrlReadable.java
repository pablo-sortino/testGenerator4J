package org.jicengine.io;

/**
 * <p>
 * Most resources, but not all, have an url. this is a interface for them.
 * Many existing Java classes use URLs, so it may be wise to support a way
 * to read Resources through Urls.
 * </p>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 * @version 1.0
 */

public interface UrlReadable {

	public java.net.URL getUrl() throws java.io.IOException;

}
