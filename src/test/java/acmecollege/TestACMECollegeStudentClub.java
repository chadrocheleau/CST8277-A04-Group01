/**
 * File:  TestACMECollegeStudentClub.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */

package acmecollege;

import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.NonAcademicStudentClub;
import acmecollege.entity.StudentClub;

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
public class TestACMECollegeStudentClub extends TestACMECollegeSystem {
	
	private static AcademicStudentClub newAcademicStudentClub;
	private static NonAcademicStudentClub newNonAcademicStudentClub;
	
	private static int newAcademicSCID;
	private static int newNonAcademicSCID;
	
	/**
	 * Initializes a new AcademicStudentClub and a NonAcademicStudentClub to be
	 * used as entities for create and delete operations that, when successful, should
	 * return the values of the created or deleted record.
	 * @throws Exception
	 */
	@BeforeAll
	public static void initTestEntities() throws Exception {

		newAcademicStudentClub = new AcademicStudentClub();
		newAcademicStudentClub.setName(NEW_ACADEMIC_CLUB_NAME);
		
		newNonAcademicStudentClub = new NonAcademicStudentClub();
		newNonAcademicStudentClub.setName(NEW_NON_ACADEMIC_CLUB_NAME);

	}
	
	/**
	 * This method tests the GET /studentclub end point when accessed by a user in ADMIN_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test 
	@Order(1)
	public void getAllStudentClubs_withAdminRole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
			.register(adminAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME)
			.request()
			.get();
		
		List<Object> sClubs = response.readEntity(new GenericType<List<Object>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(sClubs.size(), is(2));
	}
	
	/**
	 * This method tests the GET /studentclub end point when accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test 
	@Order(2)
	public void getAllStudentClubs_withUserRole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
			.register(userAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME)
			.request()
			.get();
		
		List<Object> sClubs = response.readEntity(new GenericType<List<Object>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(sClubs.size(), is(2));
	}
	
	/**
	 * This method tests the GET /studentclub/{id} end point when accessed by a user in ADMIN_ROLE
	 * when searching for a student club that exists. THe request uses an ID value that
	 * corresponds to the second student club provided as sample data and therefore expects the
	 * returned student club to match those values.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(3)
	public void getStudentClub_ById_AsAdminRole_WithResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + ID_PATH_2)
			.request()
			.get();

		StudentClub returnedStudentClub = response.readEntity(new GenericType<StudentClub>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedStudentClub.getName(), is(CLUB_2));
	}
	
	/**
	 * This method tests the GET /studentclub/{id} end point when accessed by a user in ADMIN_ROLE
	 * when searching for a student club that does not exist.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(4)
	public void getStudentClub_ById_AsAdminRole_NoResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + ID_PATH_20)
			.request()
			.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
	/**
	 * This method tests the GET /studentclub/{id} end point when accessed by a user in USER_ROLE
	 * when searching for a student club that exists. THe request uses an ID value that
	 * corresponds to the second student club provided as sample data and therefore expects the
	 * returned student club to match those values.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(5)
	public void getStudentClub_ById_AsUserRole_WithResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(userAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + ID_PATH_2)
			.request()
			.get();

		StudentClub returnedStudentClub = response.readEntity(new GenericType<StudentClub>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedStudentClub.getName(), is(CLUB_2));
	}
	
	/**
	 * This method tests the GET /studentclub/{id} end point when accessed by a user in USER_ROLE
	 * when searching for a student club that does not exist.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(6)
	public void getStudentClub_ById_AsUserRole_NoResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(userAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + ID_PATH_20)
			.request()
			.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
	/**
	 * This method tests the POST /studentclub end point when accessed by a user in ADMIN_ROLE.
	 * This test will produce a new Academic StudentClub, the third StudentClub.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(7)
	public void postNewAcademicStudentClub_AdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(adminAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME)
				.request()
				.post(Entity.entity(newAcademicStudentClub, MediaType.APPLICATION_JSON));

		StudentClub createdSC = response.readEntity(new GenericType<StudentClub>() {});
		assertThat(response.getStatus(), is(OK));
		assertThat(createdSC.getName(), is(NEW_ACADEMIC_CLUB_NAME));
		assertThat(createdSC.getIsAcademic(), is(true));
		
		newAcademicSCID = createdSC.getId();
	}

	/**
	 * This method tests the POST /studentclub end point for an AcademicStudentClub when 
	 * accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(8)
	public void postNewAcademicStudentClub_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(userAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME)
				.request()
				.post(Entity.entity(newAcademicStudentClub, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	/**
	 * This method tests the POST /studentclub end point when accessed by a user in ADMIN_ROLE.
	 * This test will produce a new NonAcademic StudentClub, the fourth StudentClub.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(9)
	public void postNewNonAcademicStudentClub_AdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(adminAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME)
				.request()
				.post(Entity.entity(newNonAcademicStudentClub, MediaType.APPLICATION_JSON));

		StudentClub createdSC = response.readEntity(new GenericType<StudentClub>() {});
		assertThat(response.getStatus(), is(OK));
		assertThat(createdSC.getName(), is(NEW_NON_ACADEMIC_CLUB_NAME));
		assertThat(createdSC.getIsAcademic(), is(false));
		
		newNonAcademicSCID = createdSC.getId();
	}

	/**
	 * This method tests the POST /studentclub end point for a NonAcademicStudentClub when 
	 * accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(10)
	public void postNewNonAcademicStudentClub_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(userAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME)
				.request()
				.post(Entity.entity(newNonAcademicStudentClub, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	/**
	 * This method tests the DELETE /studentclub/{id} end point to delete the previously created
	 * AcademicClub when accessed by a user in ADMIN_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(11)
	public void deleteAcademicStudentClub_AdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(adminAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME + "/" + newAcademicSCID)
				.request()
				.delete();

		StudentClub deletedSC = response.readEntity(new GenericType<StudentClub>() {});
		assertThat(response.getStatus(), is(OK));
		assertThat(deletedSC.getName(), is(NEW_ACADEMIC_CLUB_NAME));
		assertThat(deletedSC.getIsAcademic(), is(true));
		
	}
	
	/**
	 * This method tests the DELETE /studentclub/{id} end point for an AcademicStudentClub when 
	 * accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(12)
	public void deleteAcademicStudentClub_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(userAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME + ID_PATH_2)
				.request()
				.delete();

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	/**
	 * This method tests the DELETE /studentclub/{id} end point to delete the previously created
	 * NonAcademicClub when accessed by a user in ADMIN_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(13)
	public void deleteNonAcademicStudentClub_AdminRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(adminAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME + "/" + newNonAcademicSCID)
				.request()
				.delete();

		StudentClub deletedSC = response.readEntity(new GenericType<StudentClub>() {});
		assertThat(response.getStatus(), is(OK));
		assertThat(deletedSC.getName(), is(NEW_NON_ACADEMIC_CLUB_NAME));
		assertThat(deletedSC.getIsAcademic(), is(false));
		
	}
	
	/**
	 * This method tests the DELETE /studentclub/{id} end point for a NonAcademicStudentClub when 
	 * accessed by a user in USER_ROLE.
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(14)
	public void deleteNonAcademicStudentClub_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(userAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME + ID_PATH_2)
				.request()
				.delete();

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	// ******** CONSTANTS ********
	
	private static final String ID_PATH_2 = "/2";
	private static final String ID_PATH_20 = "/20";
	private static final String CLUB_2 = "Mountain Hiking Club";
	private static final String NEW_ACADEMIC_CLUB_NAME = "New Student Club";
	private static final String NEW_NON_ACADEMIC_CLUB_NAME = "New Non Academic Student Club";

}