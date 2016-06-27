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

import org.bson.types.ObjectId;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive Repository using Project Reactor types like {@link Flux} and {@link Mono}.
 * 
 * @author Mark Paluch
 */
@NoRepositoryBean
public interface ReactorPersonRepository extends ReactiveCrudRepository<Person, ObjectId> {

	/**
	 * Find one by {@code firstname} and {@code lastname}.
	 *
	 * @param firstname
	 * @param lastname
	 * @return a {@link Mono}
	 */
	Mono<Person> findByFirstnameAndLastname(String firstname, String lastname);

	/**
	 * Find many by {@code lastname}.
	 * 
	 * @param lastname
	 * @return a {@link Flux}
	 */
	Flux<Person> findByLastname(String lastname);

	/**
	 * Find many by {@code firstname} wrapped inside a {@link Mono}.
	 *
	 * @param lastname
	 * @return a {@link Flux}
	 */
	Flux<Person> findByLastname(Mono<String> lastname);

}
