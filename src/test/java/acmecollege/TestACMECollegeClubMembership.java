/**
 * File:  TestACMECollegeClubMembership.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */
package acmecollege;

import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CARD_STUDENT_LIST_ID_PATH;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.annotation.Testable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.MembershipCard;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeClubMembership extends TestACMECollegeSystem {

	private static ClubMembership newClubMembership;
	
	@Test 
	@Order(1)
	public void getAllClubMemberships_withAdminRole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
			.register(adminAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME)
			.request()
			.get();
		
		List<ClubMembership> clubMemberships = response.readEntity(new GenericType<List<ClubMembership>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(clubMemberships.size(), is(2));
	}
	
	@Test 
	@Order(2)
	public void getAllClubMemberships_withUserRole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
			.register(userAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME)
			.request()
			.get();
		
		List<ClubMembership> clubMemberships = response.readEntity(new GenericType<List<ClubMembership>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(clubMemberships.size(), is(2));
	}
	
	@Test
	@Order(3)
	public void getClubMembership_ById_AsAdminRole_WithResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth)
				.path(CLUB_MEMBERSHIP_RESOURCE_NAME + ID_PATH_2)
				.request()
				.get();

		ClubMembership returnedClubMembership = response.readEntity(new GenericType<ClubMembership>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedClubMembership.getStudentClub().getName(), is(DEFAULT_STUDENT_CLUB_NAME));	
	}
	
	@Test
	@Order(4)
	public void getClubMembership_ById_AsUserRole_WithResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(CLUB_MEMBERSHIP_RESOURCE_NAME + ID_PATH_2)
				.request()
				.get();

		ClubMembership returnedClubMembership = response.readEntity(new GenericType<ClubMembership>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedClubMembership.getStudentClub().getName(), is(DEFAULT_STUDENT_CLUB_NAME));	
	}
	
	@Test
	@Order(5)
	public void postNewClubMembership_AsAdminRole() throws JsonMappingException, JsonProcessingException {

		newClubMembership = new ClubMembership();
		// newClubMembership.set
		
		Response response = webTarget
			.register(adminAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME + ID_PATH_1)
			.request()
			.post(Entity.entity(newClubMembership, MediaType.APPLICATION_JSON));
	}

	

	// *********************** CONSTANTS ******************************

	private final String ID_PATH_1 = "/1";
	private final String ID_PATH_2 = "/2";
	private final String DEFAULT_STUDENT_CLUB_NAME = "Mountain Hiking Club";
	
}
