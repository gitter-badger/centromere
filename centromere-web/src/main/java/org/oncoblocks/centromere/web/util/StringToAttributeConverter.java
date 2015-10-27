/*
 * Copyright 2015 William Oemler, Blueprint Medicines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oncoblocks.centromere.web.util;

import org.oncoblocks.centromere.core.model.support.Attribute;
import org.springframework.core.convert.converter.Converter;

/**
 * Simple converter for {@link Attribute}
 * 
 * @author woemler
 */
public class StringToAttributeConverter implements Converter<String, Attribute> {
	@Override 
	public Attribute convert(String s) {
		String[] bits = s.split(":");
		Attribute attribute = new Attribute();
		attribute.setName(bits[0]);
		if (bits.length > 1){
			attribute.setValue(bits[1]);
		}
		return attribute;
	}
}
