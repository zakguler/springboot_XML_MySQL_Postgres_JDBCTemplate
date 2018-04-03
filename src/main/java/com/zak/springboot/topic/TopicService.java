package com.zak.springboot.topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service	//<================= business services are singleton
public class TopicService {

	private List<Topic> topics = new ArrayList<Topic>(Arrays.asList(
			new Topic("Spring", "Spring Framework", "Spring framework Description"),
			new Topic("Java", "Core Java", "Core Java Description"),				
			new Topic("Javascript", "Javascript", "Javascript Description")
			));
	
	public List<Topic> getAllTopics(){
		return topics;
	}
	
	public Topic getTopic(String id) {
		Topic topic = topics.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst().get();
		return topic;
	}

	public void addTopic(Topic topic) {
		topics.add(topic);	
	}
		
	public void updateTopic(String id, Topic topic) {
		Topic t = topics.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst().get();
		topics.set(topics.indexOf(t), topic);	
	}

	public void deleteTopic(String id) {
		topics.removeIf(e-> e.getId().equalsIgnoreCase(id));		
	}

}
