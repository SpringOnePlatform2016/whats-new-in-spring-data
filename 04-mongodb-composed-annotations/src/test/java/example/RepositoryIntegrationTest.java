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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test showing the usage of MongoDB Composed Annotations support through a MongoDB repository.
 *
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class RepositoryIntegrationTest {

	@Autowired PersonRepository repository;

	Person skyler, walter, flynn;

	@Before
	public void setUp() {

		repository.deleteAll();

		this.skyler = repository.save(new Person("Skyler", "White"));
		this.walter = repository.save(new Person("Walter", "White"));
		this.flynn = repository.save(new Person("Walter Jr. (Flynn)", "White"));
	}

	/**
	 * Execute a query method annotated with the built-in {@link Query} annotation.
	 */
	@Test
	public void findWalterUsingQueryAnnotation() {

		List<Person> result = repository.findWalterUsingQueryAnnotation();

		assertThat(result, contains(walter));
	}

	/**
	 * Execute a query method annotated with a custom {@link FindWalter} annotation.
	 */
	@Test
	public void findWalterUsingComposedAnnotations() {

		List<Person> result = repository.findWalterUsingComposedAnnotations();

		assertThat(result, contains(walter));
	}
}
