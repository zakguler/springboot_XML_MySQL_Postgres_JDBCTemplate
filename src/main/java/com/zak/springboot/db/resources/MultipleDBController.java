package com.zak.springboot.db.resources;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dao")
public class MultipleDBController {

	@Autowired
	@Qualifier("postgresJdbcTemplate")
	private JdbcTemplate postgresTemplate;

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;

	@RequestMapping(value = "/getPGUser")
	public String getPGUser() {
		Map<String, Object> map = new HashMap<String, Object>();
		String query = " select * from usermaster";
		try {
			map = postgresTemplate.queryForMap(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "PostgreSQL Data: " + map.toString();
	}

//	@RequestMapping(value = "/getMYUser")
//	public String getMYUser() {
//		Map<String, Object> map = new HashMap<String, Object>();
//		String query = " select * from usermaster";
//		try {
//			map = mysqlTemplate.queryForMap(query);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "MySQL Data: " + map.toString();
//	}


	@RequestMapping(value = "/getMYUser")
	public String getMYUser() {
		Map<String, Object> map = new HashMap<String, Object>();
		String query = " select * from usermaster";
		try {
			map = mysqlTemplate.queryForMap(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "MySQL Data: " + map.toString();
	}



}
