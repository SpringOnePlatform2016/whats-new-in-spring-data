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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

/**
 * Integration tests showing the usage of Reactive MongoDB support using RxJava types through Spring Data repositories.
 * 
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RxJavaPersonRepositoryIntegrationTests {

	@Autowired RxJavaPersonRepository repository;

	Person skyler, walter, flynn;

	@Before
	public void setUp() {
		TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
		repository.deleteAll().subscribe(testSubscriber);
		testSubscriber.awaitTerminalEvent();

		this.skyler = subscribe(repository.save(new Person("Skyler", "White", 45)));
		this.walter = subscribe(repository.save(new Person("Walter", "White", 50)));
		this.flynn = subscribe(repository.save(new Person("Walter Jr. (Flynn)", "White", 17)));
	}

	/**
	 * Observables are handles to an execution. Nothing happens without a subscription to a {@link Observable}.
	 */
	@Test
	public void shouldExecuteOnlyOnSubscribe() {

		assertThat(subscribe(repository.count()), is(3L));

		repository.save(new Person("Marie", "Schrader", 38));
		repository.save(new Person("Hank", "Schrader", 43));

		assertThat(subscribe(repository.count()), is(3L));
	}

	/**
	 * {@link Observable} execution can emit zero, one or many items. Declaring {@link Observable} as return type
	 * translates to a MongoDB {@code find} execution.
	 */
	@Test
	public void shouldFindByLastname() {

		Observable<Person> whites = repository.findByLastname("White");

		TestSubscriber<Person> testSubscriber = new TestSubscriber<>();

		whites.doOnNext(System.out::println).subscribe(testSubscriber);

		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(3);

		assertThat(testSubscriber.getOnNextEvents(), hasItems(skyler, walter, flynn));
	}

	/**
	 * {@link Single} execution can emit zero or one item. Declaring {@link Single} as return type translates to a MongoDB
	 * {@code findOne} execution.
	 */
	@Test
	public void shouldFindByFirstnameAndLastname() {

		Single<Person> walter = repository.findByFirstnameAndLastname("Walter", "White");

		TestSubscriber<Person> testSubscriber = new TestSubscriber<>();

		walter.subscribe(testSubscriber);

		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();

		testSubscriber.assertValueCount(1);

		assertThat(testSubscriber.getOnNextEvents(), hasItems(this.walter));
	}

	/**
	 * Parameters can be wrapped inside of {@link Single} or {@link Observable}. Parameters are unwrapped upon
	 * subscription to the result type.
	 */
	@Test
	public void shouldFindByWrappedLastname() {

		Observable<Person> whites = repository.findByLastname(Single.just("White"));

		TestSubscriber<Person> testSubscriber = new TestSubscriber<>();

		whites.subscribe(testSubscriber);

		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertNoErrors();

		testSubscriber.assertValueCount(3);

		assertThat(testSubscriber.getOnNextEvents(), hasItems(skyler, walter, flynn));
	}

	private <T> T subscribe(Single<? extends T> single) {
		return single.toBlocking().value();
	}
}
