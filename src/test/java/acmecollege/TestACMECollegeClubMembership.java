package acmecollege;

import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CARD_STUDENT_LIST_ID_PATH;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

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
import org.junit.platform.commons.annotation.Testable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.StudentClub;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeClubMembership extends TestACMECollegeSystem {

	private static ClubMembership newClubMembership;
	private static int postedClubMembershipID;
	
	@BeforeAll
	public static void initTestEntities() throws Exception {
		
		newClubMembership = new ClubMembership();

		DurationAndStatus ds = new DurationAndStatus();
		ds.setDurationAndStatus(LocalDateTime.now(), LocalDateTime.now().plusYears(1), "+");
		newClubMembership.setDurationAndStatus(ds);
		
	}
	
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
		assertThat(returnedClubMembership.getStudentClub().getName(), is(CLUB_2));	
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
		assertThat(returnedClubMembership.getStudentClub().getName(), is(CLUB_2));	
	}
	
	@Test
	@Order(5)
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

	@Test
	@Order(6)
	public void postNewClubMembership_AsUserRole() throws JsonMappingException, JsonProcessingException {

		
		Response response = webTarget
			.register(userAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + STUDENT_CLUB_RESOURCE_NAME + ID_PATH_1)
			.request()
			.post(Entity.entity(newClubMembership, MediaType.APPLICATION_JSON));
		
		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	@Test
	@Order(7)
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

	@Test
	@Order(8)
	public void deleteClubMembership_AsUserRole() throws JsonMappingException, JsonProcessingException {

		
		Response response = webTarget
			.register(userAuth)
			.path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + postedClubMembershipID)
			.request()
			.delete();
		
		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	

	// *********************** CONSTANTS ******************************

	private static final String ID_PATH_1 = "/1";
	private static final String ID_PATH_2 = "/2";
	private static final String CLUB_1 = "Computer Programming Club";
	private static final String CLUB_2 = "Mountain Hiking Club";
	
}
