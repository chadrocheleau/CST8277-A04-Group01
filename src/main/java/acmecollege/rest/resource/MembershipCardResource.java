/**
 * File:  MembershipCardResource.java Course materials (23W) CST 8277
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
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_STUDENT_PROFESSOR_REG_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CARD_STUDENT_LIST_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_STUDENT_ID;
import static acmecollege.utility.MyConstants.STUDENT_MEMBERSHIP_CARD_PATH;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
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

import acmecollege.ejb.ClubMembershipService;
import acmecollege.ejb.MembershipCardService;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(MEMBERSHIP_CARD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {
	
	private static final Logger LOG = LogManager.getLogger();
	
    @EJB
    protected MembershipCardService service;

    @Inject
    protected SecurityContext sc;
    
	/**
     * Simply gets a list of all Membership Cards. only ADMIN_ROLE user can do this.
     * @return
     */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getMembershipCards() {
        LOG.debug("retrieving all Membership Cards ...");
        List<MembershipCard> cards = service.getAll(MembershipCard.class, MembershipCard.ALL_CARDS_QUERY_NAME);
        Response response = Response.ok(cards).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(CARD_STUDENT_LIST_PATH)
    public Response getMembershipCardById(@PathParam(RESOURCE_PATH_STUDENT_ID) int studentId) {
        LOG.debug("Retrieving membership cards for student with id = {}", studentId);
        Student student = new Student();
        Response response = null;
        Set<MembershipCard> cards = new HashSet<>();
        //Student student = service.getById(Student.class, Student.QUERY_STUDENT_BY_ID, studentId);
        
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
    
    @POST
    @RolesAllowed({ADMIN_ROLE})
    @Path(STUDENT_MEMBERSHIP_CARD_PATH)
    public Response addCourseRegistration(@PathParam("studentId") int studentId ,
								    	  @PathParam("clubmembershipId") int clubMembershipId) {
    	Response response = null;
		
		MembershipCard persistedCard = service.persistMembershipCard(studentId, clubMembershipId);
    	response = Response.ok(persistedCard).build();
       
        return response;
    }
}
