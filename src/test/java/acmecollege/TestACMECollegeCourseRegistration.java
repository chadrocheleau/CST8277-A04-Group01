/**
 * File:  TestACMECollegeCourseRegistration.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */

package acmecollege;

import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
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

import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;

/**
 * This test suite tests the ACME College API for the CourseRegistration Entity 
 * of the ACME College System. This test suite uses TestMethodOrder
 * to ensure that the tests are run in a particular order as some tests 
 * may change expected results of other tests depending on the order
 * in which they are run.
 * @author decen
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeCourseRegistration extends TestACMECollegeSystem {
	private static CourseRegistration newCourseRegistration;
	private static Course newCourse;
	private static Professor newProfessor;
	private static Student newStudent;
	private static CourseRegistrationPK newCourseRegistrationId;
	private static int newCourseId;

	/**
	 * Initializes professor, student, course, and course registration
	 * to be used in post and put operations
	 * 
	 * @throws Exception
	 */
	@BeforeAll
	public static void initTestEntities() throws Exception {
		
		newCourseRegistration = new CourseRegistration();
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
	 * This test tests GET /CourseRegistration end point when accessed by user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(1)
	public void get_all_courseregistrations_with_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth).path(COURSE_REGISTRATION_RESOURCE_NAME).request().get();

		List<CourseRegistration> courseRegistrations = response.readEntity(new GenericType<List<CourseRegistration>>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(courseRegistrations, is(not(empty())));
	}

	/**
	 * This test tests GET /CourseRegistration end point when accessed by user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(2)
	public void get_all_courseregistrations_with_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME)
				.request()
				.get();

		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	/**
	 * This test tests GET /CourseRegistration/course/list/student/{id} end point when accessed by 
	 * user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(3)
	public void get_courselist_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				.register(adminAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + COURSE_LIST_PATH + 
						DEFAULT_ID_PATH_FIRST_RECORD)
				.request()
				.get();
		
		List<Course> courses = response.readEntity(new GenericType<List<Course>>() {
		});
		
		assertThat(response.getStatus(), is(OK));
		assertThat(courses, is(not(empty())));
	}
	/**
	 * This test tests GET /CourseRegistration/course/list/student/{id} end point when accessed by 
	 * user in USER_ROLE using their own student ID.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(4)
	public void get_courselist_with_userrole_authenticated() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				.register(userAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + COURSE_LIST_PATH + 
						DEFAULT_ID_PATH_FIRST_RECORD)
				.request()
				.get();
		
		List<Course> courses = response.readEntity(new GenericType<List<Course>>() {
		});
		
		assertThat(response.getStatus(), is(OK));
		assertThat(courses, is(not(empty())));
	}
	/**
	 * This test tests GET /CourseRegistration/course/list/student/{id} end point when accessed by 
	 * user in USER_ROLE using someone else's student ID.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(5)
	public void get_courselist_with_userrole_not_authenticated() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				.register(userAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + COURSE_LIST_PATH + 
						"/2")
				.request()
				.get();
		
		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	/**
	 * This test tests GET /CourseRegistration/student/list/course/{id} end point when accessed by 
	 * user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(6)
	public void get_studentlist_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				.register(adminAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + STUDENT_LIST_PATH + 
						DEFAULT_ID_PATH_FIRST_RECORD)
				.request()
				.get();
		
		List<Student> students = response.readEntity(new GenericType<List<Student>>() {
		});
		
		assertThat(response.getStatus(), is(OK));
		assertThat(students, is(not(empty())));
	}
	/**
	 * This test tests GET /CourseRegistration/student/list/course/{id} end point when accessed by 
	 * user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(7)
	public void get_studentlist_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				.register(userAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + STUDENT_LIST_PATH + 
						DEFAULT_ID_PATH_FIRST_RECORD)
				.request()
				.get();
		
		assertThat(response.getStatus(), is(FORBIDDEN));
	}
	/**
	 * This test tests POST /CourseRegistration end point when accessed by 
	 * user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(8)
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

		newCourseId = returnedCourse.getId();
		newCourseRegistrationId = returnedCourseRegistration.getId();
	}

	/**
	 * This test tests POST /CourseRegistration end point when accessed by 
	 * user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(9)
	public void post_new_courseregistration_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget.register(userAuth).path(PROFESSOR_SUBRESOURCE_NAME).request()
				.post(Entity.entity(newCourseRegistration, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(FORBIDDEN));
	}

	/**
	 * This test tests PUT /CourseRegistration /course/{id}/professor/{id} end point when accessed by 
	 * user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(10)
	public void put_update_coursereg_professor_adminrole() throws JsonMappingException, JsonProcessingException {

		Response responseProfessor = webTarget
		.register(adminAuth)
		.path(PROFESSOR_SUBRESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD)
		.request()
		.get();
		Professor updateProfessor = responseProfessor.readEntity(new GenericType<Professor>() {
		});
		
		Response response = webTarget.register(adminAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + 
						"/course/" + newCourseId + 
						"/professor" + DEFAULT_ID_PATH_FIRST_RECORD)
				.request()
				.put(Entity.entity(updateProfessor, MediaType.APPLICATION_JSON));

		Professor returnedProfessor = response.readEntity(new GenericType<Professor>() {
		});

		assertThat(response.getStatus(), is(OK));
		assertThat(returnedProfessor.getFirstName(), is(DEFAULT_PROFESSOR_FIRST_NAME));
		assertThat(returnedProfessor.getLastName(), is(DEFAULT_PROFESSOR_LAST_NAME));
		assertThat(returnedProfessor.getDepartment(), is(DEFAULT_PROFESSOR_DEPARTMENT));
	}
	
	/**
	 * This test tests PUT /CourseRegistration /course/{id}/professor/{id} end point when accessed by 
	 * user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(11)
	public void put_update_coursereg_professor_userrole() throws JsonMappingException, JsonProcessingException {

		Response responseProfessor = webTarget
		.register(userAuth)
		.path(PROFESSOR_SUBRESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD)
		.request()
		.get();
		Professor updateProfessor = responseProfessor.readEntity(new GenericType<Professor>() {
		});
		
		Response response = webTarget.register(userAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + 
						"/course/" + newCourseId + 
						"/professor" + DEFAULT_ID_PATH_FIRST_RECORD)
				.request()
				.put(Entity.entity(updateProfessor, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(FORBIDDEN));
	}

	/**
	 * This test tests DELETE /CourseRegistration /{id} end point when accessed by 
	 * user in ADMIN_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(12)
	public void delete_courseregistration_adminrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(adminAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + "/" + 
				STUDENT_RESOURCE_NAME + "/" + newCourseRegistrationId.getStudentId() + "/" +
				COURSE_RESOURCE_NAME + "/" + newCourseRegistrationId.getCourseId())
				.request()
				.delete();

		CourseRegistration deletedCourseRegistration = response.readEntity(new GenericType<CourseRegistration>() {
		});

		assertThat(response.getStatus(), is(OK));
		// deleting the record that was created and updated
		// therefore the response should match the updated values of that record
		assertThat(deletedCourseRegistration.getProfessor().getFirstName(), is(DEFAULT_PROFESSOR_FIRST_NAME));
		assertThat(deletedCourseRegistration.getCourse().getCourseTitle(), is(newCourse.getCourseTitle()));
		assertThat(deletedCourseRegistration.getStudent().getFirstName(), is(newStudent.getFirstName()));
	}

	/**
	 * This test tests DELETE /CourseRegistration /{id} end point when accessed by 
	 * user in USER_ROLE.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	@Order(13)
	public void delete_Professor_userrole() throws JsonMappingException, JsonProcessingException {

		Response response = webTarget
				.register(userAuth)
				.path(COURSE_REGISTRATION_RESOURCE_NAME + "/" + 
				STUDENT_RESOURCE_NAME + "/" + newCourseRegistrationId.getStudentId() + "/" +
				COURSE_RESOURCE_NAME + "/" + newCourseRegistrationId.getCourseId())
				.request()
				.delete();

		assertThat(response.getStatus(), is(FORBIDDEN));

	}

	// *********************** CONSTANTS ******************************
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
	 * Path for course list by student ID path
	 */
	private final String COURSE_LIST_PATH = "/course/list/student";
	
	/**
	 * Path for student list by course ID path
	 */
	private final String STUDENT_LIST_PATH = "/student/list/course";
}
