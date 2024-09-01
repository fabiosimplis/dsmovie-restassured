package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;

public class ScoreControllerRA {
	

	private Long existingMovieId, nonExistingMovieId;
	private String adminUsername, adminPassword, adminToken;
	private Map<String, Object> postScoreInstance;

	@BeforeEach
	void setUp() throws Exception{
		baseURI = "http://localhost:8080";

		existingMovieId = 1L;
		nonExistingMovieId = 100L;

		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		
		postScoreInstance = new HashMap<>();
		postScoreInstance.put("movieId", existingMovieId);
		postScoreInstance.put("score", 4.9);

	}

	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {	
		postScoreInstance.put("movieId", nonExistingMovieId);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
		.log().all()
		.header("Content-type", "application/json")
        .header("Authorization", "Bearer " + adminToken)
        .body(newScore)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .put("/scores")
        .then()
        .log().all()
        .statusCode(404)
        .body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {

		postScoreInstance.put("movieId", null);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
		.log().all()
		.header("Content-type", "application/json")
        .header("Authorization", "Bearer " + adminToken)
        .body(newScore)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .put("/scores")
        .then()
        .log().all()
        .statusCode(422)
        .body("errors.message[0]", equalTo("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		
		postScoreInstance.put("score", -1);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
		.log().all()
		.header("Content-type", "application/json")
        .header("Authorization", "Bearer " + adminToken)
        .body(newScore)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .put("/scores")
        .then()
        .log().all()
        .statusCode(422)
        .body("errors.message[0]", equalTo("Valor mínimo 0"));
	}
}
