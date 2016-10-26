package tests;

import lib._Base;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.builder.RequestSpecBuilder;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;

public class ToDosTest{
	
	//Base class to be used for creating of new ToDos
	private class NewToDoDetails{
		int userId;
		String title;
		boolean completed;
		NewToDoDetails(int userId, String title, boolean completed){
			this.userId = userId;
			this.title = title;
			this.completed = completed;
		}
	}
	
	//Base class to be used for updation of existing todo
	private class ExistingToDoDetails{
		int id;
		int userId;
		String title;
		boolean completed;
		ExistingToDoDetails(int id, int userId, String title, boolean completed){
			this.userId = userId;
			this.title = title;
			this.completed = completed;
		}
	}

	_Base base;
	RequestSpecification requestSpec;
	
	@BeforeClass
	public void beforeClass(){
		base = new _Base();
		//Load properties files
		base.loadConfig();
		base.loadTestData();
		requestSpec = new RequestSpecBuilder().
				setBaseUri(base.getConfig("baseURI")).
				setBasePath("todos").
				build();
	}
	
	@DataProvider(name="getToDosIDs")
	public String[][] getToDosIDs(){
		//Parameterize test with set of Ids for data driven testing
		String strToDosIDs = base.getTestData("todo.idList");
		String[] tmpArr = strToDosIDs.split(",");
		String[][] arrToDosIDs = new String[tmpArr.length][1];
		for(int i=0; i<tmpArr.length; i++){
			arrToDosIDs[i][0] = tmpArr[i];
		}
		return arrToDosIDs;
	}
	
	@Test(dataProvider="getToDosIDs")
	public void viewToDosForDifferentIDs(String id) {
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id",equalTo(Integer.valueOf(id)));
	}
	
	@Test
	public void viewToDosForInvalidId() {
		given().
			spec(requestSpec).
		when().
			get(base.getTestData("todo.invalidId")).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode"))).
			body("id",not(hasItem("id")));
	}
	
	@Test
	public void viewToDosDetailsForAParticularID() {
		String id = base.getTestData("todo.id");
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("userId",equalTo(Integer.valueOf(base.getTestData("todo.userId")))).
			body("id",equalTo(Integer.valueOf(id))).
			body("title",equalTo(base.getTestData("todo.title"))).
			body("completed",equalTo(Boolean.valueOf(base.getTestData("todo.completed"))));
	}
	
	@Test
	public void createToDos(){
		int userId = Integer.valueOf(base.getTestData("todo.userId"));
		String title = base.getTestData("todo.title");
		boolean completed = Boolean.valueOf(base.getTestData("todo.completed"));
		NewToDoDetails todoDetails = new NewToDoDetails(userId, title, completed);
		given().
			spec(requestSpec).
			body(todoDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("createdStatusCode"))).
			body("id", equalTo(Integer.valueOf(base.getTestData("todo.newId"))));
		
	}
	
	@Test
	public void createToDosInvalidRequest(){
		//Do not pass json as request
		given().
			spec(requestSpec).
			//body(todoDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("unsupportedMediaTypeStatusCode")));
		
	}
	
	@Test
	public void updateToDos(){
		int id = Integer.valueOf(base.getTestData("todo.idToUpdate"));
		int userId = Integer.valueOf(base.getTestData("todo.userId"));
		String title = "Updated title.";
		boolean completed = false;
		ExistingToDoDetails todoDetails = new ExistingToDoDetails(id, userId, title, completed);
		given().
			spec(requestSpec).
			body(todoDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id", equalTo(id));
	}
	
	@Test
	public void updateToDosInvalidRequest(){
		int id = Integer.valueOf(base.getTestData("todo.invalidId"));
		int userId = Integer.valueOf(base.getTestData("todo.userId"));
		String title = "Updated title.";
		boolean completed = false;
		ExistingToDoDetails todoDetails = new ExistingToDoDetails(id, userId, title, completed);
		given().
			spec(requestSpec).
			body(todoDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
	@Test
	public void deleteToDos() {
		String id = base.getTestData("todo.id");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode")));
	}
	
	
	@Test
	public void deleteToDosInvalidRequest() {
		String id = base.getTestData("todo.invalidId");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
}
