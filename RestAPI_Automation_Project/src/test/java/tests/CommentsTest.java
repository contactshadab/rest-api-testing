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

public class CommentsTest{
	
	//Base class to be used for creation of new Comment
	private class NewCommentDetails{
		int postId;
		String name;
		String email;
		String body;
		NewCommentDetails(int postId, String name, String email, String body){
			this.postId = postId;
			this.name = name;
			this.email = email;
			this.body = body;
		}
	}
	
	//Base class to be used for updation of existing comment
	private class ExistingCommentDetails{
		int id;
		int postId;
		String name;
		String email;
		String body;
		ExistingCommentDetails(int id, int postId, String name, String email, String body){
			this.id = id;
			this.postId = postId;
			this.name = name;
			this.email = email;
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
				setBasePath("comments").
				build();
	}
	
	@DataProvider(name="getCommentIDs")
	public String[][] getCommentIDs(){
		//Parameterize test with set of Ids for data driven testing
		String strCommentIDs = base.getTestData("comment.idList");
		String[] tmpArr = strCommentIDs.split(",");
		String[][] arrCommentIDs = new String[tmpArr.length][1];
		for(int i=0; i<tmpArr.length; i++){
			arrCommentIDs[i][0] = tmpArr[i];
		}
		return arrCommentIDs;
	}
	
	@Test(dataProvider="getCommentIDs")
	public void viewCommentForDifferentIDs(String id) {
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id",equalTo(Integer.valueOf(id)));
	}
	
	@Test
	public void viewCommentForInvalidId() {
		given().
			spec(requestSpec).
		when().
			get(base.getTestData("comment.invalidId")).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode"))).
			body("id",not(hasItem("id")));
	}
	
	@Test
	public void viewCommentDetailsForAParticularID() {
		String id = base.getTestData("comment.id");
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("postId",equalTo(Integer.valueOf(base.getTestData("comment.postId")))).
			body("id",equalTo(Integer.valueOf(id))).
			body("name",equalTo(base.getTestData("comment.name"))).
			body("email",equalTo(base.getTestData("comment.email"))).
			body("body",containsString(base.getTestData("comment.body")));
	}
	
	@Test
	public void createComment(){
		int postId = Integer.valueOf(base.getTestData("comment.postId"));
		String name = base.getTestData("comment.name");
		String email = base.getTestData("comment.email");
		String body = base.getTestData("comment.body");
		NewCommentDetails postDetails = new NewCommentDetails(postId, name, email, body);
		given().
			spec(requestSpec).
			body(postDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("createdStatusCode"))).
			body("id", equalTo(Integer.valueOf(base.getTestData("comment.newId"))));
		
	}
	
	@Test
	public void createCommentInvalidRequest(){
		//Do not pass JSON as request body.
		//It should throw Status Code 415 for Unsupported MediaType Exception
		given().
			spec(requestSpec).
			//body(albumDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("unsupportedMediaTypeStatusCode")));
		
	}
	
	@Test
	public void updateComment(){
		int id = Integer.valueOf(base.getTestData("comment.idToUpdate"));
		int postId = Integer.valueOf(base.getTestData("comment.postId"));
		String name = base.getTestData("comment.name");
		String email = base.getTestData("comment.email");
		String body = base.getTestData("comment.body");
		ExistingCommentDetails commentDetails = new ExistingCommentDetails(id, postId, name, email, body);
		given().
			spec(requestSpec).
			body(commentDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id", equalTo(id));
	}
	
	
	@Test
	public void updateCommentInvalidRequest(){
		int id = Integer.valueOf(base.getTestData("comment.invalidId"));
		int postId = Integer.valueOf(base.getTestData("comment.postId"));
		String name = base.getTestData("comment.name");
		String email = base.getTestData("comment.email");
		String body = base.getTestData("comment.body");
		ExistingCommentDetails commentDetails = new ExistingCommentDetails(id, postId, name, email, body);
		given().
			spec(requestSpec).
			body(commentDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
	
	@Test
	public void deleteComment() {
		String id = base.getTestData("comment.id");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode")));
	}
	
	@Test
	public void deleteCommentInvalidRequest() {
		String id = base.getTestData("comment.invalidId");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
}
