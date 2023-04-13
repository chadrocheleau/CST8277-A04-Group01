/**
 * File:  ProfessorResource.java Course materials (23W) CST 8277
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


import acmecollege.ejb.ProfessorService;
import acmecollege.entity.Professor;
import acmecollege.utility.ResponseCodes;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

/**
 * This class provides all the resources available to the REST API for 
 * the Professor Entity.
 * @author paisl
 *
 */
@Path(PROFESSOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {

	private static final Logger LOG = LogManager.getLogger();

	/**
     * The EJB service that supports this Student Resource
     */
	@EJB
	protected ProfessorService service;

	/**
     * The SecurityContext used by this class to authenticate users.
     */
	@Inject
	protected SecurityContext sc;

	 /**
     * Resource for getting all professors
     * @return response containing the list of all Professors in the database
     */
	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getProfessors() {
		LOG.debug("retrieving all professors...");
		List<Professor> professors = service.getAll(Professor.class, "Professor.findAll");
		return ResponseCodes.getAllResponse(professors);
	}

	/**
     * Resource for getting a Professor by a provided ID
     * @param id The Id of the Professor for which to search
     * @return response containing The Professor with the provided ID or null if not found.
     */
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific professor " + id);
		Professor professor = service.getById(Professor.class, Professor.QUERY_PROFESSOR_BY_ID, id);
		return ResponseCodes.getOrDeleteResponse(professor);
	}
	
	/**
     * Resource for adding a new Professor. 
     * @param newProfessor The new Professor to add to the database
     * @return response with the Professor added 
     * TODO add logic for returning null if operation not performed.
     */
	@POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
        Response response = null;
        service.persistEntity(newProfessor);
        response = Response.ok(newProfessor).build();
        return response;
    }
	
	/**
     * Resource for updating a Professor
     * @param id the id of the Professor to update with the provided information
     * @param updateProfessor the information with which to update the existing Professor
     * @return response with the updated Professor after updated or null if Professor not
     * found for update.
     */
	@PUT
	@RolesAllowed({ADMIN_ROLE})
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Professor updateProfessor) {
		Professor updatedProf = null;
		Response response = null;
		updatedProf = service.updateProfessorById(updateProfessor,  id);
		response = Response.status(updatedProf == null ? Status.NOT_FOUND : Status.OK).entity(updatedProf).build();
		return response;
	}
	
	/**
	 * Resource for deleting a Professor by Id
	 * @param id The id of the Professor to delete
	 * @return The deleted Professor or null if operation not performed.
	 */
	@DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	Professor deletedProf = service.deleteProfessor(id);
    	return ResponseCodes.getOrDeleteResponse(deletedProf);
    }
}
