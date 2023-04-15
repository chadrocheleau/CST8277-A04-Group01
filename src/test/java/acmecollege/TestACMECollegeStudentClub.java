package acmecollege;

import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import acmecollege.entity.StudentClub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeStudentClub extends TestACMECollegeSystem {

		@Test 
		@Order(1)
		public void get_all_student_clubs_with_adminrole() throws JsonMappingException, JsonProcessingException {
			Response response = webTarget
				.register(adminAuth)
				.path(STUDENT_CLUB_RESOURCE_NAME)
				.request()
				.get();
			
			List<StudentClub> sClubs = response.readEntity(new GenericType<List<StudentClub>>(){});

			assertThat(response.getStatus(), is(OK));
			assertThat(sClubs, is(not(empty())));
		}
		
		
	}
