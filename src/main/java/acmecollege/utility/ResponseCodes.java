/**
 * File:  ResponseCodes.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */
package acmecollege.utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import acmecollege.rest.resource.HttpErrorResponse;
/**
 * This class contains utility methods to streamline the process of checking responses
 * for errors/null values and returning user-friendly error messages.
 * @author decen
 *
 */
public class ResponseCodes {
	/**
	 * Checks response for get or delete by ID requests.
	 * error code if is null, otherwise builds a JSON response
	 * containing the entity.
	 * 
	 * @param entity to be checked for null value and built in reponse object
	 * @return Response object containing error code or entity in JSON format
	 */
	public static <T> Response getOrDeleteResponse(T entity) {
		if(entity == null) {
			return Response.status(Status.NOT_FOUND).entity(new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "Entity not found.")).build();
		} else {
			return Response.ok(entity).build();
		}
	}
	
	/**
	 * Checks response for getAll requests. Error code if list is empty,
	 * else build list into response object in JSON format.
	 * @param entityList List of entities to be processed
	 * @return Response object with either error code or JSON formatted list of entities
	 */
	public static <T> Response getAllResponse(List<T> entityList) {
		if(entityList.isEmpty()) {
			return Response.status(Status.NOT_FOUND).entity(new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No entities found.")).build();
		} else {
			return Response.ok(entityList).build();
		}
	}
	
	/**
	 * Checks response for getAll requests. Error code if set is empty,
	 * else build set into response object in JSON format.
	 * @param entityList Set of entities to be processed
	 * @return Response object with either error code or JSON formatted set of entities.
	 */
	public static <T> Response getAllResponse(Set<T> entityList) {
		if(entityList.isEmpty()) {
			return Response.status(Status.NOT_FOUND).entity(new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No entities found.")).build();
		} else {
			return Response.ok(entityList).build();
		}
	}
	
	
}
