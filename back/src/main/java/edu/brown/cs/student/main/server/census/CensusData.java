package edu.brown.cs.student.main.server.census;

/**
 * Record that helps us store CensusData.
 *
 * @param broadbandPct
 */
public record CensusData(double broadbandPct, String dateTime) {}
