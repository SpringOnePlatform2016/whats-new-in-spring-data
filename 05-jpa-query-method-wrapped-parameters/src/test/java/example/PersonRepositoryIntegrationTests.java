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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import scala.Option;

/**
 * Integration test showing the usage of JPA wrapped query method parameters support through Spring Data repositories.
 *
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringApplicationConfiguration(classes = ApplicationConfiguration.class)
public class PersonRepositoryIntegrationTests {

	@Autowired PersonRepository repository;

	Person gomez, mortica, thing, itt;

	@Before
	public void setUp() {

		repository.deleteAll();

		this.gomez = repository.save(new Person("Gomez", "Addams", "male"));
		this.mortica = repository.save(new Person("Mortica", "Addams", "female"));
		this.thing = repository.save(new Person("Thing T.", "Thing", null));
		this.itt = repository.save(new Person("Cousin", "Itt", null));
	}

	@Test
	public void findByGender() {

		assertThat(repository.findByGender("male"), contains(gomez));

		assertThat(repository.findByGender((String) null), hasItems(thing, itt));
		assertThat(repository.findByGender((String) null), not(hasItems(gomez, mortica)));
	}

	@Test
	public void findByGenderWrappedIn8Optional() {

		assertThat(repository.findByGender(Optional.of("male")), contains(gomez));

		assertThat(repository.findByGender(Optional.empty()), hasItems(thing, itt));
		assertThat(repository.findByGender(Optional.empty()), not(hasItems(gomez, mortica)));
	}

	@Test
	public void findByGenderWrappedInGuavaOptional() {

		assertThat(repository.findByGender(com.google.common.base.Optional.of("male")), contains(gomez));

		assertThat(repository.findByGender(com.google.common.base.Optional.absent()), hasItems(thing, itt));
		assertThat(repository.findByGender(com.google.common.base.Optional.absent()), not(hasItems(gomez, mortica)));
	}

	@Test
	public void findByGenderWrappedInScalaOption() {

		assertThat(repository.findByGender(Option.apply("male")), contains(gomez));

		assertThat(repository.findByGender(Option.empty()), hasItems(thing, itt));
		assertThat(repository.findByGender(Option.empty()), not(hasItems(gomez, mortica)));
	}

}
