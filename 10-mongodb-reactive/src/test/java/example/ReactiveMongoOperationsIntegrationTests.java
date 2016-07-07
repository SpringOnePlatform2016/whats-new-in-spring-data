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
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.test.TestSubscriber;

/**
 * Integration tests showing the usage of Reactive MongoDB support through
 * {@link org.springframework.data.mongodb.core.ReactiveMongoOperations}.
 * 
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactiveMongoOperationsIntegrationTests {

	@Autowired ReactiveMongoOperations operations;

	Person skyler, walter, flynn;

	@Before
	public void setUp() {
		TestSubscriber<Void> testSubscriber = TestSubscriber.subscribe(operations.dropCollection(Person.class));
		testSubscriber.await();

		this.skyler = subscribe(operations.save(new Person("Skyler", "White", 45)));
		this.walter = subscribe(operations.save(Mono.just(new Person("Walter", "White", 50))));
	}

	@Test
	public void shouldReturnCount() {

		Mono<Long> count = operations.count(new Query(), Person.class);

		assertThat(count.block(), is(2L));
	}

	@Test
	public void insertCountAndDeleteChain() {

		flynn = new Person("Walter Jr. (Flynn)", "White", 17);

		Flux<Long> flux = operations.insert(Mono.just(flynn))//
				.then(() -> {
					return operations.count(new Query(), Person.class);
				}) //
				.flatMap(count -> {
					System.out.println("Count: " + count);
					assertThat(count, is(3L));
					return operations.remove(flynn);
				}) //
				.flatMap(deleteResult -> operations.count(new Query(), Person.class)) //
				.doOnNext(count -> {
					System.out.println("Count: " + count);
					assertThat(count, is(2L));
				});

		flux.then().block();
	}

	private <T> T subscribe(Mono<? extends T> mono) {
		return mono.block();
	}
}
