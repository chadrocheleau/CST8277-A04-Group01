package acmecollege.utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import acmecollege.rest.resource.HttpErrorResponse;

public class ResponseCodes {
	public static <T> Response getOrDeleteResponse(T entity) {
		if(entity == null) {
			return Response.status(Status.NOT_FOUND).entity(new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "Entity not found.")).build();
		} else {
			return Response.ok(entity).build();
		}
	}
	
	public static <T> Response getAllResponse(List<T> entityList) {
		if(entityList.isEmpty()) {
			return Response.status(Status.NOT_FOUND).entity(new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No entities found.")).build();
		} else {
			return Response.ok(entityList).build();
		}
	}
	
	public static <T> Response getAllResponse(Set<T> entityList) {
		if(entityList.isEmpty()) {
			return Response.status(Status.NOT_FOUND).entity(new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(), "No entities found.")).build();
		} else {
			return Response.ok(entityList).build();
		}
	}
	
	
}
