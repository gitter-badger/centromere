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

package org.oncoblocks.centromere.model.common;

import org.oncoblocks.centromere.core.model.Filterable;
import org.oncoblocks.centromere.core.model.Model;

import java.io.Serializable;
import java.util.Set;

/**
 * @author woemler
 */

@Filterable
public class Study<ID extends Serializable> implements Model<ID> {
	
	private ID id;
	private String name;
	private String group;
	private String description;
	private Set<ID> sampleIds;

	public Study() { }

	public Study(ID id, String name, String group, String description, Set<ID> sampleIds) {
		this.id = id;
		this.name = name;
		this.group = group;
		this.description = description;
		this.sampleIds = sampleIds;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<ID> getSampleIds() {
		return sampleIds;
	}

	public void setSampleIds(Set<ID> sampleIds) {
		this.sampleIds = sampleIds;
	}
}
