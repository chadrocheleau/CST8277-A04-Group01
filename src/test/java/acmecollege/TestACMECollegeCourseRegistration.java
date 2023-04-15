package acmecollege;

import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_STUDENT_PROFESSOR_REG_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeCourseRegistration extends TestACMECollegeSystem {
	private static CourseRegistration newCourseRegistration;
	private static CourseRegistration updateCourseRegistration;
	private static Course newCourse;
	private static Professor newProfessor;
	private static Student newStudent;
	private static CourseRegistrationPK newCourseRegistrationId;

	/**
	 * Initializes two Professors to be used as entities for create and update
	 * operations
	 * 
	 * @throws Exception
	 */
	@BeforeAll
	public static void initTestEntities() throws Exception {
		
		newCourseRegistration = new CourseRegistration();
		updateCourseRegistration = new CourseRegistration();
		newProfessor = new Professor();
		newCourse = new Course();
		newStudent = new Student();

		newProfessor.setProfessor(NEW_PROFESSOR_FIRST_NAME, NEW_PROFESSOR_LAST_NAME, NEW_PROFESSOR_DEPARTMENT);
		newCourse.setCourse(NEW_COURSE_CODE, NEW_COURSE_TITLE, NEW_COURSE_YEAR, NEW_COURSE_SEMESTER, NEW_COURSE_UNITS, NEW_COURSE_ONLINE);
		newStudent.setFullName(NEW_STUDENT_FIRST_NAME, NEW_STUDENT_LAST_NAME);
		
		newCourseRegistration.setCourse(newCourse);
		newCourseRegistration.setProfessor(newProfessor);
		newCourseRegistration.setStudent(newStudent);
		
	}

	/**
	 * This test tests GET /Professor end point when accessed by user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(1)
	public void get_all_courseregistrations_with_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(COURSE_REGISTRATION_RESOURCE_NAME).request().get();

		List<CourseRegistration> courseRegistrations = response.readEntity(new GenericType<List<CourseRegistration>>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(courseRegistrations, is(not(empty())));
	}

	/**
	 * This test tests GET /Professor end point when accessed by user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(2)
	public void get_all_courseregistrations_with_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(userAuth).path(COURSE_REGISTRATION_RESOURCE_NAME).request().get();

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	
	@Test
	@Order(3)
	public void post_new_courseregistration_adminrole() throws JsonMappingException, JsonProcessingException {
		Response responseProfessor = webTarget
		.register(adminAuth)
		.path(PROFESSOR_SUBRESOURCE_NAME)
		.request()
		.post(Entity.entity(newProfessor, MediaType.APPLICATION_JSON));
		Professor returnedProfessor = responseProfessor.readEntity(new GenericType<Professor>() {
		});
		Response responseStudent = webTarget
		.register(adminAuth)
		.path(STUDENT_RESOURCE_NAME)
		.request()
		.post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
		Student returnedStudent = responseStudent.readEntity(new GenericType<Student>() {
		});
		Response responseCourse = webTarget
		.register(adminAuth)
		.path(COURSE_RESOURCE_NAME)
		.request()
		.post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
		Course returnedCourse = responseCourse.readEntity(new GenericType<Course>() {
		});
		Response response = webTarget
				.register(adminAuth)
				.path("/courseregistration/course/"+returnedCourse.getId()+
						"/student/"+returnedStudent.getId()+
						"/professor/"+returnedProfessor.getId())
				.request()
				.post(Entity.entity(newCourseRegistration, MediaType.APPLICATION_JSON));

		CourseRegistration returnedCourseRegistration = response.readEntity(new GenericType<CourseRegistration>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedCourseRegistration.getProfessor(), is(returnedProfessor));
		assertThat(returnedCourseRegistration.getStudent(), is(returnedStudent));
		assertThat(returnedCourseRegistration.getCourse(), is(returnedCourse));

		newCourseRegistrationId = returnedCourseRegistration.getId();
	}

	/**
	 * This test tests POST /Professor end point when accessed by user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(4)
	public void post_new_courseregistration_userrole() throws JsonMappingException, JsonProcessingException {

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
	 * Sample data provides first Student Record with this First Name
	 */
	private static final String DEFAULT_STUDENT_FIRST_NAME = "John";
	
	/**
	 * Sample data provides first Student Record with this First Name
	 */
	private static final String DEFAULT_STUDENT_LAST_NAME = "Smith";
	
	/**
	 * Used For Creating New Student
	 */
	private static final String NEW_STUDENT_FIRST_NAME = "Chad";
	
	/**
	 * Used For Creating New Student 
	 */
	private static final String NEW_STUDENT_LAST_NAME = "Rocheleau";
	/**
	 * Sample data provides first Professor Record with this First Name
	 */
	private static final String DEFAULT_PROFESSOR_FIRST_NAME = "Teddy";

	/**
	 * Sample data provides first Professor Record with this First Name
	 */
	private static final String DEFAULT_PROFESSOR_LAST_NAME = "Yap";

	/**
	 * Sample data provides first Professor Record with this Department
	 */
	private static final String DEFAULT_PROFESSOR_DEPARTMENT = "Information and Communications Technology";

	/**
	 * Used For Creating New Professor
	 */
	private static final String NEW_PROFESSOR_FIRST_NAME = "Lucas";

	/**
	 * Used For Creating New Professor
	 */
	private static final String NEW_PROFESSOR_LAST_NAME = "Ross";

	/**
	 * Used for Creating New Professor
	 */
	private static final String NEW_PROFESSOR_DEPARTMENT = "Geography Department";
	
  	/**
  	 * These are the values of the default Course provided as record 1
  	 */
  	private static final String DEFAULT_COURSE_CODE = "CST8277";
  	private static final String DEFAULT_COURSE_TITLE = "Enterprise Application Programming";
  	private static final int DEFAULT_COURSE_YEAR = 2022;
  	private static final String DEFAULT_COURSE_SEMESTER = "AUTUMN";
  	private static final int DEFAULT_COURSE_UNITS = 3;
  	private static final byte DEFAULT_COURSE_ONLINE = (byte) 0;
  	
  	/**
  	 * These are the values of the newCourse Entity of Course being created
  	 */
  	private static final String NEW_COURSE_CODE = "CST6666";
  	private static final String NEW_COURSE_TITLE = "My New Course";
  	private static final int NEW_COURSE_YEAR = 2023;
  	private static final String NEW_COURSE_SEMESTER = "WINTER";
  	private static final int NEW_COURSE_UNITS = 2;
  	private static final byte NEW_COURSE_ONLINE = (byte) 0;

	/**
	 * Refers to id of first record provided by sample data
	 */
	private final String DEFAULT_ID_PATH_FIRST_RECORD = "/1";

	/**
	 * Refers to id of the new record that these tests will create
	 */
	private final String DEFAULT_ID_PATH_NEW_RECORD = "/3";

	/**
	 * Refers to id of a surely non existent Entity when needing to get no results
	 */
	private final String DEFAULT_ID_PATH_NO_RECORD = "/20";
	
    /**  
     * Refers to id of first record provided by sample data
  	 */
  	private final String DEFAULT_COURSE_ID_PATH_FIRST_RECORD = "/1";
  	
  	/**
  	 * Refers to id of the new record that these tests will create
  	 */
  	private final String DEFAULT_COURSE_ID_PATH_NEW_RECORD = "/3";
  	
	/**
	 * Refers to id of first record provided by sample data
	 */
	private final String DEFAULT_PROFESSOR_ID_PATH_FIRST_RECORD = "/1";

	/**
	 * Refers to id of the new record that these tests will create
	 */
	private final String DEFAULT_PROFESSOR_ID_PATH_NEW_RECORD = "/2";
	
	/**
	 * Refers to id of first record provided by sample data
	 */
	private final String DEFAULT_STUDENT_ID_PATH_FIRST_RECORD = "/1";
	
	/**
	 * Refers to id of the new record that these tests will create
	 */
	private final String DEFAULT_STUDENT_ID_PATH_NEW_RECORD = "/2";
}
