package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;

public class MovieControllerRA {
	
	private String movieName;
	private Long existingMovieId, nonExistingMovieId;
	private String adminUsername, adminPassword, adminToken, clientUsername, clientPassword, clientToken;
	private Map<String, Object> postMovieInstance;

	@BeforeEach
	void setUp() throws Exception{
		baseURI = "http://localhost:8080";

		movieName = "Duna";
		existingMovieId = 1L;
		nonExistingMovieId = 100L;

		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Pulp Fiction: Tempo de Violência");
		postMovieInstance.put("score", 8.9);
		postMovieInstance.put("count", 89);
		postMovieInstance.put("image", "https://www.imdb.com/title/tt0110912/mediaviewer/rm0302081/?ref_=tt_ov_i");

	}


	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

		given().get("/movies").then()
		.statusCode(200);
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		
		
		given().when().get("/movies?title={movieName}", movieName)
		.then()
		.statusCode(200)
		.body("content.id[0]", is(28))
		.body("content.title[0]", equalTo(movieName))
		.body("content.score[0]", is(0F))
		.body("content.count[0]", is(0))
		.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jYEW5xZkZk2WTrdbMGAPFuBqbDc.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		
		given().when().get("/movies/{id}", existingMovieId)
		.then()
		.statusCode(200)
		.body("id", is(1))
		.body("title", equalTo("The Witcher"))
		.body("score", is(4.5F))
		.body("count", is(2))
		.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		
		given().when().get("/movies/{id}", nonExistingMovieId)
		.then()
		.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {	
		
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
		.header("Content-type", "application/json")
        .header("Authorization", "Bearer " + adminToken)
        .body(newMovie)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
		.when()
		.post("/movies")
		.then()
		.statusCode(201)
		.body("id", is(30))
		.body("title", equalTo(postMovieInstance.get("title")))
		.body("score", is(8.9F))
		.body("count", is(postMovieInstance.get("count")))
		.body("image", equalTo(postMovieInstance.get("image")));
		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
		.header("Content-type", "application/json")
        .header("Authorization", "Bearer " + clientToken)
        .body(newMovie)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
		.when()
		.post("/movies")
		.then()
		.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
		.header("Content-type", "application/json")
        .header("Authorization", "Bearer " + clientToken + "TokenInvalido")
        .body(newMovie)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
		.when()
		.post("/movies")
		.then()
		.statusCode(401);
	}
}
