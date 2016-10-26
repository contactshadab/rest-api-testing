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

public class PhotosTest{
	
	//Base class to be used for creation of new Photo
	private class NewPhotoDetails{
		int albumId;
		String title;
		String url;
		String thumbnailUrl;
		NewPhotoDetails(int albumId, String title, String url, String thumbnailUrl){
			this.albumId = albumId;
			this.title = title;
			this.url = url;
			this.thumbnailUrl = thumbnailUrl;
		}
	}
	
	//Base class to be used for updation of existing photo
	private class ExistingPhotoDetails{
		int id;
		int albumId;
		String title;
		String url;
		String thumbnailUrl;
		ExistingPhotoDetails(int id, int albumId, String title, String url, String thumbnailUrl){
			this.id = id;
			this.albumId = albumId;
			this.title = title;
			this.url = url;
			this.thumbnailUrl = thumbnailUrl;
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
				setBasePath("photos").
				build();
	}
	
	@DataProvider(name="getPhotoIDs")
	public String[][] getPhotoIDs(){
		//Parameterize test with set of Ids for data driven testing
		String strPhotoIDs = base.getTestData("photo.idList");
		String[] tmpArr = strPhotoIDs.split(",");
		String[][] arrPhotoIDs = new String[tmpArr.length][1];
		for(int i=0; i<tmpArr.length; i++){
			arrPhotoIDs[i][0] = tmpArr[i];
		}
		return arrPhotoIDs;
	}
	
	@Test(dataProvider="getPhotoIDs")
	public void viewPhotoForDifferentIDs(String id) {
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id",equalTo(Integer.valueOf(id)));
	}
	
	@Test
	public void viewPhotoForInvalidId() {
		given().
			spec(requestSpec).
		when().
			get(base.getTestData("photo.invalidId")).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode"))).
			body("id",not(hasItem("id")));
	}
	
	@Test
	public void viewPhotoDetailsForAParticularID() {
		String id = base.getTestData("photo.id");
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("albumId",equalTo(Integer.valueOf(base.getTestData("photo.albumId")))).
			body("id",equalTo(Integer.valueOf(id))).
			body("title",equalTo(base.getTestData("photo.title"))).
			body("url",equalTo(base.getTestData("photo.url"))).
			body("thumbnailUrl",containsString(base.getTestData("photo.thumbnailUrl")));
	}
	
	@Test
	public void createPhoto(){
		int albumId = Integer.valueOf(base.getTestData("photo.albumId"));
		String title = base.getTestData("photo.title");
		String url = base.getTestData("photo.url");
		String thumbnailUrl = base.getTestData("photo.thumbnailUrl");
		NewPhotoDetails postDetails = new NewPhotoDetails(albumId, title, url, thumbnailUrl);
		given().
			spec(requestSpec).
			body(postDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("createdStatusCode"))).
			body("id", equalTo(Integer.valueOf(base.getTestData("photo.newId"))));
		
	}
	
	@Test
	public void createPhotoInvalidRequest(){
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
	public void updatePhoto(){
		int id = Integer.valueOf(base.getTestData("photo.idToUpdate"));
		int albumId = Integer.valueOf(base.getTestData("photo.albumId"));
		String title = base.getTestData("photo.title");
		String url = base.getTestData("photo.url");
		String thumbnailUrl = base.getTestData("photo.thumbnailUrl");
		ExistingPhotoDetails photoDetails = new ExistingPhotoDetails(id, albumId, title, url, thumbnailUrl);
		given().
			spec(requestSpec).
			body(photoDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id", equalTo(id));
	}
	
	@Test
	public void updatePhotoInvalidRequest(){
		int id = Integer.valueOf(base.getTestData("photo.invalidId"));
		int albumId = Integer.valueOf(base.getTestData("photo.albumId"));
		String title = base.getTestData("photo.title");
		String url = base.getTestData("photo.url");
		String thumbnailUrl = base.getTestData("photo.thumbnailUrl");
		ExistingPhotoDetails photoDetails = new ExistingPhotoDetails(id, albumId, title, url, thumbnailUrl);
		given().
			spec(requestSpec).
			body(photoDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
	@Test
	public void deletePhoto() {
		String id = base.getTestData("photo.id");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode")));
	}
	
	@Test
	public void deletePhotoInvalidId() {
		String id = base.getTestData("photo.invalidId");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
}
