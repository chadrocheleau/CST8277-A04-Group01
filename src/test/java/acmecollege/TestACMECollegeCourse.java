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

import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Course;


/**
 * This test suite tests the ACME College API for the Course Entity 
 * of the ACME College System. This test suite uses TestMethodOrder
 * to ensure that the tests are run in a particular order as some tests 
 * may change expected results of other tests depending on the order
 * in which they are run.
 * @author paisl
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestACMECollegeCourse extends TestACMECollegeSystem {

	private static Course newCourse;
	private static Course updateCourse;
	
	/**
	 * Initializes two Courses to be used as entities for create
	 * and update operations that when successful should return the 
	 * values of the created or updated record.
	 * @throws Exception
	 */
	@BeforeAll
    public static void initTestEntities() throws Exception {
        newCourse = new Course();
        updateCourse = new Course();
        
        newCourse.setCourse(NEW_COURSE_CODE,
        		NEW_COURSE_TITLE,
        		NEW_COURSE_YEAR,
        		NEW_COURSE_SEMESTER,
        		NEW_COURSE_UNITS,
        		NEW_COURSE_ONLINE);
        
        updateCourse.setCourse(UPDATE_COURSE_CODE,
        		UPDATE_COURSE_TITLE,
        		UPDATE_COURSE_YEAR,
        		UPDATE_COURSE_SEMESTER,
        		UPDATE_COURSE_UNITS,
        		UPDATE_COURSE_ONLINE);
    }
	
	/**
	 * This test tests GET /student end point when accessed by user
	 * in ADMIN_ROLE.  
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
    @Test
    @Order(1)
    public void get_all_courses_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        
        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
            
        assertThat(response.getStatus(), is(OK));
        assertThat(courses, is(not(empty())));
    }
    
    /**
   	 * This test tests GET /course end point when accessed by user
   	 * in USER_ROLE.  
   	 * @throws JsonMappingException
   	 * @throws JsonProcessingException
   	 */
       @Test
       @Order(2)
       public void get_all_courses_with_userrole() throws JsonMappingException, JsonProcessingException {
           Response response = webTarget
               .register(userAuth)
               .path(COURSE_RESOURCE_NAME)
               .request()
               .get();
               
           List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
           
           assertThat(response.getStatus(), is(OK));
           assertThat(courses, is(not(empty())));
       }
       
       /**
        * This test tests GET /course/{id} end point when accessed by user in ADMIN_ROLE 
        * when searching for a student that exists. The request gets the first Record
        * provided as sample data and so expects the values of this default first record
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(3)
       public void get_course_by_id_as_adminrole_with_results() throws JsonMappingException, JsonProcessingException {
       	
    	   Response response = webTarget
                   .register(adminAuth)
                   .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD)
                   .request()
                   .get();
       	
    	   Course returnedCourse = response.readEntity(new GenericType<Course>(){});
       	
           assertThat(response.getStatus(), is(OK)); 
           assertThat(returnedCourse.getCourseCode(), is(DEFAULT_COURSE_CODE));
           assertThat(returnedCourse.getCourseTitle(), is(DEFAULT_COURSE_TITLE));
           assertThat(returnedCourse.getYear(), is(DEFAULT_COURSE_YEAR));
           assertThat(returnedCourse.getSemester(), is(DEFAULT_COURSE_SEMESTER));
           assertThat(returnedCourse.getCreditUnits(), is(DEFAULT_COURSE_UNITS));
           assertThat(returnedCourse.getOnline(), is(DEFAULT_COURSE_ONLINE));
       }
       
       /**
        * This test tests GET /course/{id} end point when accessed by user in ADMIN_ROLE 
        * when searching for a course that does not exist
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(4)
       public void get_course_by_id_as_adminrole_no_results() throws JsonMappingException, JsonProcessingException {
       	
    	   Response response = webTarget
                   .register(adminAuth)
                   .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_NO_RECORD)
                   .request()
                   .get();
       	
           assertThat(response.getStatus(), is(NOT_FOUND));
          
       }
       
       /**
        * This test tests GET /course/{id} to get course by Id for course that
        * exists
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(5)
       public void get_course_by_id_as_userrole_with_results() throws JsonMappingException, JsonProcessingException {
       	
    	   Response response = webTarget
                   .register(userAuth)
                   .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_FIRST_RECORD)
                   .request()
                   .get();
       	
       	Course returnedCourse = response.readEntity(new GenericType<Course>(){});
           
           assertThat(response.getStatus(), is(OK)); 
           assertThat(returnedCourse.getCourseCode(), is(DEFAULT_COURSE_CODE));
           assertThat(returnedCourse.getCourseTitle(), is(DEFAULT_COURSE_TITLE));
           assertThat(returnedCourse.getYear(), is(DEFAULT_COURSE_YEAR));
           assertThat(returnedCourse.getSemester(), is(DEFAULT_COURSE_SEMESTER));
           assertThat(returnedCourse.getCreditUnits(), is(DEFAULT_COURSE_UNITS));
           assertThat(returnedCourse.getOnline(), is(DEFAULT_COURSE_ONLINE));
       }
       
       /**
        * This test tests GET /course/{id} end point when accessed by user in USER_ROLE 
        * when searching for a course that does not exist
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(6)
       public void get_course_by_id_as_userrole_no_results() throws JsonMappingException, JsonProcessingException {
       	
    	   Response response = webTarget
                   .register(userAuth)
                   .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_NO_RECORD)
                   .request()
                   .get();
       	
           assertThat(response.getStatus(), is(NOT_FOUND));
          
       }
       
       /**
        * This test tests POST /course end point when accessed by user in ADMIN_ROLE. This
        * test will produce a Third course Record
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(7)
       public void post_new_course_adminrole() throws JsonMappingException, JsonProcessingException {

           Response response = webTarget
               .register(adminAuth)
               .path(COURSE_RESOURCE_NAME)
               .request()
               .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
           
           Course created = response.readEntity(new GenericType<Course>(){});
               
           assertThat(response.getStatus(), is(OK)); 
           assertThat(created.getCourseCode(), is(NEW_COURSE_CODE));
           assertThat(created.getCourseTitle(), is(NEW_COURSE_TITLE));
           assertThat(created.getYear(), is(NEW_COURSE_YEAR));
           assertThat(created.getSemester(), is(NEW_COURSE_SEMESTER));
           assertThat(created.getCreditUnits(), is(NEW_COURSE_UNITS));
           assertThat(created.getOnline(), is(NEW_COURSE_ONLINE));
           
       }
       
       
       /**
        * This test tests POST /course end point when accessed by user in USER_ROLE.
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(8)
       public void post_new_course_userrole() throws JsonMappingException, JsonProcessingException {
           
    	   Response response = webTarget
               .register(userAuth)
               .path(COURSE_RESOURCE_NAME)
               .request()
               .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
              
           assertThat(response.getStatus(), is(FORBIDDEN));   
       }
       
       /**
        * This test tests the PUT /course/{id} Resource when using ADMIN_ROLE to update a course.
        * that does exist
        * will update the new record created in test 8.
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(9)
       public void put_update_course_adminrole() throws JsonMappingException, JsonProcessingException {
           
    	   Response response = webTarget
               .register(adminAuth)
               .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
               .request()
               .put(Entity.entity(updateCourse, MediaType.APPLICATION_JSON));
              
           Course updatedCourse = response.readEntity(new GenericType<Course>(){});
               
           assertThat(response.getStatus(), is(OK)); 
           assertThat(updatedCourse.getCourseCode(), is(UPDATE_COURSE_CODE));
           assertThat(updatedCourse.getCourseTitle(), is(UPDATE_COURSE_TITLE));
           assertThat(updatedCourse.getYear(), is(UPDATE_COURSE_YEAR));
           assertThat(updatedCourse.getSemester(), is(UPDATE_COURSE_SEMESTER));
           assertThat(updatedCourse.getCreditUnits(), is(UPDATE_COURSE_UNITS));
           assertThat(updatedCourse.getOnline(), is(UPDATE_COURSE_ONLINE));
       }
       
       /**
        * This test tests the PUT /course/{id} Resource when using USER_ROLE to update a student.
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(10)
       public void put_update_course_userrole() throws JsonMappingException, JsonProcessingException {
           
    	   Response response = webTarget
               .register(userAuth)
               .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
               .request()
               .put(Entity.entity(updateCourse, MediaType.APPLICATION_JSON));
           
           assertThat(response.getStatus(), is(FORBIDDEN));
          
       }
       
       /**
        * This test tests the DELETE /course/{id} Resource when using ADMIN_ROLE to delete a Course
        * @throws JsonProcessingException
        */
       @Test
       @Order(11)
       public void delete_course_adminrole() throws JsonMappingException, JsonProcessingException {
           
    	   Response response = webTarget
               .register(adminAuth)
               .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
               .request()
               .delete();
           
           Course deletedCourse = response.readEntity(Course.class);
           
           assertThat(response.getStatus(), is(OK)); 
           assertThat(deletedCourse.getCourseCode(), is(UPDATE_COURSE_CODE));
           assertThat(deletedCourse.getCourseTitle(), is(UPDATE_COURSE_TITLE));
           assertThat(deletedCourse.getYear(), is(UPDATE_COURSE_YEAR));
           assertThat(deletedCourse.getSemester(), is(UPDATE_COURSE_SEMESTER));
           assertThat(deletedCourse.getCreditUnits(), is(UPDATE_COURSE_UNITS));
           assertThat(deletedCourse.getOnline(), is(UPDATE_COURSE_ONLINE));
          
       }
       
       /**
        * This test tests the DELETE /course/{id} Resource when using USER_ROLE to delete a student.
        * @throws JsonMappingException
        * @throws JsonProcessingException
        */
       @Test
       @Order(12)
       public void delete_course_userrole() throws JsonMappingException, JsonProcessingException {
           Response response = webTarget
               .register(userAuth)
               .path(COURSE_RESOURCE_NAME + DEFAULT_ID_PATH_NEW_RECORD)
               .request()
               .delete();
           
           assertThat(response.getStatus(), is(FORBIDDEN));
          
       }
       
       //*************************** CONSTANTS ******************
    /**  
     * Refers to id of first record provided by sample data
  	 */
  	private final String DEFAULT_ID_PATH_FIRST_RECORD = "/1";
  	
  	/**
  	 * Refers to id of the new record that these tests will create
  	 */
  	private final String DEFAULT_ID_PATH_NEW_RECORD = "/3";
  	
  	/**
  	 * Refers to id of a surely non existent Entity when needing to get no results
  	 */
  	private final String DEFAULT_ID_PATH_NO_RECORD = "/20";
  	
  	/**
  	 * These are the values of the default Course provided as record 1
  	 */
  	private static final String DEFAULT_COURSE_CODE = "CST8277";
  	private static final String DEFAULT_COURSE_TITLE = "Enterprise Application Programming";
  	private static final int DEFAULT_COURSE_YEAR = 2022;
  	private static final String DEFAULT_COURSE_SEMESTER = "AUTUMN";
  	private static final int DEFAULT_COURSE_UNITS = 3;
  	private static final byte DEFAULT_COURSE_ONLINE = (byte) 0;
  	
  	/**
  	 * These are the values of the newCourse Entity of Course being created
  	 */
  	private static final String NEW_COURSE_CODE = "CST6666";
  	private static final String NEW_COURSE_TITLE = "My New Course";
  	private static final int NEW_COURSE_YEAR = 2023;
  	private static final String NEW_COURSE_SEMESTER = "WINTER";
  	private static final int NEW_COURSE_UNITS = 2;
  	private static final byte NEW_COURSE_ONLINE = (byte) 0;
  	
  	/**
  	 * These are the values of the updateCourse Entity representing update details
  	 */
  	private static final String UPDATE_COURSE_CODE = "UDT2222";
  	private static final String UPDATE_COURSE_TITLE = "My Updated Course";
  	private static final int UPDATE_COURSE_YEAR = 2024;
  	private static final String UPDATE_COURSE_SEMESTER = "SUMMER";
  	private static final int UPDATE_COURSE_UNITS = 1;
  	private static final byte UPDATE_COURSE_ONLINE = (byte) 1;
  	
  	
}
