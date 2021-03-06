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

package org.oncoblocks.centromere.core.util;

import java.lang.annotation.*;

/**
 * Annotation intended to be used in configuration classes to identify individual {@link org.oncoblocks.centromere.core.model.Model}
 *   classes, or classpath locations of model classes, that will be used to generate a {@link ModelRegistry}
 *   instance that identifies and maps valid persisted models in the current context.  Is used
 *   similarly to Spring's {@link org.springframework.context.annotation.ComponentScan}.
 * 
 * @author woemler
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface ModelScan {

	/**
	 * Array of classpath locations.  Overrides the {@code basePackages}.
	 * 
	 * @return an array of package locations.
	 */
	String[] value() default {};

	/**
	 * Array of classpath locations.  Will be ignored if {@code value} is not null or empty.
	 *
	 * @return an array of package locations.
	 */
	String[] basePackages() default {};

	/**
	 * An array of individual classes to be registered as models in the context.  
	 * 
	 * @return an array of model classes.
	 */
	Class<?>[] basePackageClasses() default {};
}
