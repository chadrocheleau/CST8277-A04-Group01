/**
 * File:  TestACMECollegeStudent.java
 * Course materials (23W) CST 8277
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
package acmecollege;

import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Student;

/**
 * This test suite tests the ACME College API for the Student Entity 
 * of the ACME College System. This test suite uses TestMethodOrder
 * to ensure that the tests are run in a particular order as some tests 
 * may change expected results of other tests depending on the order
 * in which they are run
 * @author paisl
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeStudent extends TestACMECollegeSystem {

	/**
	 * This test tests GET /student end point when accessed by user
	 * in ADMIN_ROLE.  
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
    @Test
    @Order(1)
    public void get_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
            
        assertThat(response.getStatus(), is(200));
        assertThat(students, is(not(empty())));
    }
    
    /**
	 * This test tests GET /student end point when accessed by user
	 * in USER_ROLE.  
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
    @Test
    @Order(2)
    public void get_all_students_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
            
        assertThat(response.getStatus(), is(403));
    }
    
    /**
     * This test tests POST /student end point when accessed by user in ADMIN_ROLE.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(3)
    public void post_new_student_adminrole() throws JsonMappingException, JsonProcessingException {
    	
    	Student student = new Student();
    	student.setFullName("Chad", "Rocheleau");

        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.entity(student, MediaType.APPLICATION_JSON));
        
        Student returnedStudent = response.readEntity(Student.class);
            
        assertThat(response.getStatus(), is(200));
        assertThat(returnedStudent.getFirstName(), is("Chad"));
        assertThat(returnedStudent.getLastName(), is("Rocheleau"));
        
    }
    
    /**
     * This test tests POST /student end point when accessed by user in ADMIN_ROLE.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(4)
    public void post_new_student_userrole() throws JsonMappingException, JsonProcessingException {
    	
    	Student student = new Student();
    	student.setFullName("Jane", "Rocheleau");

        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.entity(student, MediaType.APPLICATION_JSON));
           
        assertThat(response.getStatus(), is(403));   
    }
}
