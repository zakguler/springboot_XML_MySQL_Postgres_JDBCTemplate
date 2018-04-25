package com.zak.springboot.hello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/hello")
public class HelloController {
	
	@GetMapping("/hello")
	public String sayHi() {
		return "MultipleDbApplication : Hi from spring boot";

	}

}
