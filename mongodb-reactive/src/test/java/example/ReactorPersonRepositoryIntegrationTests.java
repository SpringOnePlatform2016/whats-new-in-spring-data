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

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.TestSubscriber;

/**
 * Integration tests showing the usage of Reactive MongoDB support using Project Reactor types through Spring Data
 * repositories.
 * 
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactorPersonRepositoryIntegrationTests {

	@Autowired ReactorPersonRepository repository;

	Person skyler, walter, flynn;

	@Before
	public void setUp() {
		TestSubscriber<Void> testSubscriber = TestSubscriber.subscribe(repository.deleteAll());
		testSubscriber.await();

		this.skyler = subscribe(repository.save(new Person("Skyler", "White", 45)));
		this.walter = subscribe(repository.save(new Person("Walter", "White", 50)));
		this.flynn = subscribe(repository.save(new Person("Walter Jr. (Flynn)", "White", 17)));
	}

	/**
	 * Observables are handles to an execution. Nothing happens without a subscription to a
	 * {@link org.reactivestreams.Publisher}.
	 */
	@Test
	public void shouldExecuteOnlyOnSubscribe() {

		assertThat(subscribe(repository.count()), is(3L));

		repository.save(new Person("Marie", "Schrader", 38));
		repository.save(new Person("Hank", "Schrader", 43));

		assertThat(subscribe(repository.count()), is(3L));
	}

	/**
	 * {@link Flux} execution can emit zero, one or many items. Declaring {@link Flux} as return type translates to a
	 * MongoDB {@code find} execution.
	 */
	@Test
	public void shouldFindByLastname() {

		Flux<Person> whites = repository.findByLastname("White");

		TestSubscriber<Person> testSubscriber = TestSubscriber.subscribe(whites.doOnNext(System.out::println));

		testSubscriber.await();
		testSubscriber.assertNoError();
		testSubscriber.assertValueCount(3);
		testSubscriber.assertContainValues(Sets.newLinkedHashSet(skyler, walter, flynn));
	}

	/**
	 * {@link Mono} execution can emit zero or one item. Declaring {@link Mono} as return type translates to a MongoDB
	 * {@code findOne} execution.
	 */
	@Test
	public void shouldFindByFirstnameAndLastname() {

		Mono<Person> walter = repository.findByFirstnameAndLastname("Walter", "White");

		TestSubscriber<Person> testSubscriber = TestSubscriber.subscribe(walter);

		testSubscriber.await();
		testSubscriber.assertNoError();
		testSubscriber.awaitAndAssertNextValues(this.walter);
	}

	/**
	 * Parameters can be wrapped inside of {@link Mono}, {@link Flux}, or {@link org.reactivestreams.Publisher}.
	 * Parameters are unwrapped upon subscription to the result type.
	 */
	@Test
	public void shouldFindByWrappedLastname() {

		Flux<Person> whites = repository.findByLastname(Mono.just("White"));

		TestSubscriber<Person> testSubscriber = TestSubscriber.subscribe(whites);

		testSubscriber.await();
		testSubscriber.assertNoError();
		testSubscriber.assertValueCount(3);
		testSubscriber.assertContainValues(Sets.newLinkedHashSet(skyler, walter, flynn));
	}

	private <T> T subscribe(Mono<? extends T> mono) {
		return mono.block();
	}
}
