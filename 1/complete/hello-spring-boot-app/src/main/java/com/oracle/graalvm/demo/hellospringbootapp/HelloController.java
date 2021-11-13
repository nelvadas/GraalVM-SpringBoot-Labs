package com.oracle.graalvm.demo.hellospringbootapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String index() {
		return "Welcome to GraalVM Spring Boot HOL!\n";
	}

}
