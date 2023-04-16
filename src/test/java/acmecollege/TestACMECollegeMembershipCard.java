package acmecollege;

import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CARD_STUDENT_LIST_ID_PATH;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;


public class TestACMECollegeMembershipCard extends TestACMECollegeSystem {

	private static Student newStudent;
//	private static AcademicStudentClub newAcademicStudentClub;
//	private static ClubMembership newClubMembership;
//	private static MembershipCard newMembershipCard;
	
	@BeforeAll
	public static void initTestEntities() throws Exception {

		newStudent = new Student();
		newStudent.setFullName(DEFAULT_STUDENT_FIRST_NAME, DEFAULT_STUDENT_LAST_NAME);

//		newAcademicStudentClub = new AcademicStudentClub();
//		newAcademicStudentClub.setName(NEW_ACADEMIC_CLUB_NAME);
	}
	
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
	
	@Test
	@Order(3)
	public void getMembershipCards_ByStudentId_AsAdminRole_WithResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + DEFAULT_ID_PATH_MEMBERSHIP_CARD_NEW)
				.request()
				.get();

		List<MembershipCard> returnedMembershipCards = response.readEntity(new GenericType<List<MembershipCard>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedMembershipCards, is(not(empty())));	
	}
	
	@Test
	@Order(4)
	public void getMembershipCards_ByStudentId_AsAdminRole_NoResults() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + DEFAULT_ID_PATH_MEMBERSHIP_CARD_NO_RECORD)
				.request()
				.get();

		assertThat(response.getStatus(), is(NOT_FOUND));
	}
	
	@Test
	@Order(5)
	public void getMembershipCards_ByStudentId_AsUserRole_ForMatchingStudent() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + DEFAULT_ID_PATH_MEMBERSHIP_CARD_NEW)
				.request()
				.get();

		List<MembershipCard> returnedMembershipCards = response.readEntity(new GenericType<List<MembershipCard>>(){});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedMembershipCards, is(not(empty())));	
	}
	
	@Test
	@Order(6)
	public void getMembershipCards_ByStudentId_AsUserRole_ForNonMatchingStudent() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + DEFAULT_ID_PATH_MEMBERSHIP_CARD_NO_RECORD)
				.request()
				.get();

		assertThat(response.getStatus(), is(FORBIDDEN));	
	}
	
	@Test
	@Order(7)
	public void postNewMembershipCard_AdminRole() throws JsonMappingException, JsonProcessingException {

		Response responseStudent = webTarget
				.register(adminAuth)
				.path(STUDENT_RESOURCE_NAME)
				.request()
				.post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
		
		Student returnedStudent = responseStudent.readEntity(new GenericType<Student>() {});

		Response response = webTarget
				.register(adminAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME + DEFAULT_ID_PATH_STUDENT_CLUB_NEW)
				.request()
				.get();

		StudentClub returnedStudentClub = response.readEntity(new GenericType<StudentClub>(){});
		
//		Response responseStudentClub = webTarget
//				.register(adminAuth)
//				.path(STUDENT_CLUB_RESOURCE_NAME)
//				.request()
//				.post(Entity.entity(newAcademicStudentClub, MediaType.APPLICATION_JSON));
//		
//		AcademicStudentClub returnedStudentClub = responseStudentClub.readEntity(new GenericType<AcademicStudentClub>() {});
//
//		newClubMembership = new ClubMembership();
//		newClubMembership.setStudentClub(returnedStudentClub);
//		
//		Response responseClubMembership = webTarget
//				.register(adminAuth)
//				.path(CLUB_MEMBERSHIP_RESOURCE_NAME)
//				.request()
//				.post(Entity.entity(newClubMembership, MediaType.APPLICATION_JSON));
//		
//		ClubMembership returnedClubMembership = responseClubMembership.readEntity(new GenericType<ClubMembership>() {});
//
//		newMembershipCard = new MembershipCard();
//		newMembershipCard.setClubMembership(returnedClubMembership);
//		newMembershipCard.setOwner(newStudent);
//		newMembershipCard.setSigned(true);
		
//		Response responseMembershipCard = webTarget
//				.register(adminAuth)
//				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + 
//						STUDENT_RESOURCE_NAME + "/" + returnedStudent.getId() + 
//						CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + returnedClubMembership.getId())
//				.request()
//				.post(Entity.entity(newMembershipCard, MediaType.APPLICATION_JSON));
//		
//		MembershipCard createdMembershipCard = responseMembershipCard.readEntity(new GenericType<MembershipCard>() {});
//
//		assertThat(responseMembershipCard.getStatus(), is(OK));
//		assertThat(createdMembershipCard.getOwner(), is(returnedStudent));
//		assertThat(createdMembershipCard.getMembership(), is(returnedClubMembership));
	}

	// *********************** CONSTANTS ******************************

	private static final String DEFAULT_STUDENT_FIRST_NAME = "John";
	private static final String DEFAULT_STUDENT_LAST_NAME = "Smith";

	private static final String NEW_ACADEMIC_CLUB_NAME = "New Student Club";
	private final String DEFAULT_ID_PATH_STUDENT_CLUB_NEW = "/2";
	
	private final String DEFAULT_ID_PATH_MEMBERSHIP_CARD_NEW = "/1";
	private final String DEFAULT_ID_PATH_MEMBERSHIP_CARD_NO_RECORD = "/2";
	
}
