/**
 * File:  ProfessorResource.java Course materials (23W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group NN
 *   041026625, Chad, Rocheleau (as from ACSIS)
 *   41020857, Lucas, Ross (as from ACSIS)
 *   studentId, Jacob, Scott (as from ACSIS)
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
import javax.ws.rs.PATCH;
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


import acmecollege.ejb.ACMECollegeService;
import acmecollege.ejb.ProfessorService;
import acmecollege.entity.Professor;


import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

@Path(PROFESSOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ProfessorService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getProfessors() {
		LOG.debug("retrieving all professors...");
		List<Professor> professors = service.getAll(Professor.class, "Professor.findAll");
		Response response = Response.ok(professors).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific professor " + id);
		Response response = null;
		Professor professor = null;

		professor = service.getById(Professor.class, Professor.QUERY_PROFESSOR_BY_ID, id);
		response = Response.status(professor == null ? Status.NOT_FOUND : Status.OK).entity(professor).build();

		return response;
	}
	
	@POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
        Response response = null;
        service.persistEntity(newProfessor);
        response = Response.ok(newProfessor).build();
        return response;
    }
	
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
	
	@DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	
    	Response response = null;
    	Professor deletedProf = null;
    	deletedProf = service.deleteById(Professor.class, Professor.QUERY_PROFESSOR_BY_ID, id);
    	response = Response.status(deletedProf == null ? Status.NOT_FOUND : Status.OK).entity(deletedProf).build();
    	return response;
    }
}
