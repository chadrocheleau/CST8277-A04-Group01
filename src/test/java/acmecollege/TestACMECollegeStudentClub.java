package acmecollege;

import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeStudentClub extends TestACMECollegeSystem {
	
	private static AcademicStudentClub newAcademicStudentClub;
	private static NonAcademicStudentClub newNonAcademicStudentClub;
	
	private static int newAcademicSCID;
	private static int newNonAcademicSCID;
	
	@BeforeAll
	public static void initTestEntities() throws Exception {

		newAcademicStudentClub = new AcademicStudentClub();
		newAcademicStudentClub.setName(NEW_ACADEMIC_CLUB_NAME);
		
		newNonAcademicStudentClub = new NonAcademicStudentClub();
		newNonAcademicStudentClub.setName(NEW_NON_ACADEMIC_CLUB_NAME);

	}
	
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
	
	@Test
	@Order(3)
	public void getStudentClub_ById_AsAdminRole_WithResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + DEFAULT_ID_PATH_STUDENT_CLUB_NEW)
			.request()
			.get();

		StudentClub returnedStudentClub = response.readEntity(new GenericType<StudentClub>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedStudentClub.getName(), is(DEFAULT_STUDENT_CLUB_NAME));
	}
	
	@Test
	@Order(4)
	public void getStudentClub_ById_AsAdminRole_NoResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(adminAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + DEFAULT_ID_PATH_STUDENT_CLUB_NO_RECORD)
			.request()
			.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
	@Test
	@Order(5)
	public void getStudentClub_ById_AsUserRole_WithResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(userAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + DEFAULT_ID_PATH_STUDENT_CLUB_NEW)
			.request()
			.get();

		StudentClub returnedStudentClub = response.readEntity(new GenericType<StudentClub>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedStudentClub.getName(), is(DEFAULT_STUDENT_CLUB_NAME));
	}
	
	@Test
	@Order(6)
	public void getStudentClub_ById_AsUserRole_NoResults() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
			.register(userAuth)
			.path(STUDENT_CLUB_RESOURCE_NAME + DEFAULT_ID_PATH_STUDENT_CLUB_NO_RECORD)
			.request()
			.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
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
	
	@Test
	@Order(12)
	public void deleteAcademicStudentClub_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(userAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME + "/" + newAcademicSCID)
				.request()
				.delete();

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
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
	
	@Test
	@Order(14)
	public void deleteNonAcademicStudentClub_UserRole() throws JsonMappingException, JsonProcessingException {
		
		Response response = webTarget
				.register(userAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME + "/" + newNonAcademicSCID)
				.request()
				.delete();

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	private final String DEFAULT_ID_PATH_STUDENT_CLUB_NEW = "/2";	
	private final String DEFAULT_STUDENT_CLUB_NAME = "Mountain Hiking Club";
	private final String DEFAULT_ID_PATH_STUDENT_CLUB_NO_RECORD = "/20";
	
	// These are the values used for the newStudentClub Entity:
	private static final String NEW_ACADEMIC_CLUB_NAME = "New Student Club";
	private static final String NEW_NON_ACADEMIC_CLUB_NAME = "New Non Academic Student Club";
}