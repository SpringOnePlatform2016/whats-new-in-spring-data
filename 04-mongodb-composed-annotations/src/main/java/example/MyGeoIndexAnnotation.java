/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

/**
 * @author Mark Paluch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@GeoSpatialIndexed
@interface MyGeoIndexAnnotation {

	@AliasFor(annotation = GeoSpatialIndexed.class, attribute = "name")
	String value();

	@AliasFor(annotation = GeoSpatialIndexed.class, attribute = "bits")
	int bits() default 2;

	@AliasFor(annotation = GeoSpatialIndexed.class, attribute = "max")
	int max() default 50;

	@AliasFor(annotation = GeoSpatialIndexed.class, attribute = "min")
	int min() default -120;

	@AliasFor(annotation = GeoSpatialIndexed.class, attribute = "type")
	GeoSpatialIndexType type() default GeoSpatialIndexType.GEO_2D;

	@AliasFor(annotation = GeoSpatialIndexed.class, attribute = "useGeneratedName")
	boolean dont_change_me() default false;

	String comment() default "What light?";
}
