/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.util.JsonModelConverter;
import org.oncoblocks.centromere.core.util.KeyValueMapModelConverter;
import org.oncoblocks.centromere.core.util.ModelScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ModelScan(basePackages = { "org.oncoblocks.centromere.core.test" })
@ContextConfiguration(classes = { TestConfig.class })
public class ModelConverterTests {
	
	@Test
	public void jsonConverterTest() throws Exception {
		String json = "{\"entrezGeneId\": 1, \"primaryGeneSymbol\": \"ABC\", \"taxId\": 9606}";
		JsonModelConverter converter = new JsonModelConverter(EntrezGene.class);
		EntrezGene gene = (EntrezGene) converter.convert(json);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId() == 1L);
		Assert.isTrue("ABC".equals(gene.getPrimaryGeneSymbol()));
		Assert.isNull(gene.getChromosome());
	}
	
	@Test
	public void badJsonConversiontest() throws Exception {
		String json = "{\"entrezGeneId\": 1, \"primaryGeneSymbol\": \"ABC\", \"badField\": 0}";
		JsonModelConverter converter = new JsonModelConverter(new ObjectMapper(), EntrezGene.class);
		EntrezGene gene = (EntrezGene) converter.convert(json);
		Assert.isNull(gene);
	}
	
	@Test
	public void mapConversionTest() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("entrezGeneId", "1");
		map.put("primaryGeneSymbol", "ABC");
		map.put("taxId", "9606");
		KeyValueMapModelConverter converter = new KeyValueMapModelConverter(EntrezGene.class);
		EntrezGene gene = (EntrezGene) converter.convert(map);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId() == 1L);
		Assert.isTrue("ABC".equals(gene.getPrimaryGeneSymbol()));
		Assert.isNull(gene.getChromosome());
	}

	@Test
	public void badMapConversionTest() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("entrezGeneId", "1");
		map.put("primaryGeneSymbol", "ABC");
		map.put("badField", "0");
		KeyValueMapModelConverter converter = new KeyValueMapModelConverter(new ObjectMapper(), EntrezGene.class);
		EntrezGene gene = (EntrezGene) converter.convert(map);
		Assert.isNull(gene);
	}
	
}
