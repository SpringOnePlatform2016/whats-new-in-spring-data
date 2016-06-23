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

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import scala.Option;

/**
 * Simple repository interface for {@link Person} instances.
 *
 * @author Mark Paluch
 */
public interface PersonRepository extends CrudRepository<Person, Long> {

	/**
	 * @param gender a plain {@link String}
	 * @return
	 */
	List<Person> findByGender(String gender);

	/**
	 * @param gender a Java 8 {@link Optional}
	 * @return
	 */
	List<Person> findByGender(Optional<String> gender);

	/**
	 * @param gender a Guava {@link com.google.common.base.Optional}
	 * @return
	 */
	List<Person> findByGender(com.google.common.base.Optional<String> gender);

	/**
	 * @param gender a Scala {@link Option}
	 * @return
	 */
	List<Person> findByGender(Option<String> gender);

}
