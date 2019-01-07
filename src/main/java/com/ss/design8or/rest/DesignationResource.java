package com.ss.design8or.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.User;
import com.ss.design8or.service.DesignationService;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/designation")
public class DesignationResource {

	private DesignationService service;

	public DesignationResource(DesignationService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<?> assignManually(@RequestBody User user) {
		service.designate(user);
		return ResponseEntity.ok(user);
	}
}
