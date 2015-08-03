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

package org.oncoblocks.centromere.core.web.controller;

import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.web.service.ServiceOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * @author woemler
 */
public class EntityQueryController<T extends Model<ID>, ID extends Serializable> 
		extends AbstractCrudController<T, ID> {

	public EntityQueryController(ServiceOperations<T, ID> service,
			ResourceAssemblerSupport<T, FilterableResource> assembler) {
		super(service, assembler);
	}

	/**
	 * GET request for a collection of entities. Can be paged and sorted using {@link org.springframework.data.domain.Pageable},
	 *   or just return a default-ordered list (typically by primary key ID). Results can be filtered 
	 *   using query string parameters that map to {@code T} entity attributes.  Supports field filtering.
	 * 
	 * @param entity Instance of a model entity, composed of fragments from request parameters. 
	 * @param fields set of field names to be included in response object.
	 * @param exclude set of field names to be excluded from the response object.
	 * @param showLinks flag to enable HAL links for entities 
	 * @param pageable {@link org.springframework.data.domain.Pageable} request object, created using 
	 *   'page', 'size', or 'sort' as query string parameters. 
	 * @param pagedResourcesAssembler {@link org.springframework.data.web.PagedResourcesAssembler} for 
	 *   assembling paged entities into a wrapped response object with hypermedia links. 
	 * @param request {@link javax.servlet.http.HttpServletRequest}
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity find(
			@ModelAttribute T entity,
			@RequestParam(value = "fields", required = false) Set<String> fields,
			@RequestParam(value = "exclude", required = false) Set<String> exclude,
			@RequestParam(value = "hal", defaultValue = "true") boolean showLinks,
			@PageableDefault(size = 1000) Pageable pageable,
			PagedResourcesAssembler<T> pagedResourcesAssembler, 
			HttpServletRequest request) {
		return doFind(entity, fields, exclude, showLinks, pageable, pagedResourcesAssembler, request);
	}

	/**
	 * {@link EntityQueryController#find}
	 */
	protected ResponseEntity doFind(T entity, Set<String> fields, Set<String> exclude, boolean showLinks,
			Pageable pageable, PagedResourcesAssembler resourcesAssembler, HttpServletRequest request) {
		ResponseEnvelope envelope = null;
		Map<String,String[]> params = request.getParameterMap();
		Link selfLink = new Link(linkTo(this.getClass()).slash("").toString() +
				(request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");
		if (params.containsKey("page") || params.containsKey("size")){
			Page<T> page = service.findPaged(entity, pageable);
			if (showLinks){
				PagedResources<ResourceSupport> pagedResources = resourcesAssembler.toResource(page, assembler, selfLink);
				envelope = new ResponseEnvelope<>(pagedResources, fields, exclude);
			} else {
				envelope = new ResponseEnvelope<>(page, fields, exclude);
			}
			
		} else if (params.containsKey("sort")){
			List<T> entities = (List<T>) service.findSorted(entity, pageable.getSort());
			if (showLinks){
				List<FilterableResource> resourceList = assembler.toResources(entities);
				Resources<FilterableResource> resources = new Resources<>(resourceList);
				resources.add(selfLink);
				envelope = new ResponseEnvelope<>(resources, fields, exclude);
			} else {
				envelope = new ResponseEnvelope<>(entity, fields, exclude);	
			}
		} else {
			List<T> entities = (List<T>) service.find(entity);
			if (showLinks){
				List<FilterableResource> resourceList = assembler.toResources(entities);
				Resources<FilterableResource> resources = new Resources<>(resourceList);
				resources.add(selfLink);
				envelope = new ResponseEnvelope<>(resources, fields, exclude);
			} else {
				envelope = new ResponseEnvelope<>(entities, fields, exclude);	
			}
		}
		return new ResponseEntity<>(envelope, HttpStatus.OK);
	}
}
