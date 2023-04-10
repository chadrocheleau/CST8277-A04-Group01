/**
 * File:  CourseRegistrationResource.java Course materials (23W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 01
 *   041026625 Chad Rocheleau (as from ACSIS)
 *   41020857 Lucas Ross (as from ACSIS)
 *   041028658 Jacob Scott (as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_STUDENT_PROFESSOR_REG_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_STUDENT_ID;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_LIST_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.COURSE_REG_COURSE_STUDENT_ID_PATH;
import static acmecollege.utility.MyConstants.COURSE_STUDENT_LIST_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_COURSE_ID;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.CourseRegistrationService;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(COURSE_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseRegistrationResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected CourseRegistrationService service;

	@Inject
	protected SecurityContext sc;

	/**
	 * Simply gets a list of all course registrations. only ADMIN_ROLE user can do
	 * this.
	 * 
	 * @return
	 */
	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getCourseRegistrations() {
		LOG.debug("retrieving all course registrations ...");
		List<CourseRegistration> registrations = service.getAll(CourseRegistration.class, "CourseRegistration.findAll");
		Response response = Response.ok(registrations).build();
		return response;
	}

	/**
	 * Resource for adding new Course Registration
	 * 
	 * @param courseId        The course Id passed as a parameter from POST request
	 * @param studentId       The student Id passed as a parameter from POST request
	 * @param profId          The professor Id passed as a parameter from POST
	 *                        request
	 * @param newRegistration Course Registration details to be used when creating
	 *                        new course registration
	 * @return the response
	 */
	@POST
	@RolesAllowed({ ADMIN_ROLE })
	@Path(COURSE_STUDENT_PROFESSOR_REG_RESOURCE_PATH)
	public Response addCourseRegistration(@PathParam("courseId") int courseId, @PathParam("studentId") int studentId,
			@PathParam("professorId") int profId, CourseRegistration newRegistration) {
		Response response = null;
		Student regStudent = service.getById(Student.class, Student.QUERY_STUDENT_BY_ID, studentId);
		Course regCourse = service.getById(Course.class, Course.COURSE_BY_ID_QUERY, courseId);
		Professor regProf = service.getById(Professor.class, Professor.QUERY_PROFESSOR_BY_ID, profId);

		if (regStudent == null || regCourse == null) {
			response = Response.status(Status.NOT_MODIFIED).build();
		} else {
			CourseRegistration persistedReg = service.persistRegistration(newRegistration, regStudent, regCourse,
					regProf);
			response = Response.ok(persistedReg).build();
		}

		return response;
	}

	/**
	 * Used to delete a CourseRegistration with primary key composed of both
	 * parameter values. Uses CourseRegistrationService method deletRegistrationById
	 * 
	 * @param studentId The id of the student belonging to the course registration
	 * @param courseId  the id of the course belonging to the course registration
	 * @return The course registration deleted.
	 */
	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(COURSE_REG_COURSE_STUDENT_ID_PATH)
	public Response deleteCourseRegistrationById(@PathParam("studentId") int studentId,
			@PathParam("courseId") int courseId) {
		CourseRegistration deletedRegistration = null;
		CourseRegistrationPK id = new CourseRegistrationPK();
		id.setCourseId(courseId);
		id.setStudentId(studentId);

		Response response = null;
		deletedRegistration = service.deleteRegistrationById(id);
		response = Response.status(deletedRegistration == null ? Status.NOT_FOUND : Status.OK)
				.entity(deletedRegistration).build();
//    	response = Response.ok(deletedRegistration).build();
		return response;
	}

	/**
	 * Resource for seeing a list of courses for a student with provided ID. If user
	 * is a USER_ROLE then the id of the user must match the id of the student for
	 * which a course list is requested. Other wise if user is ADMIN_ROLE then they
	 * are able to look at any students course list.
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(STUDENT_COURSE_LIST_PATH)
	public Response getStudentCourseList(@PathParam(RESOURCE_PATH_STUDENT_ID) int id) {
		LOG.debug("Get course list for student with id: " + id);
		Response response = null;
		Student student = null;
		Set<CourseRegistration> registrations = new HashSet<>();
		Set<Course> courses = new HashSet<>();

		if (sc.isCallerInRole(ADMIN_ROLE)) {
			student = service.getById(Student.class, Student.QUERY_STUDENT_BY_ID, id);
			if (student != null) {
				registrations = student.getCourseRegistrations();
				registrations.forEach(registration -> {
					courses.add(registration.getCourse());
				});
			} else {
				response = Response.status(Status.NOT_FOUND).build();
			}
			response = Response.status(student == null ? Status.NOT_FOUND : Status.OK).entity(courses).build();
		} else if (sc.isCallerInRole(USER_ROLE)) {
			WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
			SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
			student = sUser.getStudent();
			if (student != null && student.getId() == id) {

				registrations = student.getCourseRegistrations();
				registrations.forEach(registration -> {
					courses.add(registration.getCourse());
				});
				response = Response.status(Status.OK).entity(courses).build();
			} else {
				throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
			}
		} else {
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}

	/**
	 * Resource for seeing a list of courses for a student with provided ID. If user
	 * is a USER_ROLE then the id of the user must match the id of the student for
	 * which a course list is requested. Other wise if user is ADMIN_ROLE then they
	 * are able to look at any students course list.
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@RolesAllowed({ ADMIN_ROLE })
	@Path(COURSE_STUDENT_LIST_PATH)
	public Response getCourseStudentList(@PathParam(RESOURCE_PATH_COURSE_ID) int id) {
		LOG.debug("Get course list for student with id: " + id);
		Response response = null;
		Course course = null;
		Set<CourseRegistration> registrations = new HashSet<>();
		Set<Student> students = new HashSet<>();

		course = service.getById(Course.class, Course.COURSE_BY_ID_QUERY, id);
		if (course != null) {
			registrations = course.getCourseRegistrations();
			registrations.forEach(registration -> {
				students.add(registration.getStudent());
			});
			response = Response.status(students == null ? Status.NOT_FOUND : Status.OK).entity(students).build();	
		} else {
			response = Response.status(Status.NOT_FOUND).build();
		}
		

		return response;
	}
}
