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
import acmecollege.entity.Professor;



@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected CourseService service;

	@Inject
	protected SecurityContext sc;
	
	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getCourses() {
		LOG.debug("retrieving all courses...");
		List<Course> courses = service.getAll(Course.class, Course.ALL_COURSES_QUERY);
		Response response = Response.ok(courses).build();
		return response;
	}
	
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific course " + id);
		Response response = null;
		Course course = null;

		course = service.getById(Course.class, Course.COURSE_BY_ID_QUERY, id);
		response = Response.status(course == null ? Status.NOT_FOUND : Status.OK).entity(course).build();

		return response;
	}
	
	@POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
        Response response = null;
        service.persistEntity(newCourse);
        response = Response.ok(newCourse).build();
        return response;
    }
	
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course updateCourse) {
		Course updatedCourse = null;
		Response response = null;
		updatedCourse = service.updateCourseById(updateCourse,  id);
		response = Response.status(updatedCourse == null ? Status.NOT_FOUND : Status.OK).entity(updatedCourse).build();
		return response;
	}
	
	@DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	
    	Response response = null;
    	Course deletedCourse = null;
    	deletedCourse = service.deleteById(Course.class, Course.COURSE_BY_ID_QUERY, id);
    	response = Response.status(deletedCourse == null ? Status.NOT_FOUND : Status.OK).entity(deletedCourse).build();
    	return response;
    }
}
