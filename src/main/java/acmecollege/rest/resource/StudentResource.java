/**
 * File:  StudentResource.java Course materials (23W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 01
 *   041026625, Chad, Rocheleau (as from ACSIS)
 *   41020857, Lucas, Ross (as from ACSIS)
 *   041028658, Jacob, Scott (as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_PROFESSOR_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_STUDENT_ID;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_LIST_PATH;

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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

//import acmecollege.ejb.ACMECollegeService;
import acmecollege.ejb.StudentService;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(STUDENT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected StudentService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getStudents() {
        LOG.debug("retrieving all students ...");
        List<Student> students = service.getAll(Student.class, Student.ALL_STUDENTS_QUERY_NAME);
        Response response = Response.ok(students).build();
        return response;
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getStudentById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific student " + id);
        Response response = null;
        Student student = null;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
            student = service.getById(Student.class, Student.QUERY_STUDENT_BY_ID, id);
            response = Response.status(student == null ? Status.NOT_FOUND : Status.OK).entity(student).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            student = sUser.getStudent();
            if (student != null && student.getId() == id) {
                response = Response.status(Status.OK).entity(student).build();
            } else {
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }

   
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addStudent(Student newStudent) {
        Response response = null;
        Student newStudentWithIdTimestamps = service.persistEntity(newStudent);
        // Build a SecurityUser linked to the new student
        service.buildUserForNewStudent(newStudentWithIdTimestamps);
        response = Response.ok(newStudentWithIdTimestamps).build();
        return response;
    }
    
    @PUT
   	@RolesAllowed({ADMIN_ROLE})
   	@Path(RESOURCE_PATH_ID_PATH)
   	public Response updateStudent(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Student updateStudent) {
   		Student updatedStudent = null;
   		Response response = null;
   		updatedStudent = service.updateStudentById(id,  updateStudent);
   		response = Response.status(updatedStudent == null ? Status.NOT_FOUND : Status.OK).entity(updatedStudent).build();
   		return response;
   	}
       
       @PUT
       @RolesAllowed({ADMIN_ROLE})
       @Path(STUDENT_COURSE_PROFESSOR_RESOURCE_PATH)
       public Response updateProfessorForStudentCourse(@PathParam("studentId") int studentId,
										    		   @PathParam("courseId") int courseId,
										    		   Professor newProfessor) {
           Response response = null;
           Professor professor = service.setProfessorForStudentCourse(studentId, courseId, newProfessor);
           response = Response.ok(professor).build();
           return response;
       }
       
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteStudentById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	Student deletedStudent = null;
    	Response response = null;
    	deletedStudent = service.deleteStudentById( id);
    	response = Response.ok(deletedStudent).build();
    	return response;
    }
}