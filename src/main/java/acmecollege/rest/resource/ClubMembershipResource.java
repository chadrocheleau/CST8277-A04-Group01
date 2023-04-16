/**
 * File:  ClubMembershipResource.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import java.util.List;
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CLUBMEMBERSHIP_CLUB_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_STUDENT_CLUB_ID;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import acmecollege.ejb.ClubMembershipService;
import acmecollege.entity.ClubMembership;
import acmecollege.utility.ResponseCodes;

/**
 * This class provides all the resources available to the REST API for 
 * the ClubMembershipResource.
 * @author paisl
 *
 */
@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource  {

	private static final Logger LOG = LogManager.getLogger();

	/**
     * The EJB service that supports this Student Resource
     */
    @EJB
    protected ClubMembershipService service;

    /**
     * The SecurityContext used by this class to authenticate users.
     */
    @Inject
    protected SecurityContext sc;
    
    /**
     * Resource for getting all ClubMemberships
     * @return response containing the list of all ClubMemberships
     * TODO add logic to return null if no ClubMemberships are found.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getClubMemberships() {
        LOG.debug("retrieving all Club Memberships ...");
        List<ClubMembership> memberships = service.getAll(ClubMembership.class, ClubMembership.FIND_ALL);
        return ResponseCodes.getAllResponse(memberships);
    }
    
    /**
     * Resource for getting a ClubMembership by Id
     * @return response containing the ClubMembership in the database or null
     * if no Courses found
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{clubMembershipId}")
    public Response getClubMembershipById(@PathParam("clubMembershipId") int clubMembershipId) {
        LOG.debug("Retrieving club membership with id = {}", clubMembershipId);
        ClubMembership clubMembership = service.getById(ClubMembership.class, ClubMembership.FIND_BY_ID, clubMembershipId);
        return ResponseCodes.getOrDeleteResponse(clubMembership);
    }
    
    /**
     * Resource for adding a new ClubMembership. A StudentClub is needed in order to create 
     * a new ClubMembership
     * @param scId The id of the StudentClub for which this ClubMembership will be created.
     * @param newClubMembership
     * @return response containing the ClubMembership that was created
     * TODO add logic for returning null if operation is not performed.
     */
    @RolesAllowed({ADMIN_ROLE})
    @POST
    @Path(CLUBMEMBERSHIP_CLUB_ID_PATH)
	 public Response addClubMembership(@PathParam(RESOURCE_PATH_STUDENT_CLUB_ID) int scId, ClubMembership newClubMembership) {
        LOG.debug("Adding a new club membership= {}", newClubMembership);
        ClubMembership tempCM = service.persistClubMembership(newClubMembership, scId);
        return Response.ok(tempCM).build();
    }
    
    /**
     * Resource for deleting a ClubMembership by Id
     * @param clubMembershipId The id of the ClubMembership to be deleted
     * @return response containing the ClubMembership deleted 
     * TODO add logic to return null if operation not performed.
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{clubMembershipId}")
    public Response deleteClubMembership(@PathParam("clubMembershipId") int clubMembershipId) {
        LOG.debug("Deleting club membership with id = {}", clubMembershipId);
        ClubMembership deletedMembership = service.deleteClubMembership(clubMembershipId);
        return ResponseCodes.getOrDeleteResponse(deletedMembership);
    }
}
