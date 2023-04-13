package acmecollege;

import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Professor;
import acmecollege.entity.Student;
import acmecollege.rest.resource.HttpErrorResponse;

public class TestACMECollegeProfessor extends TestACMECollegeSystem {
    @Test
    public void test01_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Professor professor = new Professor();
        professor.setFirstName("Test");
        professor.setLastName("Test");
        professor.setDepartment("Test");
    	Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.entity(professor, MediaType.APPLICATION_JSON));
            
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
        assertThat(students, hasSize(1));
    }
}
