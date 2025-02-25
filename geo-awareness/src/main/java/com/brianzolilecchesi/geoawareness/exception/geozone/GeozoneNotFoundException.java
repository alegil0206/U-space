package com.brianzolilecchesi.geoawareness.exception.geozone;

import com.brianzolilecchesi.geoawareness.exception.NotFoundException;

public class GeozoneNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 1L;
	
	public GeozoneNotFoundException() {
		super();
	}
	
	public GeozoneNotFoundException(String id) {
		super(String.format("Geozone with id %s not found", id));
	}
}