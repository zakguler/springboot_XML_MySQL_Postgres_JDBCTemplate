package com.doh.api.jpa_topic.resources;



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

import com.doh.api.jpa_topic.models.Topic;
import com.doh.api.jpa_topic.services.TopicService;

@RestController
@RequestMapping("/topics")
public class TopicController {

	@Autowired
	private TopicService topicService;
	
	// http://localhost:5151/topics
	
	@GetMapping()
	public List<Topic> getAllTopics() {
		return topicService.getAllTopics();				
	}
		
	//	public Topic getTopic(@PathVariable("foo") String id) {		
	@GetMapping("/{id}")
	public @ResponseBody ResponseEntity<Topic> getTopic(@PathVariable String id) {		
		return new ResponseEntity<Topic>(topicService.getTopic(id), HttpStatus.OK);
	}

	@PostMapping() //<==== "POST" ADD new request
	public ResponseEntity<Object> addTopic(@RequestBody Topic topic) {
		topicService.addTopic(topic);
		return new ResponseEntity<Object>("POST Response", HttpStatus.OK);
	}
	

	@PutMapping("/{id}") //<==== "PUT" update request
	public void updateTopic(@RequestBody Topic topic, @PathVariable String id) {
		topicService.updateTopic(id, topic);
	}
	
	@DeleteMapping("/{id}") //<==== "DELETE" delete request
	public void deleteTopic(@PathVariable String id) {
		topicService.deleteTopic(id);
	}
	
}
