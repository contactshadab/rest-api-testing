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

public class PostsTest{
	
	//Base class to be used for creating of new Post
	private class NewPostDetails{
		int userId;
		String title;
		String body;
		NewPostDetails(int userId, String title, String body){
			this.userId = userId;
			this.title = title;
			this.body = body;
		}
	}
	
	//Base class to be used for updation of existing post
	private class ExistingPostDetails{
		int id;
		int userId;
		String title;
		String body;
		ExistingPostDetails(int id, int userId, String title, String body){
			this.userId = userId;
			this.title = title;
			this.body = body;
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
				setBasePath("posts").
				build();
	}
	
	@DataProvider(name="getPostIDs")
	public String[][] getPostIDs(){
		//Parameterize test with set of Ids for data driven testing
		String strPostIDs = base.getTestData("post.idList");
		String[] tmpArr = strPostIDs.split(",");
		String[][] arrPostIDs = new String[tmpArr.length][1];
		for(int i=0; i<tmpArr.length; i++){
			arrPostIDs[i][0] = tmpArr[i];
		}
		return arrPostIDs;
	}
	
	@Test(dataProvider="getPostIDs")
	public void viewPostForDifferentIDs(String id) {
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id",equalTo(Integer.valueOf(id)));
	}
	
	@Test
	public void viewPostForInvalidId() {
		given().
			spec(requestSpec).
		when().
			get(base.getTestData("post.invalidId")).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode"))).
			body("id",not(hasItem("id")));
	}
	
	@Test
	public void viewPostDetailsForAParticularID() {
		String id = base.getTestData("post.id");
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("userId",equalTo(Integer.valueOf(base.getTestData("post.userId")))).
			body("id",equalTo(Integer.valueOf(id))).
			body("title",equalTo(base.getTestData("post.title"))).
			body("body",containsString(base.getTestData("post.body")));
	}
	
	@Test
	public void createPost(){
		int userId = Integer.valueOf(base.getTestData("post.userId"));
		String title = base.getTestData("post.title");
		String body = base.getTestData("post.body");
		NewPostDetails postDetails = new NewPostDetails(userId, title, body);
		given().
			spec(requestSpec).
			body(postDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("createdStatusCode"))).
			body("id", equalTo(Integer.valueOf(base.getTestData("post.newId"))));
		
	}
	
	@Test
	public void createPostInvalidRequest(){
		//Do not pass json as request
		given().
			spec(requestSpec).
			//body(postDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("unsupportedMediaTypeStatusCode")));
		
	}
	
	@Test
	public void updatePost(){
		int id = Integer.valueOf(base.getTestData("post.idToUpdate"));
		int userId = Integer.valueOf(base.getTestData("post.userId"));
		String title = "Updated title.";
		String body = "This is an updated body.";
		ExistingPostDetails postDetails = new ExistingPostDetails(id, userId, title, body);
		given().
			spec(requestSpec).
			body(postDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id", equalTo(id));
	}
	
	
	@Test
	public void updatePostInvalidRequest(){
		int id = Integer.valueOf(base.getTestData("post.invalidId"));
		int userId = Integer.valueOf(base.getTestData("post.userId"));
		String title = "Updated title.";
		String body = "This is an updated body.";
		ExistingPostDetails postDetails = new ExistingPostDetails(id, userId, title, body);
		given().
			spec(requestSpec).
			body(postDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
	
	@Test
	public void deletePost() {
		String id = base.getTestData("post.id");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode")));
	}
	
	@Test
	public void deletePostInvalidRequest() {
		String id = base.getTestData("post.invalidId");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
}
