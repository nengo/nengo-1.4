/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "ConstructableNode.java". Description: 
""

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

package ca.nengo.ui.models.constructors;

import java.util.Map;

import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.descriptors.PString;

public abstract class ConstructableNode extends AbstractConstructable {
	protected static final Property pName = new PString("Name", "Name of the item to create", "");

	@Override
	protected final Object configureModel(Map<Property, Object> configuredProperties) throws ConfigException {
		String name = (String) configuredProperties.get(pName);

		// if (nodeContainer.getNodeModel(name) != null) {
		// throw new ConfigException("A node with the same name already
		// exists");
		// }

		return createNode(configuredProperties, name);
	}

	protected abstract Object createNode(Map<Property, Object> configuredProperties, String name)
			throws ConfigException;

	public final Property[] getSchema() {
		Property[] nodeSchema = getNodeSchema();
		Property[] newSchema = new Property[nodeSchema.length + 1];
		newSchema[0] = pName;
		System.arraycopy(nodeSchema, 0, newSchema, 1, nodeSchema.length);
		return newSchema;
	}

	public abstract Property[] getNodeSchema();
}
