package acmecollege;

import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CARD_STUDENT_LIST_ID_PATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import acmecollege.entity.MembershipCard;
import acmecollege.entity.StudentClub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


public class TestACMECollegeMembershipCard extends TestACMECollegeSystem {

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
	
//	@Test
//	@Order(5)
//	public void getMembershipCards_ByStudentId_AsUserRole() throws JsonMappingException, JsonProcessingException {
//
//		Response response = webTarget
//				.register(userAuth)
//				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + CARD_STUDENT_LIST_ID_PATH + DEFAULT_ID_PATH_MEMBERSHIP_CARD_NEW)
//				.request()
//				.get();
//
//		assertThat(response.getStatus(), is(FORBIDDEN));	
//	}

	private final String DEFAULT_ID_PATH_MEMBERSHIP_CARD_NEW = "/1";
	private final String DEFAULT_ID_PATH_MEMBERSHIP_CARD_NO_RECORD = "/2";
	
}
