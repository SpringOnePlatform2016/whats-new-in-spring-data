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
import org.springframework.data.repository.Repository;

import reactor.core.publisher.Mono;
import rx.Observable;
import rx.Single;

/**
 * Reactive Repository using Project Reactor and RxJava types.
 * 
 * @author Mark Paluch
 */
@NoRepositoryBean
public interface ReactivePersonRepository extends Repository<Person, ObjectId> {

	/**
	 * Find one by {@code firstname},
	 *
	 * @param firstname
	 * @param lastname
	 * @return an {@link Single}
	 */
	Mono<Person> findByFirstnameAndLastname(String firstname, String lastname);

	/**
	 * Find many by {@code lastname}.
	 *
	 * @param lastname
	 * @return an {@link Observable}
	 */
	Observable<Person> findByLastname(String lastname);

	/**
	 * Find one by {@code firstname} wrapped inside a {@link Mono} and {@code lastname}.
	 *
	 * @param firstname
	 * @param lastname
	 * @return a {@link Mono}
	 */
	Mono<Person> findByFirstnameAndLastname(Mono<String> firstname, String lastname);

	/**
	 * Find one by {@code id} wrapped inside a {@link Mono}.
	 *
	 * @param objectId
	 * @return
	 */
	Mono<Person> findOne(Mono<ObjectId> objectId);

	/**
	 * Find one by {@code id} wrapped inside a {@link Single}.
	 *
	 * @param objectId
	 * @return
	 */
	Single<Person> findOne(Single<ObjectId> objectId);

	Mono<Void> deleteAll();

	Mono<Person> save(Person person);

}
