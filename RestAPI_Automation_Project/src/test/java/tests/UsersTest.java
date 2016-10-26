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

public class UsersTest{
	
	//Base class to be used for creation of new User
	private class NewUserDetails{
		String name;
		String username;
		String email;
		String street;
		String suite;
		String city;
		String zipcode;
		String lat;
		String lng;
		String phone;
		String website;
		String company;
		String catchPhrase;
		String bs;
		NewUserDetails(String name,String username,String email,String street,String suite,String city,String zipcode,String lat2,String lng2,String phone,String website,String company,String catchPhrase,String bs){
			this.name = name;
			this.username = username;
			this.email = email;
			this.street = street;
			this.suite = suite;
			this.city = city;
			this.zipcode = zipcode;
			this.lat = lat2;
			this.lng = lng2;
			this.phone = phone;
			this.website = website;
			this.company = company;
			this.catchPhrase = catchPhrase;
			this.bs = bs;
		}
	}
	
	//Base class to be used for updation of existing user
	private class ExistingUserDetails{
		int id;
		String name;
		String username;
		String email;
		String street;
		String suite;
		String city;
		String zipcode;
		String lat;
		String lng;
		String phone;
		String website;
		String company;
		String catchPhrase;
		String bs;
		ExistingUserDetails(int id, String name,String username,String email,String street,String suite,String city,String zipcode,String lat,String lng,String phone,String website,String company,String catchPhrase,String bs){
			this.id = id;
			this.name = name;
			this.username = username;
			this.email = email;
			this.street = street;
			this.suite = suite;
			this.city = city;
			this.zipcode = zipcode;
			this.lat = lat;
			this.lng = lng;
			this.phone = phone;
			this.website = website;
			this.company = company;
			this.catchPhrase = catchPhrase;
			this.bs = bs;
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
				setBasePath("users").
				build();
	}
	
	@DataProvider(name="getUserIDs")
	public String[][] getUserIDs(){
		//Parameterize test with set of Ids for data driven testing
		String strUserIDs = base.getTestData("user.idList");
		String[] tmpArr = strUserIDs.split(",");
		String[][] arrUserIDs = new String[tmpArr.length][1];
		for(int i=0; i<tmpArr.length; i++){
			arrUserIDs[i][0] = tmpArr[i];
		}
		return arrUserIDs;
	}
	
	@Test(dataProvider="getUserIDs")
	public void viewUserForDifferentIDs(String id) {
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id",equalTo(Integer.valueOf(id)));
	}
	
	@Test
	public void viewUserForInvalidId() {
		given().
			spec(requestSpec).
		when().
			get(base.getTestData("user.invalidId")).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode"))).
			body("id",not(hasItem("id")));
	}
	
	@Test
	public void viewUserDetailsForAParticularID() {
		String id = base.getTestData("user.id");
		given().
			spec(requestSpec).
		when().
			get(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id",equalTo(Integer.valueOf(id))).
			body("name",equalTo(base.getTestData("user.name"))).
			body("email",equalTo(base.getTestData("user.email"))).
			body("address.street",containsString(base.getTestData("user.street"))).
			body("address.suite",containsString(base.getTestData("user.suite"))).
			body("address.city",containsString(base.getTestData("user.city"))).
			body("address.zipcode",containsString(base.getTestData("user.zipcode"))).
			body("address.geo.lat",containsString(base.getTestData("user.lat"))).
			body("address.geo.lng",containsString(base.getTestData("user.lng"))).
			body("phone",containsString(base.getTestData("user.phone"))).
			body("website",containsString(base.getTestData("user.website"))).
			body("company.name",containsString(base.getTestData("user.company"))).
			body("company.catchPhrase",containsString(base.getTestData("user.catchPhrase"))).
			body("company.bs",containsString(base.getTestData("user.bs")));	
			
	}
	
	@Test
	public void createUser(){
		String name = base.getTestData("user.name");
		String username = base.getTestData("user.username");
		String email = base.getTestData("user.email");
		String street = base.getTestData("user.street");
		String suite = base.getTestData("user.suite");
		String city = base.getTestData("user.city");
		String zipcode = base.getTestData("user.zipcode");
		String lat = base.getTestData("user.lat");
		String lng = base.getTestData("user.lng");
		String phone = base.getTestData("user.phone");
		String website = base.getTestData("user.website");
		String company = base.getTestData("user.company");
		String catchPhrase = base.getTestData("user.catchPhrase");
		String bs = base.getTestData("user.bs");
		
		NewUserDetails postDetails = new NewUserDetails(name,username,email,street,suite,city,zipcode,lat,lng,phone,website,company,catchPhrase,bs);
		given().
			spec(requestSpec).
			body(postDetails.toString()).
		when().
			post().
		then().
			statusCode(Integer.valueOf(base.getConfig("createdStatusCode"))).
			body("id", equalTo(Integer.valueOf(base.getTestData("user.newId"))));
		
	}
	
	@Test
	public void createUserInvalidRequest(){
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
	public void updateUser(){
		int id = Integer.valueOf(base.getTestData("user.idToUpdate"));
		String name = base.getTestData("user.name");
		String username = base.getTestData("user.username");
		String email = base.getTestData("user.email");
		String street = base.getTestData("user.street");
		String suite = base.getTestData("user.suite");
		String city = base.getTestData("user.city");
		String zipcode = base.getTestData("user.zipcode");
		String lat = base.getTestData("user.lat");
		String lng = base.getTestData("user.lng");
		String phone = base.getTestData("user.phone");
		String website = base.getTestData("user.website");
		String company = base.getTestData("user.company");
		String catchPhrase = base.getTestData("user.catchPhrase");
		String bs = base.getTestData("user.bs");
		ExistingUserDetails userDetails = new ExistingUserDetails(id, name,username,email,street,suite,city,zipcode,lat,lng,phone,website,company,catchPhrase,bs);
		given().
			spec(requestSpec).
			body(userDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode"))).
			body("id", equalTo(id));
	}
	
	@Test
	public void updateUserInvalidRequest(){
		int id = Integer.valueOf(base.getTestData("user.invalidId"));
		String name = base.getTestData("user.name");
		String username = base.getTestData("user.username");
		String email = base.getTestData("user.email");
		String street = base.getTestData("user.street");
		String suite = base.getTestData("user.suite");
		String city = base.getTestData("user.city");
		String zipcode = base.getTestData("user.zipcode");
		String lat = base.getTestData("user.lat");
		String lng = base.getTestData("user.lng");
		String phone = base.getTestData("user.phone");
		String website = base.getTestData("user.website");
		String company = base.getTestData("user.company");
		String catchPhrase = base.getTestData("user.catchPhrase");
		String bs = base.getTestData("user.bs");
		ExistingUserDetails userDetails = new ExistingUserDetails(id, name,username,email,street,suite,city,zipcode,lat,lng,phone,website,company,catchPhrase,bs);
		given().
			spec(requestSpec).
			body(userDetails.toString()).
		when().
			put(String.valueOf(id)).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
	@Test
	public void deleteUser() {
		String id = base.getTestData("user.id");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("successStatusCode")));
	}
	
	@Test
	public void deleteUserInvalidRequest() {
		String id = base.getTestData("user.invalidId");
		given().
			spec(requestSpec).
		when().
			delete(id).
		then().
			statusCode(Integer.valueOf(base.getConfig("notFoundStatusCode")));
	}
	
}
