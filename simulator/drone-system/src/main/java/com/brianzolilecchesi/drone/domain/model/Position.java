package com.brianzolilecchesi.drone.domain.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.brianzolilecchesi.drone.infrastructure.service.navigation.flight_plan.model.geo.GeoCalculatorSingleton;

public final class Position extends Coordinate {
	
	private static final double THRESHOLD = 1e-6;
	private final double altitude;
	
	public Position(double latitude, double longitude, double altitude) {
		super(latitude, longitude);
		assert altitude >= 0;
		
		this.altitude = altitude;
	}
	
	public Position(Coordinate coordinate, double altitude) {
		super(coordinate.getLatitude(), coordinate.getLongitude());
		assert altitude >= 0;
		
		this.altitude = altitude;
	}
		
	public double getAltitude() {
		return altitude;
	}
	
	public double distance(Position other, boolean includeAltitude) {
		return GeoCalculatorSingleton.INSTANCE.getInstance().distance(
				this, 
				other, 
				includeAltitude
				);
	}
	
	public double distance(Position other) {
		return distance(other, true);
	}
	
	
	public Position move(double latitudeDisplacement, double longitudeDisplacement, double altitudeDisplacement) {
		return GeoCalculatorSingleton
				.INSTANCE
				.getInstance()
				.moveByDisplacement(
						this, 
						latitudeDisplacement, 
						longitudeDisplacement, 
						altitudeDisplacement
						);
	}
	
	public Vector3D toVector3D() {
        return new Vector3D(longitude, latitude, altitude);
    }
	
	@Override
	public String toString() {
		return String.format(
				"Position[%s, altitude=%s]", 
				super.toString(),
				altitude
				);
	}
	
	public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (!(obj instanceof Position))
	      return false;
	    Position other = (Position) obj;
	    
	    return Math.abs(this.getLatitude() - other.getLatitude()) < THRESHOLD &&
	         Math.abs(this.getLongitude() - other.getLongitude()) < THRESHOLD &&
	         Math.abs(this.altitude - other.altitude) < THRESHOLD;
	}
}