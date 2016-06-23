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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test showing the usage of MongoDB Composed Annotations support.
 * 
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class ComposedAnnotationIntegrationTest {

	@Autowired MongoOperations operations;

	Venue theWhiteResidence, jessesHouse;
	ImprovedVenue carWash, pollosHermanos, saulsOffice;

	@Before
	public void setUp() {

		operations.remove(new Query(), Venue.class);
		operations.remove(new Query(), ImprovedVenue.class);

		this.theWhiteResidence = new Venue("308 Negra Arroyo Lane, Albuquerque, New Mexico, 87104",
				new Point(-106.5387498, 35.1261101));

		this.jessesHouse = new Venue("9809 Margo Street, Albuquerque, New Mexico 87104",
				new Point(-106.6677747, 35.087538));

		this.carWash = new ImprovedVenue("9516 Snow Heights Circle NE, Albuqerque, New Mexico 87112",
				new Point(-106.5369764, 35.1084638));

		this.pollosHermanos = new ImprovedVenue("12000 – 12100 Coors Rd SW, Albuquerque NM, 87045",
				new Point(-106.688545, 35.0146382));

		this.saulsOffice = new ImprovedVenue("9800 Montgomery Blvd NE, Albuquerque, New Mexico, 87111",
				new Point(-106.5346129, 35.1293502));

		operations.save(this.theWhiteResidence);
		operations.save(this.jessesHouse);
		operations.save(this.carWash);
		operations.save(this.pollosHermanos);
		operations.save(this.saulsOffice);
	}

	/**
	 * Issue a {@code geoNear} query using a 2d legacy index specified by the built-in {@link GeospatialIndex} annotation.
	 */
	@Test
	public void findsVenuesWithRegularAnnotations() {

		GeoResults<Venue> geoResults = operations
				.geoNear(NearQuery.near(jessesHouse.getPoint()).maxDistance(10, Metrics.MILES), Venue.class);

		assertThat(geoResults.getContent(), hasSize(2));

		GeoResult<Venue> geoResult = geoResults.getContent().get(1);

		assertThat(geoResult.getContent(), is(equalTo(theWhiteResidence)));
		assertThat(geoResult.getDistance().getValue(), is(closeTo(7.7, 0.1)));
	}

	/**
	 * Issue a {@code geoNear} query using a 2d legacy index specified by a custom, composed {@link MyGeoIndexAnnotation}
	 * annotation.
	 */
	@Test
	public void findsVenuesWithComposedAnnotations() {

		GeoResults<ImprovedVenue> geoResults = operations
				.geoNear(NearQuery.near(theWhiteResidence.getPoint()).maxDistance(2, Metrics.MILES), ImprovedVenue.class);

		assertThat(geoResults.getContent(), hasSize(2));

		GeoResult<ImprovedVenue> geoResult = geoResults.getContent().get(1);

		assertThat(geoResult.getContent(), is(equalTo(carWash)));
		assertThat(geoResult.getDistance().getValue(), is(closeTo(1.2, 0.1)));
	}
}
