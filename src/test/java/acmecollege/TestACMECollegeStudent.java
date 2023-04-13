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
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
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
 * in which they are run.
 * @author paisl
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeStudent extends TestACMECollegeSystem {

	private static Student newStudent;
	private static Student updateStudent;
	/**
	 * Initializes two Students to be used as entities for create
	 * and update operations
	 * @throws Exception
	 */
	@BeforeAll
    public static void initTestEntities() throws Exception {
        newStudent = new Student();
        updateStudent = new Student();
        
        newStudent.setFullName(NEW_FIRST_NAME, NEW_LAST_NAME);;
        updateStudent.setFullName(UPDATE_FIRST_NAME, UPDATE_LAST_NAME);
    }
	
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
            
        assertThat(response.getStatus(), is(OK));
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
            
        assertThat(response.getStatus(), is(FORBIDDEN));
    }
    
    /**
     * This test tests GET /student/{id} end point when accessed by user in ADMIN_ROLE 
     * when searching for a student that exists. The request gets the first Record
     * provided as sample data and so expects the values of this default first record
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(3)
    public void get_student_by_id_as_adminrole_with_results() throws JsonMappingException, JsonProcessingException {
    	
    	Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD)
                .request()
                .get();
    	
    	Student returnedStudent = response.readEntity(new GenericType<Student>(){});
    	
    	
        assertThat(response.getStatus(), is(OK)); 
        assertThat(returnedStudent.getFirstName(), is(DEFAULT_FIRST_NAME));
        assertThat(returnedStudent.getLastName(), is(DEFAULT_LAST_NAME));
    }
    
    /**
     * This test tests GET /student/{id} end point when accessed by user in ADMIN_ROLE 
     * when searching for a student that does not exist
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(4)
    public void get_student_by_id_as_adminrole_no_results() throws JsonMappingException, JsonProcessingException {
    	
    	Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_NO_RECORD)
                .request()
                .get();
    	
        assertThat(response.getStatus(), is(NOT_FOUND));
       
    }
    /**
     * This test tests /student/{id} to get student by Id of a student that matches
     * the logged in SecurityUser. The default first record provided in the sample 
     * data provides a default SecurityUser with which to try getting authenticated
     * results for this logged in user.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(5)
    public void get_student_by_id_as_userrole_authenticated() throws JsonMappingException, JsonProcessingException {
    	
    	Response response = webTarget
                .register(userAuth)
                .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD)
                .request()
                .get();
    	
    	Student returnedStudent = response.readEntity(new GenericType<Student>(){});
        
        assertThat(response.getStatus(), is(OK)); 
        assertThat(returnedStudent.getFirstName(), is(DEFAULT_FIRST_NAME));
        assertThat(returnedStudent.getLastName(), is(DEFAULT_LAST_NAME));
    }
    
    /**
     * This tests /student/{id} to get student by Id but while logged in as
     * SecurityUser that matches a different student. This should fail as a logged
     * in student shouldn't be able to get any other student's record when they
     * are the active logged in user.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(6)
    public void get_student_by_id_as_userrole_not_authenticated() throws JsonMappingException, JsonProcessingException {
    	
    	Response response = webTarget
                .register(userAuth)
                .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_NO_RECORD)
                .request()
                .get();
    	
        assertThat(response.getStatus(), is(FORBIDDEN));
    }
    
    /**
     * This test tests POST /student end point when accessed by user in ADMIN_ROLE. This
     * test will produce a second Student Record
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(7)
    public void post_new_student_adminrole() throws JsonMappingException, JsonProcessingException {
    	
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
        
        Student returnedStudent = response.readEntity(new GenericType<Student>(){});
            
        assertThat(response.getStatus(), is(OK));
        assertThat(returnedStudent.getFirstName(), is(NEW_FIRST_NAME));
        assertThat(returnedStudent.getLastName(), is(NEW_LAST_NAME));
        
    }
    
    /**
     * This test tests POST /student end point when accessed by user in USER_ROLE.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(8)
    public void post_new_student_userrole() throws JsonMappingException, JsonProcessingException {

        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
           
        assertThat(response.getStatus(), is(FORBIDDEN));   
    }
    
    /**
     * This test tests the PUT /student/{id} Resource when using ADMIN_ROLE to update a student that
     * does exist.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(9)
    public void put_update_student_adminrole() throws JsonMappingException, JsonProcessingException {
    	
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
            .request()
            .put(Entity.entity(updateStudent, MediaType.APPLICATION_JSON));
           
        Student returnedStudent = response.readEntity(new GenericType<Student>(){});
            
        assertThat(response.getStatus(), is(OK));
        assertThat(returnedStudent.getFirstName(), is(UPDATE_FIRST_NAME));
        assertThat(returnedStudent.getLastName(), is(UPDATE_LAST_NAME));
    }
    
    /**
     * This test tests the PUT /student/{id} Resource when using USER_ROLE to update a student.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(10)
    public void put_update_student_userrole() throws JsonMappingException, JsonProcessingException {
    	
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
            .request()
            .put(Entity.entity(updateStudent, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(FORBIDDEN));
       
    }
    
    /**
     * This test tests the DELETE /student/{id} Resource when using USER_ROLE to delete a student.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(11)
    public void delete_student_adminrole() throws JsonMappingException, JsonProcessingException {
    	
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
            .request()
            .delete();
        
        Student deletedStudent = response.readEntity(Student.class);
        
        assertThat(response.getStatus(), is(OK));
        //deleting the record that was created and updated
        //therefore the response should match the updated values of that record
        assertThat(deletedStudent.getFirstName(), is(UPDATE_FIRST_NAME));
        assertThat(deletedStudent.getLastName(), is(UPDATE_LAST_NAME));
       
    }
    
    /**
     * This test tests the DELETE /student/{id} Resource when using USER_ROLE to update a student.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(12)
    public void delete_student_userrole() throws JsonMappingException, JsonProcessingException {
    	
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
            .request()
            .delete();
        
        assertThat(response.getStatus(), is(FORBIDDEN));
       
    }
    
    //*********************** CONSTANTS ******************************
    /**
	 * Sample data provides first Student Record with this First Name
	 */
	private static final String DEFAULT_FIRST_NAME = "John";
	
	/**
	 * Sample data provides first Student Record with this First Name
	 */
	private static final String DEFAULT_LAST_NAME = "Smith";
	
	/**
	 * Used For Creating New Student
	 */
	private static final String NEW_FIRST_NAME = "Chad";
	
	/**
	 * Used For Creating New Student 
	 */
	private static final String NEW_LAST_NAME = "Rocheleau";
	
	/**
	 * Used for Update Student
	 */
	private static String UPDATE_FIRST_NAME = "Thomas";
	
	/**
	 * Used for Update Student
	 */
	private static final String UPDATE_LAST_NAME = "Anderson";
	
	/**
	 * Refers to id of first record provided by sample data
	 */
	private final String DEFAULT_ID_PATH_FIRST_RECORD = "/1";
	
	/**
	 * Refers to id of the new record that these tests will create
	 */
	private final String DEFAULT_ID_PATH_NEW_RECORD = "/2";
	
	/**
	 * Refers to id of a surely non existent Entity when needing to get no results
	 */
	private final String DEFAULT_ID_PATH_NO_RECORD = "/20";
	
	
	
}
