package org.granite.generator.as3.reflect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;

import org.granite.generator.as3.As3Type;
import org.granite.generator.as3.ClientType;
import org.granite.generator.util.PropertiesUtil;

public class ComplexJavaEnum extends JavaEnum {

	
	 ///////////////////////////////////////////////////////////////////////////
    // Fields.

    protected final SortedMap<String, JavaProperty> properties;
    
    ///////////////////////////////////////////////////////////////////////////
    // Constructor.
    
	public ComplexJavaEnum(JavaTypeFactory provider, Class<?> type, URL url) {
		super(provider, type, url);
	    // Collect bean properties.
        SortedMap<String,JavaProperty> sortedMap  = PropertiesUtil.getProperties(provider, type);
        sortedMap.remove("name");
        filterCalculatableProperties(sortedMap);
        this.properties = Collections.unmodifiableSortedMap(sortedMap);
	} 
	
	private void filterCalculatableProperties(SortedMap<String,JavaProperty> properties) {
		for (java.util.Iterator<String> keyIter = properties.keySet().iterator(); keyIter.hasNext();) {
			String key = keyIter.next();
			JavaProperty property = properties.get(key);
			boolean keepProperty = false;
			if (property.getType() != null) {
				if (property.getType().isPrimitive() 
						|| BigDecimal.class.equals(property.getType())
						|| BigInteger.class.equals(property.getType())
						|| String.class.equals(property.getType())) {
					keepProperty = true;
				} else if (property.getType().isEnum()) {
					keepProperty = provider.getJavaType(property.getType()) != null;
				} 
			}
			if (!keepProperty) keyIter.remove();
		}
	}
	
	public Collection<JavaProperty> getProperties() {
        return properties.values();
    }
   
    private String converNullValueToAs3String(ClientType as3Type) {
    	if (As3Type.NUMBER.equals(as3Type)) {
			return "NaN";
		} else { 
			Object nullValue = as3Type.getNullValue();
			if (nullValue != null) return nullValue.toString();
			else return "null";
		}
    }
    
    public String convertPropertyValueToAs3String(String propertyName, Object propertyValue) {
    	JavaProperty javaProperty = properties.get(propertyName);
    	String as3String = null;
    	ClientType as3Type = this.provider.getAs3Type(javaProperty.getType());
    	if (propertyValue == null) {
    		return converNullValueToAs3String(as3Type);
    	}
    	String prefix = "";
    	String suffix = "";
    	
    	if (As3Type.STRING.equals(as3Type)) {
    		prefix = suffix = "\"";
    	} else if (As3Type.BIG_DECIMAL.equals(as3Type)) {
    		prefix = "new BigDecimal(";
    		suffix = ")";
    	} else if (As3Type.BIG_INTEGER.equals(as3Type)) {
    		prefix = "new BigInteger(";
    		suffix = ")";
    	} else if (As3Type.LONG.equals(as3Type)) {	
    		prefix = "new Long(";
    		suffix = ")";
    	} else if (javaProperty.getType().isEnum()) {
    		prefix = javaProperty.getType().getSimpleName()+".";
    	}
    	as3String  = prefix + propertyValue.toString() + suffix;
    	return as3String;
    }
  
}
