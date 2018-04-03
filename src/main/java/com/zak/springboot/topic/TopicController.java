package com.zak.springboot.topic;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {

	@Autowired
	private TopicService topicService;
	
// http://localhost:8080/topics
	
	@RequestMapping("/topics")	//<==================================== "GET" request
	public List<Topic> getAllTopics() {
		return topicService.getAllTopics();				
	}
		
	
// http://localhost:8080/topics/spring
	
//	@RequestMapping("/topics/{foo}")
//	public Topic getTopic(@PathVariable("foo") String id) {		
	@RequestMapping("/topics/{id}")
	public Topic getTopic(@PathVariable String id) {		
		return topicService.getTopic(id);	
	}

	@RequestMapping(method=RequestMethod.POST, value="/topics") //<==== "POST" add new request
	public void addTopic(@RequestBody Topic topic) {
		topicService.addTopic(topic);
	}
	

	@RequestMapping(method=RequestMethod.PUT, value="/topics/{id}") //<==== "PUT" update request
	public void addTopic(@RequestBody Topic topic, @PathVariable String id) {
		topicService.updateTopic(id, topic);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/topics/{id}") //<==== "DELETE" delete request
	public void deleteTopic(@PathVariable String id) {
		topicService.deleteTopic(id);
	}
	
}
