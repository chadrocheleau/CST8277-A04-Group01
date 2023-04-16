/**
 * File:  TestACMECollegeMembershipCard.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */

package acmecollege;

import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CARD_STUDENT_LIST_ID_PATH;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import acmecollege.entity.MembershipCard;
import acmecollege.entity.Student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
public class TestACMECollegeMembershipCard extends TestACMECollegeSystem {

	private static Student firstStudent;
	private static int postedMembershipCardID;
	
	/**
	 * Initializes a new Student to be linked to a MembershipCard. The MembershipCard will be
	 * used as an entity for create and delete operations that, when successful, should return
	 * the values of the created or deleted record (and, therefore, the values of the Student).
	 * @throws Exception
	 */
	@BeforeAll
	public static void initTestEntities() throws Exception {

		firstStudent = new Student();
		firstStudent.setFullName(STUDENT_1_FIRST_NAME, STUDENT_1_LAST_NAME);

	}
	
	/**
	 * This method tests the GET /membershipcard end point when accessed by a user in ADMIN_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(1)
	public void getAllMembershipCards_WithAdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(MEMBERSHIP_CARD_RESOURCE_NAME)
			.request()
			.get();
		
		List<MembershipCard> mCards = response.readEntity(new GenericType<List<MembershipCard>>(){});
		
		assertThat(response.getStatus(), is(OK));
		assertThat(mCards, is(not(empty())));
		
	}
	
	/**
	 * This method tests the GET /membershipcard end point when accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(2)
	public void getAllMembershipCards_WithUserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(userAuth)
			.path(MEMBERSHIP_CARD_RESOURCE_NAME)
			.request()
			.get();
		
		assertThat(response.getStatus(), is(FORBIDDEN));   
		
	}
	
	/**
	 * This method tests the GET /membershipcard/card/list/student/{studentID} end point when
	 * accessed by a user in ADMIN_ROLE when searching for existing membership cards linked
	 * to a student ID. The request uses an ID value that corresponds to the first student
	 * provided as sample data and therefore expects the existing membership card to be returned
	 * as part of a List.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(3)
	public void getMembershipCards_ByStudentId_AsAdminRole_WithResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + ID_PATH_1)
				.request()
				.get();

		List<MembershipCard> returnedMembershipCards = response.readEntity(new GenericType<List<MembershipCard>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedMembershipCards, is(not(empty())));	
	}
	
	/**
	 * This method tests the GET /membershipcard/card/list/student/{studentID} end point when
	 * accessed by a user in ADMIN_ROLE when searching for existing membership cards linked
	 * to a student ID without any active membership cards.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(4)
	public void getMembershipCards_ByStudentId_AsAdminRole_NoResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + ID_PATH_2)
				.request()
				.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
	/**
	 * This method tests the GET /membershipcard/card/list/student/{studentID} end point when
	 * accessed by a user in USER_ROLE when searching for existing membership cards linked
	 * to a student ID matching that of the user. The request uses an ID value that corresponds 
	 * to the first student provided as sample data and therefore expects the existing membership 
	 * card to be returned as part of a List.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(5)
	public void getMembershipCards_ByStudentId_AsUserRole_ForMatchingStudent() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + ID_PATH_1)
				.request()
				.get();

		List<MembershipCard> returnedMembershipCards = response.readEntity(new GenericType<List<MembershipCard>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedMembershipCards, is(not(empty())));	
	}
	
	/**
	 * This method tests the GET /membershipcard/card/list/student/{studentID} end point when
	 * accessed by a user in USER_ROLE when searching for existing membership cards linked
	 * to a student ID that does not match the user (i.e. a separate student).
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(6)
	public void getMembershipCards_ByStudentId_AsUserRole_ForNonMatchingStudent() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + ID_PATH_2)
				.request()
				.get();

		assertThat(response.getStatus(), is(FORBIDDEN));	
	}
	
	/**
	 * This method tests the POST /membershipcard/student/{studentID}/studentclub/{scID} end point
	 * when accessed by a user in ADMIN_ROLE. This test will produce a new MembershipCard linked
	 * to the sample Student (studentID: 1) and the sample StudentClub (scID: 1).
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(7)
	public void postNewMembershipCard_AdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + 
					STUDENT_RESOURCE_NAME + ID_PATH_1 + "/" + 
					STUDENT_CLUB_RESOURCE_NAME + ID_PATH_1)
			.request()
			.post(Entity.entity(firstStudent, MediaType.APPLICATION_JSON));
		
		MembershipCard createdMembershipCard = response.readEntity(new GenericType<MembershipCard>() {});

		assertThat(response.getStatus(), is(OK));
		assertThat(createdMembershipCard.getOwner().getFirstName(), is(STUDENT_1_FIRST_NAME));
		assertThat(createdMembershipCard.getOwner().getLastName(), is(STUDENT_1_LAST_NAME));
		assertThat(createdMembershipCard.getMembership().getStudentClub().getName(), is(CLUB_1));
		
		postedMembershipCardID = createdMembershipCard.getId();
		
	}
	
	/**
	 * This method tests the POST /membershipcard/student/{studentID}/studentclub/{scID} end point
	 * when accessed by a user in USER_ROLE. 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(8)
	public void postNewMembershipCard_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(userAuth)
			.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + 
					STUDENT_RESOURCE_NAME + ID_PATH_1 + "/" + 
					STUDENT_CLUB_RESOURCE_NAME + ID_PATH_1)
			.request()
			.post(Entity.entity(firstStudent, MediaType.APPLICATION_JSON));
		
		assertThat(response.getStatus(), is(FORBIDDEN));
		
	}
	
	/**
	 * This method tests the DELETE /membershipcard/{id} end point to delete the previously created
	 * MembershipCard when accessed by a user in ADMIN_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(9)
	public void deleteMembershipCard_AdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + postedMembershipCardID)
			.request()
			.delete();
		
		MembershipCard deletedMembershipCard = response.readEntity(new GenericType<MembershipCard>() {});

		assertThat(response.getStatus(), is(OK));
		assertThat(deletedMembershipCard.getOwner().getFirstName(), is(STUDENT_1_FIRST_NAME));
		assertThat(deletedMembershipCard.getOwner().getLastName(), is(STUDENT_1_LAST_NAME));
		assertThat(deletedMembershipCard.getMembership().getStudentClub().getName(), is(CLUB_1));
		
	}
	
	/**
	 * This method tests the DELETE /membershipcard/{id} end point to delete a MembershipCard 
	 * when accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(10)
	public void deleteMembershipCard_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(userAuth)
			.path(MEMBERSHIP_CARD_RESOURCE_NAME + ID_PATH_1)
			.request()
			.delete();
		
		assertThat(response.getStatus(), is(FORBIDDEN));
		
	}

	// *********************** CONSTANTS ******************************

	private static final String STUDENT_1_FIRST_NAME = "John";
	private static final String STUDENT_1_LAST_NAME = "Smith";

	private static final String CLUB_1 = "Computer Programming Club";
	
	private static final String ID_PATH_1 = "/1";
	private static final String ID_PATH_2 = "/2";
	
}
