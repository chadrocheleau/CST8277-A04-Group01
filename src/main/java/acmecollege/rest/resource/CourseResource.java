/**
 * File:  CourseResource.java Course materials (23W) CST 8277
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
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import acmecollege.ejb.CourseService;
import acmecollege.entity.Course;
import acmecollege.utility.ResponseCodes;


/**
 * This class provides all the resources available to the REST API for 
 * the Course Entity.
 * @author paisl
 *
 */
@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

	private static final Logger LOG = LogManager.getLogger();

	/**
     * The EJB service that supports this Student Resource
     */
	@EJB
	protected CourseService service;

	/**
     * The SecurityContext used by this class to authenticate users.
     */
	@Inject
	protected SecurityContext sc;
	
	/**
     * Resource for getting all Courses
     * @return response containing the list of all Courses in the database
     */
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	public Response getCourses() {
		LOG.debug("retrieving all courses...");
		List<Course> courses = service.getAll(Course.class, Course.ALL_COURSES_QUERY);
		return ResponseCodes.getAllResponse(courses);
	}
	
	 /**
     * Resource for getting a Course by Id
     * @param id the id of the Course to be retrieved
     * @return response containing the Course in the database or null
     * if no Courses found
     */
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific course " + id);
		Course course = service.getById(Course.class, Course.COURSE_BY_ID_QUERY, id);
		return ResponseCodes.getOrDeleteResponse(course);
	}
	
	/**
     * Resource for adding a new Course. 
     * @param newCourse The new Course to add to the database
     * @return response with the Course added
     * TODO add logic for returning null if operation not performed.
     */
	@POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
        Response response = null;
        service.persistEntity(newCourse);
        response = Response.ok(newCourse).build();
        return response;
    }
	
	/**
     * Resource for updating a Course
     * @param id the id of the Course to update with the provided information
     * @param updateCourse the information with which to update the existing Course
     * @return response with the updated Course after updated or null if Course not
     * found for update.
     */
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course updateCourse) {
		Course updatedCourse = service.updateCourseById(updateCourse,  id);
//		response = Response.status(updatedCourse == null ? Status.NOT_FOUND : Status.OK).entity(updatedCourse).build();
		return ResponseCodes.getOrDeleteResponse(updatedCourse);
	}
	
	/**
     * Resource for deleting a Course by Id
     * @param id The id of the Course to delete
     * @return The deleted Course or null if Course not found or operation not performed.
     */
	@DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	Course deletedCourse = service.deleteById(Course.class, Course.COURSE_BY_ID_QUERY, id);
    	return ResponseCodes.getOrDeleteResponse(deletedCourse);
    }
}
