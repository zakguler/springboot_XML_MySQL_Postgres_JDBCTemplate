package com.zak.springboot.topic.resources;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zak.springboot.topic.models.Topic;
import com.zak.springboot.topic.services.TopicService;

@RestController
@RequestMapping("/topic")
public class TopicResource {

	@Autowired
	private TopicService topicService;
	
	// http://localhost:5151/topics
	
	@GetMapping("/topics")
	public List<Topic> getAllTopics() {
		return topicService.getAllTopics();				
	}
		
	
	// http://localhost:8080/topics/Javascript
//	public Topic getTopic(@PathVariable("foo") String id) {		
	@GetMapping("/topics/{id}")
	public @ResponseBody ResponseEntity<Topic> getTopic(@PathVariable String id) {		
		return new ResponseEntity<Topic>(topicService.getTopic(id), HttpStatus.OK);
	}

	@PostMapping("/topics") //<==== "POST" ADD new request
	public ResponseEntity<Object> addTopic(@RequestBody Topic topic) {
		topicService.addTopic(topic);
		return new ResponseEntity<Object>("POST Response", HttpStatus.OK);
	}
	

//	@RequestMapping(method=RequestMethod.PUT, value="/topics/{id}") //<==== "PUT" update request
	@PutMapping("/topics/{id}") //<==== "PUT" update request
	public void updateTopic(@RequestBody Topic topic, @PathVariable String id) {
		topicService.updateTopic(id, topic);
	}
	
	@DeleteMapping("/topics/{id}") //<==== "DELETE" delete request
	public void deleteTopic(@PathVariable String id) {
		topicService.deleteTopic(id);
	}
	
}
