/**
 * File:  StudentResource.java Course materials (23W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 01
 *   041026625, Chad, Rocheleau (as from ACSIS)
 *   041020857, Lucas, Ross (as from ACSIS)
 *   041028658, Jacob, Scott (as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_PROFESSOR_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;
import java.util.List;
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
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.utility.ResponseCodes;

/**
 * This class provides all the resources available to the REST API for 
 * the Student Entity.
 * @author paisl
 *
 */
@Path(STUDENT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * The EJB service that supports this Student Resource
     */
    @EJB
    protected StudentService service;

    /**
     * The SecurityContext used by this class to authenticate users.
     */
    @Inject
    protected SecurityContext sc;

    /**
     * Resource for getting all students
     * @return response containing the list of all Students in the database
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getStudents() {
        LOG.debug("retrieving all students ...");
        List<Student> students = service.getAll(Student.class, Student.ALL_STUDENTS_QUERY_NAME);
        return ResponseCodes.getAllResponse(students);
    }

    /**
     * Resource for getting a Student by a provided ID. If the user is a Student with 
     * only USER_ROLE they can only get results for themselves.
     * @param id The Id of the Student for which to search
     * @return response containing The Student with the provided ID or null if not found.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getStudentById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific student " + id);
        Response response = null;
        Student student = null;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
            student = service.getById(Student.class, Student.QUERY_STUDENT_BY_ID, id);
            response = ResponseCodes.getOrDeleteResponse(student);
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            student = sUser.getStudent();
            if (student != null && student.getId() == id) {
                response = Response.ok(student).build();
            } else {
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }


    /**
     * Resource for adding a new Student. When adding a new Student a default 
     * SecurityUser is created and assigned to the Student with USER_ROLE.
     * @param newStudent The new Student to add to the database
     * @return response with the Student added
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addStudent(Student newStudent) {
        Response response = null;
        Student newStudentWithIdTimestamps = service.persistEntity(newStudent);
        // Build a SecurityUser linked to the new student
        service.buildUserForNewStudent(newStudentWithIdTimestamps);
        return ResponseCodes.getOrDeleteResponse(newStudentWithIdTimestamps);
    }
    
    /**
     * Resource for updating a Student
     * @param id the id of the Student to update with the provided information
     * @param updateStudent the information with which to update the existing Student
     * @return response with the updated Student after updated or null if Student not
     * found for update.
     */
    @PUT
   	@RolesAllowed({ADMIN_ROLE})
   	@Path(RESOURCE_PATH_ID_PATH)
   	public Response updateStudent(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Student updateStudent) {
   		Student updatedStudent = service.updateStudentById(id,  updateStudent);
   		return ResponseCodes.getOrDeleteResponse(updatedStudent);
   	}
       
    /**
     * Resource for Adding a Professor to a CourseRegistration for a Student and Course identified
     * by the passed parameters.
     * @param studentId The student Id of the CourseRegistration to add Professor to
     * @param courseId The course Id of the CourseResgistration to add Professor to
     * @param newProfessor The new Professor to add to the CourseRegistration
     * @return the Professor that was added to the CourseRegistration or null if Professor not added.
     */
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
       
       /**
        * Resource for deleting a Student by Id
        * @param id The id of the Student to delete
        * @return The deleted Student or null if Student not found or operation not performed.
        * TODO add logic for returning response indicating that operation was or was not performed.
        */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteStudentById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	Student deletedStudent = service.deleteStudentById( id);
    	return ResponseCodes.getOrDeleteResponse(deletedStudent);
    }
}