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

package org.oncoblocks.centromere.web.query;

import org.oncoblocks.centromere.core.repository.Evaluation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for modifying {@link org.oncoblocks.centromere.core.repository.QueryCriteria} creation
 *   in classes that extend {@link QueryParameters}.  Allows remapping of query parameter names with
 *   the {@code value} attribute, and alternate {@link Evaluation} values, which define repository
 *   query operators.
 * 
 * @author woemler
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueryParameter {
	String value() default "";
	Evaluation evalutation() default Evaluation.EQUALS;
}
