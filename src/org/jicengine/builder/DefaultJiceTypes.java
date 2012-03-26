package org.jicengine.builder;
import java.util.*;

import org.jicengine.element.SimpleType;
import org.jicengine.element.Type;
import org.jicengine.element.impl.*;
/**
 *
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

public class DefaultJiceTypes {

	private static Map types = new HashMap();
	static {
		types.put("array", ArrayElementCompiler.class);
		types.put("list", ListElementCompiler.class);
    types.put("collection", CollectionElementCompiler.class);    
		types.put("map", MapCompiler.class);

		types.put("bean", BeanElementCompiler.class);
		types.put("container", ContainerElementCompiler.class);
		types.put("switch", SwitchCompiler.class);
		types.put("factory", FactoryElementCompiler.class);
		types.put("constant-of", ConstantOfElementCompiler.class);
    types.put("cdata-converter", CdataConverterElementCompiler.class);
    
    // kept here for backwards compatibility (was used in JICE 2.1.1)
		types.put("cdata-conversion", CdataConverterElementCompiler.class);
	}
  
  private static Type defaultType = new SimpleType("bean",new String[0]);

  public static Type getDefaultType()
  {
    return defaultType;
  }
  
	public static TypeManager getTypeManager()
	{
		return new TypeManagerImpl(Collections.unmodifiableMap(types));
	}
}
