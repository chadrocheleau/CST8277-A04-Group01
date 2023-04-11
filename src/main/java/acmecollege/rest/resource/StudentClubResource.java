/**
 * File:  StudentClubResource.java Course materials (23W) CST 8277
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
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import javax.ws.rs.core.Response.Status;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import acmecollege.ejb.ACMECollegeService;
import acmecollege.ejb.StudentClubService;
import acmecollege.entity.StudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Student;

/**
 * This class provides all resources for operations related to StudentClubs
 * @author paisl
 *
 */
@Path("studentclub")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentClubResource {
    
    private static final Logger LOG = LogManager.getLogger();

    /**
     * The EJB service that supports this StudentClub Resource
     */
    @EJB
    protected StudentClubService service;

    /**
     * The SecurityContext used by this class to authenticate users.
     */
    @Inject
    protected SecurityContext sc;
    
    /**
     * Resource for getting all StudentClubs
     * @return response containing the list of all StudentClubs in the database
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getStudentClubs() {
        LOG.debug("Retrieving all student clubs...");
        List<StudentClub> studentClubs = service.getAll(StudentClub.class, StudentClub.ALL_STUDENT_CLUBS_QUERY_NAME);
        LOG.debug("Student clubs found = {}", studentClubs);
        Response response = Response.ok(studentClubs).build();
        return response;
    }
    
    /**
     * Resource for getting a StudentClub by a provided ID
     * @param id The Id of the StudentClub for which to search
     * @return response containing The Student with the provided ID or null if not found.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{studentClubId}")
    public Response getStudentClubById(@PathParam("studentClubId") int studentClubId) {
        LOG.debug("Retrieving student club with id = {}", studentClubId);
        StudentClub studentClub = service.getById(StudentClub.class, StudentClub.STUDENT_CLUB_QUERY_BY_ID , studentClubId);
        Response response = Response.ok(studentClub).build();
        return response;
    }

    /**
     * Resource for deleting a StudentClub by Id
     * @param id The id of the StudentClub to delete
     * @return The deleted StudentClub or null if Student not found or operation not performed.
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{studentClubId}")
    public Response deleteStudentClub(@PathParam("studentClubId") int scId) {
        LOG.debug("Deleting student club with id = {}", scId);
        StudentClub sc = service.deleteStudentClub(scId);
        Response response = Response.ok(sc).build();
        return response;
    }
    
    //TODO Please try to understand and test the below methods:
    
    /**
     * Resource for adding a new StudentClub. When adding a new StudentClub this resource
     * contains logic for verifying that a StudentClub does not already exist with the same
     * name.
     * @param newStudent The new StudentClub to add to the database
     * @return response with the StudentClub added or null if not performed
     */
    @RolesAllowed({ADMIN_ROLE})
    @POST
    public Response addStudentClub(StudentClub newStudentClub) {
        LOG.debug("Adding a new student club = {}", newStudentClub);
        if (service.isDuplicated(newStudentClub, StudentClub.IS_DUPLICATE_QUERY_NAME, newStudentClub.getName())) {
            HttpErrorResponse err = new HttpErrorResponse(Status.CONFLICT.getStatusCode(), "Entity already exists");
            return Response.status(Status.CONFLICT).entity(err).build();
        }
        else {
            StudentClub tempStudentClub = service.persistEntity(newStudentClub);
            return Response.ok(tempStudentClub).build();
        }
    }

    /**
     * Resource for updating StudentClub. 
     * @param scId The id of the StudentClub to update
     * @param updatingStudentClub The StudentClub information with which to update
     * the StudentClub
     * @return response containing the updated StudentClub or null if operation not performed.
     */
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @PUT
    @Path("/{studentClubId}")
    public Response updateStudentClub(@PathParam("studentClubId") int scId, StudentClub updatingStudentClub) {
        LOG.debug("Updating a specific student club with id = {}", scId);
        Response response = null;
        StudentClub updatedStudentClub = service.updateStudentClub(scId, updatingStudentClub);
        response = Response.ok(updatedStudentClub).build();
        return response;
    }
    
}
