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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.test.TestSubscriber;
import rx.Observable;
import rx.Single;

/**
 * Integration tests showing the usage of Reactive MongoDB support using mixed reactive types through Spring Data
 * repositories.
 * 
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactivePersonRepositoryIntegrationTests {

	@Autowired ReactivePersonRepository repository;

	Person skyler, walter, flynn;

	@Before
	public void setUp() {
		TestSubscriber<Void> testSubscriber = TestSubscriber.subscribe(repository.deleteAll());
		testSubscriber.await();

		this.skyler = subscribe(repository.save(new Person("Skyler", "White", 45)));
		this.walter = subscribe(repository.save(new Person("Walter", "White", 50)));
		this.flynn = subscribe(repository.save(new Person("Walter Jr. (Flynn)", "White", 17)));
	}

	@Test
	public void shouldFindByLastname() {

		Observable<Person> whites = repository.findByLastname("White");

		List<Person> result = whites.toList().toBlocking().single();

		assertThat(result, hasItems(skyler, walter, flynn));
	}

	@Test
	public void shouldFindByFirstnameAndLastname() {

		Mono<Person> walter = repository.findByFirstnameAndLastname("Walter", "White");

		assertThat(walter.block(), is(this.walter));
	}

	/**
	 * Parameters can be wrapped inside of {@link Mono}, {@link Flux}, or {@link org.reactivestreams.Publisher}.
	 * Parameters are unwrapped upon subscription to the result type.
	 */
	@Test
	public void shouldFindByWrappedLastname() {

		Mono<Person> walter = repository.findByFirstnameAndLastname(Mono.just("Walter"), "White");

		TestSubscriber<Person> testSubscriber = TestSubscriber.subscribe(walter);

		testSubscriber.await();
		testSubscriber.assertNoError();
		testSubscriber.assertValues(this.walter);
	}

	/**
	 * Reactive Repositories allow redeclaration of base methods and wrapper type conversion.
	 */
	@Test
	public void findOne() {

		Single<Person> findOneRxJava = repository.findOne(Single.just(skyler.getObjectId()));
		Person skylerFromRxJava = findOneRxJava.toBlocking().value();

		assertThat(skylerFromRxJava, is(skyler));

		Mono<Person> findOneReactor = repository.findOne(Mono.just(skyler.getObjectId()));
		Person skylerFromRxReactor = findOneReactor.block();

		assertThat(skylerFromRxReactor, is(skyler));
	}

	private <T> T subscribe(Mono<? extends T> mono) {
		return mono.block();
	}
}
