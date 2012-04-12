<%--
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
  
  This is a slight modification of the template of Dmitry Ionash discussed on 
  
  http://java.dzone.com/articles/graniteds-gas3-template
  
  but for which the attachment is missing. Most of the code + suggestions have been recuperated from postings of Dmitry 
  in the GraniteDS google group forum. 
  
  This template should be combined with the accompanying custom groovy transformer + ComplexJavaEnum. 
  
  
--%>/**
 * Generated by Gas3 v${gVersion} (Granite Data Services).
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERWRITTEN EACH TIME YOU USE
 * THE GENERATOR.
 */

 
 
package ${jClass.as3Type.packageName} {

	<%
		Set as3Imports = new TreeSet();
	
		as3Imports.add("org.granite.util.Enum");
		
		for (jProperty in jClass.properties) {
			if (jProperty.as3Type.hasPackage() && jProperty.as3Type.packageName != jClass.as3Type.packageName)
				as3Imports.add(jProperty.as3Type.qualifiedName);
		}

	for (as3Import in as3Imports) {%>import ${as3Import};
	<%}%>


    [Bindable]
    [RemoteClass(alias="${jClass.qualifiedName}")]
    public class ${jClass.as3Type.name} extends Enum {
    
		private static const __name__to__constant__instance__ : Object = {};
		<%
        	for (jEnumValue in jClass.enumValues) {
			
		%>		
		public static const ${jEnumValue.name} : ${jClass.name}  = new ${jClass.name}("${jEnumValue.name}",<% for (jProperty in jClass.properties){
			 %>${jClass.convertPropertyValueToAs3String(jProperty.name,jEnumValue.constant[jProperty.name])},<%}%>_);<%}
			 
			 if (jClass.properties.size() > 0) {%>
			 
			 	
		private var __variables__calculated__ : Boolean = false;<%}%>
			 
<% for (jProperty in jClass.properties) {
			 %>		private var _${jProperty.name}:${jProperty.as3Type.name};

<%}%>		function ${jClass.as3Type.name}(value:String = null,<% for (jProperty in jClass.properties) 
		{%>${jProperty.name}:${jProperty.as3Type.name} = ${jClass.convertPropertyValueToAs3String(jProperty.name,null)},<%}%>restrictor:* = null) {
            super((value || ${jClass.firstEnumValue.name}.name), restrictor);<%for (jProperty in jClass.properties) {%>
			_${jProperty.name} = ${jProperty.name};<%} 
			
			if (jClass.properties.size() > 0) {%>
			if (restrictor) __variables__calculated__ = true;<%}%>
		}
			
<% for (jProperty in jClass.properties) {
%>		public function get ${jProperty.name}():${jProperty.as3Type.name} {
			calculateVariablesIfNecessary();
			return _${jProperty.name} 
		}

<%}%>		protected override function getConstants():Array {
            return constants;
        }

        public static function get constants():Array {
            return [<%
                for (jEnumValue in jClass.enumValues) {
                    if (jEnumValue != jClass.firstEnumValue) {
                        %>, <%
                    }
                    %>${jEnumValue.name}<%
                }
            %>];
        }

        public static function valueOf(name:String):${jClass.as3Type.name} {
			if (!__name__to__constant__instance__.hasOwnProperty(name)) {
				 __name__to__constant__instance__[name] = ${jClass.as3Type.name}(${jClass.firstEnumValue.name}.constantOf(name));
			}
			return __name__to__constant__instance__[name] as ${jClass.as3Type.name};
            
        }
        
         <%if (jClass.properties.size() > 0) {%>private function calculateVariablesIfNecessary() : void {
        	if (__variables__calculated__) return;<% 
        	for (jProperty in jClass.properties) {%>
			_${jProperty.name} = valueOf(name)._${jProperty.name};<%}%>
			__variables__calculated__ = true;				        	
        }<%}%>
    }
}