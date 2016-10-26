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

public class AlbumsTest{
	
	//Base class to be used for creating of new Album
	private class NewAlbumDetails{
		int userId;
		String title;
		NewAlbumDetails(int userId, String title){
			this.userId = userId;
			this.title = title;
		}
	}
	
	//Base class to be used for updation of existing album
	private class ExistingAlbumDetails{
		int id;
		int userId;
		String title;
		ExistingAlbumDetails(int id, int userId, String title){
			this.userId = userId;
			this.title = title;
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
				setBasePath("albums").
				build();
	}
	
	@DataProvider(name="getAlbumIDs")
	public String[][] getAlbumIDs(){
		//Parameterize test with set of Ids for data driven testing
		String strAlbumIDs = base.getTestData("album.idList");
		String[] tmpArr = strAlbumIDs.split(",");
		String[][] arrAlbumIDs = new String[tmpArr.length][1];
		for(int i=0; i<tmpArr.length; i++){
			arrAlbumIDs[i][0] = tmpArr[i];
		}
		return arrAlbumIDs;
	}
	
	@Test(dataProvider="getAlbumIDs")
	public void viewAlbumForDifferentIDs(String id) {
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id",equalTo(Integer.valueOf(id)));
	}
	
	@Test
	public void viewAlbumForInvalidId() {
		given().
			spec(requestSpec).
		when().
			get(base.getTestData("album.invalidId")).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode"))).
			body("id",not(hasItem("id")));
	}
	
	@Test
	public void viewAlbumDetailsForAParticularID() {
		String id = base.getTestData("album.id");
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("userId",equalTo(Integer.valueOf(base.getTestData("album.userId")))).
			body("id",equalTo(Integer.valueOf(id))).
			body("title",equalTo(base.getTestData("album.title")));
	}
	
	@Test
	public void createAlbum(){
		int userId = Integer.valueOf(base.getTestData("album.userId"));
		String title = base.getTestData("album.title");
		NewAlbumDetails albumDetails = new NewAlbumDetails(userId, title);
		given().
			spec(requestSpec).
			body(albumDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("createdStatusCode"))).
			body("id", equalTo(Integer.valueOf(base.getTestData("album.newId"))));
		
	}
	
	@Test
	public void createAlbumInvalidRequest(){
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
	public void updateAlbum(){
		int id = Integer.valueOf(base.getTestData("album.idToUpdate"));
		int userId = Integer.valueOf(base.getTestData("album.userId"));
		String title = "Updated title.";
		ExistingAlbumDetails albumDetails = new ExistingAlbumDetails(id, userId, title);
		given().
			spec(requestSpec).
			body(albumDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id", equalTo(id));
	}
	
	@Test
	public void updateAlbumInvalidRequest(){
		int id = Integer.valueOf(base.getTestData("album.invalidId"));
		int userId = Integer.valueOf(base.getTestData("album.userId"));
		String title = "Updated title.";
		ExistingAlbumDetails albumDetails = new ExistingAlbumDetails(id, userId, title);
		given().
			spec(requestSpec).
			body(albumDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
	@Test
	public void deleteAlbum() {
		String id = base.getTestData("album.id");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode")));
	}
	
	@Test
	public void deleteAlbumInvalidRequest() {
		String id = base.getTestData("album.invalidId");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
}
