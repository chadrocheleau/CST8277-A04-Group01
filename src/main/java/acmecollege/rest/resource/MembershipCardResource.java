/**
 * File:  MembershipCard.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_STUDENT_PROFESSOR_REG_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CARD_STUDENT_LIST_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_STUDENT_ID;
import static acmecollege.utility.MyConstants.STUDENT_MEMBERSHIP_CARD_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_STUDENT_CLUB_ID;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_CLUB_MEMBERSHIP_ID;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_MEMBERSHIP_CARD_ID;
import static acmecollege.utility.MyConstants.CARD_MEMBERSHIP_ID_PATH;

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

import acmecollege.ejb.ClubMembershipService;
import acmecollege.ejb.MembershipCardService;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.utility.ResponseCodes;


/**
 * This class provides all the resources available to the REST API for 
 * the MembershipCard Entity.
 * @author paisl
 *
 */
@Path(MEMBERSHIP_CARD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {
	
	private static final Logger LOG = LogManager.getLogger();
	
	/**
     * The EJB service that supports this Student Resource
     */
    @EJB
    protected MembershipCardService service;

    @Inject
    protected SecurityContext sc;
    
	/**
     * Resource for getting all MembershipCards 
     * @return response containing the list of all MembershipCards in the database
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getMembershipCards() {
        LOG.debug("retrieving all Membership Cards ...");
        List<MembershipCard> cards = service.getAll(MembershipCard.class, MembershipCard.ALL_CARDS_QUERY_NAME);
        return ResponseCodes.getAllResponse(cards);
    }
    
    /**
     * Gets a list of MembershipCards belonging to a Student. If the logged in user is a Student
     * they are only able to get a list of MembershipCards belonging to themselves. This method 
     * authenticates Students to ensure that a logged in Student may not retrieve a list of MembershipCards
     * that do not belong to them.
     * @param studentId the id of the Student for which to retrieve a list of MembershipCards.
     * @return response containing the Set of MembershipCards belonging to Student with Id or null if operation
     * not performed.
     */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(CARD_STUDENT_LIST_PATH)
    public Response getMembershipCardsForStudent(@PathParam(RESOURCE_PATH_STUDENT_ID) int studentId) {
        LOG.debug("Retrieving membership cards for student with id = {}", studentId);
        Student student = new Student();
        Response response = null;
        Set<MembershipCard> cards = new HashSet<>();
        
        if (sc.isCallerInRole(ADMIN_ROLE)) {
            student = service.getById(Student.class, Student.QUERY_STUDENT_BY_ID, studentId);
            try{
            	cards = student.getMembershipCards();
                if(!cards.isEmpty()) {
                	response = Response.status(student == null ? Status.NOT_FOUND : Status.OK).entity(cards).build();
                } else {
                	response = Response.status(Status.NOT_FOUND).build();
                }
            } catch (Exception e) {
            	Response.status(Status.NOT_FOUND).build();
            }
            
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            student = sUser.getStudent();
            if (student != null && student.getId() == studentId) {
            	
            	cards = student.getMembershipCards();
                response = Response.status(Status.OK).entity(cards).build();
            } else {
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }
    
    /**
     * Resource for creating a new MembershipCard for a Student 
     * @param studentId The id of the Student for which a MembershipCard is created
     * @param clubMembershipId The id of the ClubMembership for which the MembershipCard is created
     * @return response containing the MembershipCard that has been created
     * TODO add logic to return null if a MembershipCard has not been created.
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    @Path(STUDENT_MEMBERSHIP_CARD_PATH)
    public Response addMembershipCard(@PathParam("studentId") int studentId ,
								      @PathParam(RESOURCE_PATH_STUDENT_CLUB_ID) int scId) {
    	Response response = null;
		MembershipCard persistedCard = service.persistMembershipCard(studentId, scId);
    	response = Response.ok(persistedCard).build();
        return response;
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(CARD_MEMBERSHIP_ID_PATH)
    public Response setClubMembership(@PathParam(RESOURCE_PATH_CLUB_MEMBERSHIP_ID) int clubMembershipId,
    		@PathParam(RESOURCE_PATH_MEMBERSHIP_CARD_ID) int membershipCardId) {
    	Response response = null;
    	MembershipCard updatedMembershipCard = service.setMembershipForMembershipCard(clubMembershipId, membershipCardId);
    	response = Response.status(updatedMembershipCard == null ? Status.NOT_MODIFIED : Status.OK).entity(updatedMembershipCard).build();
    	return response;
    }
    
    /**
     * Resource for deleting a MembershipCard by Id
     * @param id The id of the MembershipCard to be deleted.
     * @return response containing the MembershipCard deleted
     * TODO add logic for returning null if operation is not performed for any reason
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMembershipCardById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	MembershipCard deletedMembershipCard = service.deleteMembershipCardById(id);
    	return ResponseCodes.getOrDeleteResponse(deletedMembershipCard);
    }
}
