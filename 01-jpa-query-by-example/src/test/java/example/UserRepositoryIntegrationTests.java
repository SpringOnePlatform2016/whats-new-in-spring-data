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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.data.domain.ExampleMatcher.*;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test showing the usage of JPA Query-by-Example support through Spring Data repositories.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 */
@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringApplicationConfiguration(classes = ApplicationConfiguration.class)
public class UserRepositoryIntegrationTests {

	@Autowired UserRepository repository;

	User skyler, walter, flynn, marie, hank;

	@Before
	public void setUp() {

		repository.deleteAll();

		this.skyler = repository.save(new User("Skyler", "White", 45));
		this.walter = repository.save(new User("Walter", "White", 50));
		this.flynn = repository.save(new User("Walter Jr. (Flynn)", "White", 17));
		this.marie = repository.save(new User("Marie", "Schrader", 38));
		this.hank = repository.save(new User("Hank", "Schrader", 43));
	}

	@Test
	public void countBySimpleExample() {

		Example<User> example = Example.of(new User(null, "White", null));

		assertThat(repository.count(example), is(3L));
	}

	@Test
	public void ignorePropertiesAndMatchByAge() {

		Example<User> example = Example.of(flynn, matching().//
				withIgnorePaths("firstname", "lastname"));

		assertThat(repository.findOne(example), is(flynn));
	}

	@Test
	public void substringMatching() {

		Example<User> example = Example.of(new User("er", null, null), matching().//
				withStringMatcher(StringMatcher.ENDING));

		assertThat(repository.findAll(example), hasItems(skyler, walter));
	}

	@Test
	public void matchStartingStringsIgnoreCase() {

		Example<User> example = Example.of(new User("Walter", "WHITE", null),
				matching().//
						withIgnorePaths("age").//
						withMatcher("firstname", startsWith()).//
						withMatcher("lastname", ignoreCase()));

		assertThat(repository.findAll(example), hasItems(flynn, walter));
	}

	@Test
	public void configuringMatchersUsingLambdas() {

		Example<User> example = Example.of(new User("Walter", "WHITE", null),
				matching().//
						withIgnorePaths("age").//
						withMatcher("firstname", matcher -> matcher.startsWith()).//
						withMatcher("lastname", matcher -> matcher.ignoreCase()));

		assertThat(repository.findAll(example), hasItems(flynn, walter));
	}

	@Test
	public void valueTransformer() {

		Example<User> example = Example.of(new User(null, "White", 99), matching(). //
				withMatcher("age", matcher -> matcher.transform(value -> Integer.valueOf(50))));

		assertThat(repository.findAll(example), hasItems(walter));
	}
}
