package edu.brown.cs.student.main.server.server_responses;

public record ServerFailureResponse(String type, String error_type, String details) {}
