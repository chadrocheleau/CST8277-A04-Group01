/**
 * File:  ClubMembershipResource.java Course materials (23W) CST 8277
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

import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;

import java.util.List;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CLUBMEMBERSHIP_CLUB_ID_PATH;
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
import acmecollege.entity.MembershipCard;
import acmecollege.entity.StudentClub;

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource  {

	private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ClubMembershipService service;

    @Inject
    protected SecurityContext sc;
    
    /**
     * Simply gets a list of all Club Memberships. only ADMIN_ROLE user can do this.
     * @return
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getClubMemberships() {
        LOG.debug("retrieving all Club Memberships ...");
        List<ClubMembership> memberships = service.getAll(ClubMembership.class, ClubMembership.FIND_ALL);
        Response response = Response.ok(memberships).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{clubMembershipId}")
    public Response getClubMembershipById(@PathParam("clubMembershipId") int clubMembershipId) {
        LOG.debug("Retrieving club membership with id = {}", clubMembershipId);
        ClubMembership clubMembership = service.getClubMembershipById(clubMembershipId);
        Response response = Response.ok(clubMembership).build();
        return response;
    }
    
    
    @RolesAllowed({ADMIN_ROLE})
    @POST
    @Path(CLUBMEMBERSHIP_CLUB_ID_PATH)
	 public Response addClubMembership(@PathParam("scId") int scId, ClubMembership newClubMembership) {
        LOG.debug("Adding a new club membership= {}", newClubMembership);
        
        ClubMembership tempCM = service.persistClubMembership(newClubMembership, scId);
        return Response.ok(tempCM).build();
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{clubMembershipId}")
    public Response deleteClubMembership(@PathParam("clubMembershipId") int clubMembershipId) {
        LOG.debug("Deleting club membership with id = {}", clubMembershipId);
        
        
        ClubMembership deletedMembership = service.deleteClubMembership(clubMembershipId);
        Response response = Response.ok(deletedMembership).build();
        return response;
    }
}
