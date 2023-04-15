package acmecollege;

import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

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

import acmecollege.entity.Professor;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeProfessor extends TestACMECollegeSystem {
	private static Professor newProfessor;
	private static Professor updateProfessor;
	private static int newProfessorId;

	/**
	 * Initializes two Professors to be used as entities for create and update
	 * operations
	 * 
	 * @throws Exception
	 */
	@BeforeAll
	public static void initTestEntities() throws Exception {
		newProfessor = new Professor();
		updateProfessor = new Professor();

		newProfessor.setProfessor(NEW_FIRST_NAME, NEW_LAST_NAME, NEW_DEPARTMENT);
		;
		updateProfessor.setProfessor(UPDATE_FIRST_NAME, UPDATE_LAST_NAME, UPDATE_DEPARTMENT);
	}

	/**
	 * This test tests GET /Professor end point when accessed by user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(1)
	public void get_all_professors_with_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PROFESSOR_SUBRESOURCE_NAME).request().get();

		List<Professor> professors = response.readEntity(new GenericType<List<Professor>>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(professors, is(not(empty())));
	}

	/**
	 * This test tests GET /Professor end point when accessed by user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(2)
	public void get_all_professors_with_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(userAuth).path(PROFESSOR_SUBRESOURCE_NAME).request().get();

		assertThat(response.getStatus(), is(FORBIDDEN));
	}

	/**
	 * This test tests GET /Professor/{id} end point when accessed by user in
	 * ADMIN_ROLE when searching for a Professor that exists. The request gets the
	 * first Record provided as sample data and so expects the values of this
	 * default first record
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(3)
	public void get_professor_by_id_as_adminrole_with_results() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(adminAuth)
				.path(PROFESSOR_SUBRESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD).request().get();

		Professor returnedProfessor = response.readEntity(new GenericType<Professor>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedProfessor.getFirstName(), is(DEFAULT_FIRST_NAME));
		assertThat(returnedProfessor.getLastName(), is(DEFAULT_LAST_NAME));
		assertThat(returnedProfessor.getDepartment(), is(DEFAULT_DEPARTMENT));
	}

	/**
	 * This test tests GET /Professor/{id} end point when accessed by user in
	 * ADMIN_ROLE when searching for a Professor that does not exist
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(4)
	public void get_professor_by_id_as_adminrole_no_results() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(adminAuth).path(PROFESSOR_SUBRESOURCE_NAME + DEFAULT_ID_PATH_NO_RECORD)
				.request().get();

		assertThat(response.getStatus(), is(NOT_FOUND));

	}

	/**
	 * This test tests GET /Professor/{id} end point when accessed by user in
	 * USER_ROLE
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(5)
	public void get_professor_by_id_as_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(userAuth).path(PROFESSOR_SUBRESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD)
				.request().get();

		Professor returnedProfessor = response.readEntity(new GenericType<Professor>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedProfessor.getFirstName(), is(DEFAULT_FIRST_NAME));
		assertThat(returnedProfessor.getLastName(), is(DEFAULT_LAST_NAME));
		assertThat(returnedProfessor.getDepartment(), is(DEFAULT_DEPARTMENT));
	}

	/**
	 * This test tests POST /Professor end point when accessed by user in
	 * ADMIN_ROLE. This test will produce a second Professor Record
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(7)
	public void post_new_professor_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(adminAuth).path(PROFESSOR_SUBRESOURCE_NAME).request()
				.post(Entity.entity(newProfessor, MediaType.APPLICATION_JSON));

		Professor returnedProfessor = response.readEntity(new GenericType<Professor>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedProfessor.getFirstName(), is(NEW_FIRST_NAME));
		assertThat(returnedProfessor.getLastName(), is(NEW_LAST_NAME));

		newProfessorId = returnedProfessor.getId();
	}

	/**
	 * This test tests POST /Professor end point when accessed by user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(8)
	public void post_new_professor_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(userAuth).path(PROFESSOR_SUBRESOURCE_NAME).request()
				.post(Entity.entity(newProfessor, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(FORBIDDEN));
	}

	/**
	 * This test tests the PUT /Professor/{id} Resource when using ADMIN_ROLE to
	 * update a Professor that does exist.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(9)
	public void put_update_professor_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(adminAuth).path(PROFESSOR_SUBRESOURCE_NAME + "/" + newProfessorId)
				.request().put(Entity.entity(updateProfessor, MediaType.APPLICATION_JSON));

		Professor returnedProfessor = response.readEntity(new GenericType<Professor>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedProfessor.getFirstName(), is(UPDATE_FIRST_NAME));
		assertThat(returnedProfessor.getLastName(), is(UPDATE_LAST_NAME));
	}

	/**
	 * This test tests the PUT /Professor/{id} Resource when using USER_ROLE to
	 * update a Professor.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(10)
	public void put_update_professor_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(userAuth).path(PROFESSOR_SUBRESOURCE_NAME + "/" + newProfessorId)
				.request().put(Entity.entity(updateProfessor, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(FORBIDDEN));

	}

	/**
	 * This test tests the DELETE /Professor/{id} Resource when using ADMIN_ROLE to
	 * delete a Professor.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(11)
	public void delete_professor_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(adminAuth).path(PROFESSOR_SUBRESOURCE_NAME + "/" + newProfessorId)
				.request().delete();

		Professor deletedProfessor = response.readEntity(Professor.class);

		assertThat(response.getStatus(), is(OK));
		// deleting the record that was created and updated
		// therefore the response should match the updated values of that record
		assertThat(deletedProfessor.getFirstName(), is(UPDATE_FIRST_NAME));
		assertThat(deletedProfessor.getLastName(), is(UPDATE_LAST_NAME));

	}

	/**
	 * This test tests the DELETE /Professor/{id} Resource when using USER_ROLE to
	 * delete a Professor.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(12)
	public void delete_Professor_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(userAuth).path(PROFESSOR_SUBRESOURCE_NAME + "/" + newProfessorId)
				.request().delete();

		assertThat(response.getStatus(), is(FORBIDDEN));

	}

	// *********************** CONSTANTS ******************************
	/**
	 * Sample data provides first Professor Record with this First Name
	 */
	private static final String DEFAULT_FIRST_NAME = "Teddy";

	/**
	 * Sample data provides first Professor Record with this First Name
	 */
	private static final String DEFAULT_LAST_NAME = "Yap";

	/**
	 * Sample data provides first Professor Record with this Department
	 */
	private static final String DEFAULT_DEPARTMENT = "Information and Communications Technology";

	/**
	 * Used For Creating New Professor
	 */
	private static final String NEW_FIRST_NAME = "Lucas";

	/**
	 * Used For Creating New Professor
	 */
	private static final String NEW_LAST_NAME = "Ross";

	/**
	 * Used for Creating New Professor
	 */
	private static final String NEW_DEPARTMENT = "Geography Department";

	/**
	 * Used for Update Professor
	 */
	private static String UPDATE_FIRST_NAME = "Thomas";

	/**
	 * Used for Update Professor
	 */
	private static final String UPDATE_LAST_NAME = "Anderson";

	/**
	 * Used for Update Professor
	 */
	private static final String UPDATE_DEPARTMENT = "Math Department";

	/**
	 * Refers to id of first record provided by sample data
	 */
	private final String DEFAULT_ID_PATH_FIRST_RECORD = "/1";

	/**
	 * Refers to id of the new record that these tests will create
	 */
	private final String DEFAULT_ID_PATH_NEW_RECORD = "/2";

	/**
	 * Refers to id of a surely non existent Entity when needing to get no results
	 */
	private final String DEFAULT_ID_PATH_NO_RECORD = "/20";
}
