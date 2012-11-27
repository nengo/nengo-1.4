/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "RangedConfigParam.java". Description:
"A Config Descriptor which can have a confined integer range

  @author Shu Wu"

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

package ca.nengo.ui.configurable.descriptors;

import java.math.BigDecimal;

import ca.nengo.ui.configurable.Property;

/**
 * A Config Descriptor which can have a confined integer range
 * 
 * @author Shu Wu
 */
public abstract class PNumber extends Property {

    private static final long serialVersionUID = 1L;

    /**
     * Whether to check the range of the value
     */
    private boolean checkRange = false;
    private BigDecimal min;
    private BigDecimal max;
    
    public PNumber(String name, String description, Object defaultValue) {
        super(name, description, defaultValue);
    }

    protected void setRange(Object min, Object max) {
    	if (min == null && max == null) {
    		this.checkRange = false;
    	} else {
    		String min_str = (min != null) ? min.toString() : "-Infinity";
    		String max_str = (max != null) ? max.toString() : "Infinity";
    		this.min = new BigDecimal(min_str);
    		this.max = new BigDecimal(max_str);
    		this.checkRange = true;
    	}
    }
    
    public String getRange() {
    	return "[" + min.toPlainString() + " to " + max.toPlainString() + "]";
    }
    
    public boolean isInRange(Object value) {
    	BigDecimal val = new BigDecimal(value.toString());
    	return val.compareTo(min) >= 0 && val.compareTo(max) <= 0;
    }
    
    /**
     * @return Whether range checking is enabled
     */
    public boolean isCheckingRange() {
        return checkRange;
    }
}
