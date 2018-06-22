package com.zak.springboot.hello.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/hello")
public class HelloController {
	
	@GetMapping("/hello")
	public String sayHi() {
		return "JDBCTemplate... MultipleDbApplication : Hi from spring boot";

	}

}
