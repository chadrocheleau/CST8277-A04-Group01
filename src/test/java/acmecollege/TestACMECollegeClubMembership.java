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
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.DurationAndStatus;

/**
 * This test suite tests the ACME College API for the Student Entity 
 * of the ACME College System. This test suite uses TestMethodOrder
 * to ensure that the tests are run in a particular order as some tests 
 * may change expected results of other tests depending on the order
 * in which they are run.
 * @author paisl
 *
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeClubMembership extends TestACMECollegeSystem {

	private static ClubMembership newClubMembership;
	private static int postedClubMembershipID;
	
	
	/**
	 * Initializes a new DurationAndStatus and sets for a newly initialized ClubMembership. The
	 * ClubMembership will be used as an entity for create and delete operations that, when 
	 * successful, should return the values of the created or deleted record.
	 * @throws Exception
	 */
	@BeforeAll
	public static void initTestEntities() throws Exception {
		
		newClubMembership = new ClubMembership();

		DurationAndStatus ds = new DurationAndStatus();
		ds.setDurationAndStatus(LocalDateTime.now(), LocalDateTime.now().plusYears(1), "+");
		newClubMembership.setDurationAndStatus(ds);
		
	}
	
	/**
	 * This method tests the GET /clubmembership end point when accessed by a user in ADMIN_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
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
	
	/**
	 * This method tests the GET /clubmembership end point when accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
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
	
	/**
	 * This method tests the GET /clubmembership/{id} end point when accessed by a user in
	 * ADMIN_ROLE when searching for a club membership that exists. The request uses an ID value 
	 * that corresponds to the second club membership provided as sample data and therefore expects
	 * the returned club membership to match those values.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
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
		assertThat(returnedClubMembership.getStudentClub().getName(), is(CLUB_2));	
	}
	
	/**
	 * This method tests the GET /clubmembership/{id} end point when accessed by a user in 
	 * ADMIN_ROLE when searching for a club membership that does not exist.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(4)
	public void getClubMembership_ById_AsAdminRole_NoResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth)
				.path(CLUB_MEMBERSHIP_RESOURCE_NAME + ID_PATH_20)
				.request()
				.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
	/**
	 * This method tests the GET /clubmembership/{id} end point when accessed by a user in
	 * USER_ROLE when searching for a club membership that exists. The request uses an ID value 
	 * that corresponds to the second club membership provided as sample data and therefore expects
	 * the returned club membership to match those values.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(5)
	public void getClubMembership_ById_AsUserRole_WithResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(CLUB_MEMBERSHIP_RESOURCE_NAME + ID_PATH_2)
				.request()
				.get();

		ClubMembership returnedClubMembership = response.readEntity(new GenericType<ClubMembership>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedClubMembership.getStudentClub().getName(), is(CLUB_2));	
	}
	
	/**
	 * This method tests the GET /clubmembership/{id} end point when accessed by a user in 
	 * USER_ROLE when searching for a club membership that does not exist.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(6)
	public void getClubMembership_ById_AsUserRole_NoResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(CLUB_MEMBERSHIP_RESOURCE_NAME + ID_PATH_20)
				.request()
				.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
	/**
	 * This method tests the POST /clubmembership end point when accessed by a user in ADMIN_ROLE.
	 * This test will produce a new ClubMembership linked to the provided StudentClub with an ID 
	 * of 1.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(7)
	public void postNewClubMembership_AsAdminRole() throws JsonMappingException, JsonProcessingException {

		
		Response response = webTarget
			.register(adminAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + STUDENT_CLUB_RESOURCE_NAME + ID_PATH_1)
			.request()
			.post(Entity.entity(newClubMembership, MediaType.APPLICATION_JSON));
		
		ClubMembership createdCM = response.readEntity(new GenericType<ClubMembership>() {});

		assertThat(response.getStatus(), is(OK));
		assertThat(createdCM.getStudentClub().getName(), is(CLUB_1));
		assertThat(createdCM.getStudentClub().getIsAcademic(), is(true));
		assertThat(createdCM.getDurationAndStatus().getActive(), is((byte)1));
		
		postedClubMembershipID = createdCM.getId();
	}

	/**
	 * This method tests the POST /clubmembership end point when accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(8)
	public void postNewClubMembership_AsUserRole() throws JsonMappingException, JsonProcessingException {

		
		Response response = webTarget
			.register(userAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + STUDENT_CLUB_RESOURCE_NAME + ID_PATH_1)
			.request()
			.post(Entity.entity(newClubMembership, MediaType.APPLICATION_JSON));
		
		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	/**
	 * This method tests the DELETE /clubmembership/{id} end point to delete the previously created
	 * ClubMembership when accessed by a user in ADMIN_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(9)
	public void deleteClubMembership_AsAdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + postedClubMembershipID)
			.request()
			.delete();
		
		ClubMembership deletedCM = response.readEntity(new GenericType<ClubMembership>() {});

		assertThat(response.getStatus(), is(OK));
		assertThat(deletedCM.getStudentClub().getName(), is(CLUB_1));
		assertThat(deletedCM.getStudentClub().getIsAcademic(), is(true));
		assertThat(deletedCM.getDurationAndStatus().getActive(), is((byte)1));

	}

	/**
	 * This method tests the DELETE /clubmembership/{id} end point to delete a ClubMembership 
	 * when accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(10)
	public void deleteClubMembership_AsUserRole() throws JsonMappingException, JsonProcessingException {

		
		Response response = webTarget
			.register(userAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME + ID_PATH_2)
			.request()
			.delete();
		
		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	

	// *********************** CONSTANTS ******************************

	private static final String ID_PATH_1 = "/1";
	private static final String ID_PATH_2 = "/2";
	private static final String ID_PATH_20 = "/20";
	private static final String CLUB_1 = "Computer Programming Club";
	private static final String CLUB_2 = "Mountain Hiking Club";
	
}
