package org.jtestcase.core.type;

import java.io.IOException;
import java.util.Date;

import org.jicengine.JICEngine;
import org.jicengine.JICException;
import org.jicengine.io.Resource;
import org.jicengine.io.StringResource;
import org.xml.sax.SAXException;

public class JiceDelegator {

	public JiceDelegator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object getInstance(String data) throws IOException, SAXException, JICException {
		Resource jicString = new StringResource("identifier", data, null);
		// process the file with JIC Engine
		return JICEngine.build(jicString);

	}
}
