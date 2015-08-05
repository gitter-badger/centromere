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

package org.oncoblocks.centromere.core.test.web.controller.criteria;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.test.config.TestJdbcDataSourceConfig;
import org.oncoblocks.centromere.core.test.config.TestWebConfig;
import org.oncoblocks.centromere.core.test.models.Subject;
import org.oncoblocks.centromere.core.test.repository.jdbc.JdbcRepositoryConfig;
import org.oncoblocks.centromere.core.test.repository.jdbc.SubjectRepository;
import org.oncoblocks.centromere.core.test.web.service.remapping.RemappingServiceConfig;
import org.oncoblocks.centromere.core.web.controller.HalMediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author woemler
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJdbcDataSourceConfig.class, JdbcRepositoryConfig.class,
		RemappingServiceConfig.class, CriteriaControllerConfig.class, TestWebConfig.class})
@WebAppConfiguration
@FixMethodOrder
public class QueryCriteriaControllerTests {

	@Autowired private SubjectRepository subjectRepository;
	private MockMvc mockMvc;
	@Autowired private WebApplicationContext webApplicationContext;
	private static boolean isConfigured = false;

	@Before
	public void setup() {

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		if (isConfigured) return;

		subjectRepository.deleteAll();

		Subject subject = new Subject(1L, "PersonA", "Homo sapiens", "M", null, null, null);
		subject.setAlias("clinic:patient01");
		subject.setAttribute("cancerType:colon");
		subjectRepository.insert(subject);

		subject = new Subject(2L, "PersonB", "Homo sapiens", "F", null, null, null);
		subject.setAlias("clinic:patient02");
		subject.setAttribute("cancerType:breast");
		subjectRepository.insert(subject);

		subject = new Subject(3L, "PersonC", "Homo sapiens", "M", null, null, null);
		subject.setAlias("clinic:patient03");
		subject.setAttribute("cancerType:lung");
		subjectRepository.insert(subject);

		subject = new Subject(4L, "MCF7", "Homo sapiens", "F", null, null, null);
		subject.setAlias("CCLE:MCF7_BREAST");
		subject.setAttribute("cancerType:breast");
		subject.setAttribute("isCellLine:Y");
		subjectRepository.insert(subject);

		subject = new Subject(5L, "A375", "Homo sapiens", "U", null, null, null);
		subject.setAlias("CCLE:A375_SKIN");
		subject.setAttribute("cancerType:skin");
		subject.setAttribute("isCellLine:Y");
		subjectRepository.insert(subject);

		isConfigured = true;

	}

	@Test
	public void findAll() throws Exception {
		mockMvc.perform(get("/subjects").accept(HalMediaType.APPLICATION_JSON_HAL_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$.content[0].subjectId", is(1)))
				.andExpect(jsonPath("$", hasKey("links")))
				.andExpect(jsonPath("$.links", hasSize(1)))
				.andExpect(jsonPath("$.links[0].rel", is("self")))
				.andExpect(jsonPath("$.links[0].href", endsWith("/subjects")))
				.andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
	}

	@Test
	public void findAllWithoutLinks() throws Exception {
		mockMvc.perform(get("/subjects").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$[0].subjectId", is(1)))
				.andExpect(jsonPath("$[0]", not(hasKey("links"))))
				.andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
	}

	@Test
	public void findFiltered() throws Exception {
		mockMvc.perform(get("/subjects?exclude=links,name")
				.accept(HalMediaType.APPLICATION_JSON_HAL_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$.content[0].subjectId", is(1)))
				.andExpect(jsonPath("$.content[0]", not(hasKey("name"))))
				.andExpect(jsonPath("$.content[0]", not(hasKey("links"))));
	}

	@Test
	public void findFieldFiltered() throws Exception {
		mockMvc.perform(get("/subjects?fields=links,name")
				.accept(HalMediaType.APPLICATION_JSON_HAL_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", not(hasKey("subjectId"))))
				.andExpect(jsonPath("$.content[0]", hasKey("name")))
				.andExpect(jsonPath("$.content[0]", hasKey("links")));
	}

	@Test
	public void findMultipleBySingleParam() throws Exception {
		mockMvc.perform(get("/subjects?name=MCF7").accept(HalMediaType.APPLICATION_JSON_HAL_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$.content[0].subjectId", is(4)))
				.andExpect(jsonPath("$", hasKey("links")))
				.andExpect(jsonPath("$.links", hasSize(1)))
				.andExpect(jsonPath("$.links[0].rel", is("self")))
				.andExpect(jsonPath("$.links[0].href", endsWith("/subjects?name=MCF7")))
				.andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
	}
	
	@Test
	public void findPaged() throws Exception {
		mockMvc.perform(get("/subjects?page=1&size=3").accept(HalMediaType.APPLICATION_JSON_HAL_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$.content[0].subjectId", is(4)))
				.andExpect(jsonPath("$", hasKey("links")))
				.andExpect(jsonPath("$.links", hasSize(2)))
				.andExpect(jsonPath("$.links[0].rel", is("self")))
				.andExpect(jsonPath("$", hasKey("page")))
				.andExpect(jsonPath("$.page.totalElements", is(5)))
				.andExpect(jsonPath("$.page.number", is(1)))
				.andExpect(jsonPath("$.page.size", is(3)))
				.andExpect(jsonPath("$.page.totalPages", is(2)));
	}

	@Test
	public void findPagedWithoutLinks() throws Exception {
		mockMvc.perform(get("/subjects?page=1&size=3").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$.content[0].subjectId", is(4)))
				.andExpect(jsonPath("$", not(hasKey("links"))));
	}

	@Test
	public void findSorted() throws Exception {
		mockMvc.perform(get("/subjects?sort=subjectId,desc").accept(HalMediaType.APPLICATION_JSON_HAL_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", hasKey("subjectId")))
				.andExpect(jsonPath("$.content[0].subjectId", is(5)));
	}
	
}