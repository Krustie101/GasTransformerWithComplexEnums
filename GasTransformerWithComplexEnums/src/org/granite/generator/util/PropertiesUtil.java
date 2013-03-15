/*
GRANITE DATA SERVICES
  Copyright (C) 2011 GRANITE DATA SERVICES S.A.S.

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Library General Public License as published by
  the Free Software Foundation; either version 2 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General Public License
  for more details.

  You should have received a copy of the GNU Library General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.

  @author Franck WOLFF

  This code has been extracted from the JavaBean class to be used in the ComplexJavaEnum class. 	

*/  

package org.granite.generator.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.granite.generator.as3.reflect.JavaFieldProperty;
import org.granite.generator.as3.reflect.JavaMethod;
import org.granite.generator.as3.reflect.JavaMethodProperty;
import org.granite.generator.as3.reflect.JavaProperty;
import org.granite.generator.as3.reflect.JavaTypeFactory;
import org.granite.generator.as3.reflect.JavaMethod.MethodType;
import org.granite.messaging.amf.io.util.externalizer.annotation.ExternalizedProperty;
import org.granite.messaging.amf.io.util.externalizer.annotation.IgnoredProperty;
import org.granite.util.PropertyDescriptor;
import org.granite.util.ClassUtil;


public class PropertiesUtil {
	

	
	public static SortedMap<String, JavaProperty> getProperties(JavaTypeFactory provider, Class<?> type) {
     
		PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(type);
        SortedMap<String, JavaProperty> propertyMap = new TreeMap<String, JavaProperty>();

        // Standard declared fields.
        for (Field field : type.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) &&
            	!Modifier.isTransient(field.getModifiers()) &&
            	!"jdoDetachedState".equals(field.getName()) &&	// Specific for JDO statically enhanced classes
            	!field.isAnnotationPresent(IgnoredProperty.class)) {

            	String name = field.getName();
                JavaMethod readMethod = null;
                JavaMethod writeMethod = null;
                
                if (field.getType().isMemberClass() && !field.getType().isEnum())
                	throw new UnsupportedOperationException("Inner classes are not supported (except enums): " + field.getType());

                if (propertyDescriptors != null) {
                    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        if (name.equals(propertyDescriptor.getName())) {
                            if (propertyDescriptor.getReadMethod() != null)
                                readMethod = new JavaMethod(propertyDescriptor.getReadMethod(), MethodType.GETTER);
                            if (propertyDescriptor.getWriteMethod() != null)
                                writeMethod = new JavaMethod(propertyDescriptor.getWriteMethod(), MethodType.SETTER);
                            break;
                        }
                    }
                }

                JavaFieldProperty property = new JavaFieldProperty(provider, field, readMethod, writeMethod);
                propertyMap.put(name, property);
            }
        }

        // Getter annotated by @ExternalizedProperty.
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
        	if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getReadMethod().getDeclaringClass().equals(type) 
        			&& propertyDescriptor.getReadMethod().isAnnotationPresent(ExternalizedProperty.class) 
        			&& !propertyMap.containsKey(propertyDescriptor.getName())) {

        		JavaMethod readMethod = new JavaMethod(propertyDescriptor.getReadMethod(), MethodType.GETTER);
                JavaMethodProperty property = new JavaMethodProperty(provider, propertyDescriptor.getName(), readMethod, null);
                propertyMap.put(propertyDescriptor.getName(), property);
        	}
        }
       
        return propertyMap;
    }

	
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> type) {
		PropertyDescriptor[] propertyDescriptors = ClassUtil.getProperties(type);
	    return (propertyDescriptors != null ? propertyDescriptors : new PropertyDescriptor[0]);
	}

	protected List<JavaProperty> getSortedUnmodifiableList(Collection<JavaProperty> coll) {
	    List<JavaProperty> list = (coll instanceof List<?> ? (List<JavaProperty>)coll : new ArrayList<JavaProperty>(coll));
	    Collections.sort(list);
	  return Collections.unmodifiableList(list);
	}
	
}
