package org.granite.generator.as3;

import java.net.URL;

import org.granite.generator.as3.reflect.ComplexJavaEnum;
import org.granite.generator.as3.reflect.JavaType;
import org.granite.generator.as3.reflect.JavaType.Kind;
import org.granite.util.ClassUtil;

public class JavaAs3GroovyTransformerWithComplexEnums extends JavaAs3GroovyTransformer {

	public JavaType getJavaType(Class<?> clazz) {
		JavaType javaType = javaTypes.get(clazz);
		if (javaType == null && getConfig().isGenerated(clazz)) {
			URL url = ClassUtil.findResource(clazz);
			Kind kind = getKind(clazz);
			if (kind == Kind.ENUM){
				javaType = new ComplexJavaEnum(this, clazz, url);
				javaTypes.put(clazz,javaType);
			} else {
				javaType = super.getJavaType(clazz);
			}
		}
		return javaType;
	}
	
	
}
