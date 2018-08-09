package com.doh.api.jpa_topic.services;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doh.api.jpa_topic.models.Topic;
import com.doh.api.jpa_topic.repositories.TopicRepository;

@Service	//<================= business services are singleton
public class TopicService {

	@Autowired
	private TopicRepository topicRepository;
	
//	private List<Topic> topics = new ArrayList<Topic>(Arrays.asList(
//			new Topic("Spring", "Spring Framework", "Spring framework Description"),
//			new Topic("Java", "Core Java", "Core Java Description"),				
//			new Topic("Javascript", "Javascript", "Javascript Description")
//			));
	
	public List<Topic> getAllTopics(){
//		return topics;
		List<Topic> topics = new ArrayList<>();
		topicRepository.findAll()
			.forEach(topics::add);
		return topics;
	}
	
	public Topic getTopic(String id) {
//		Topic topic = topics.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst().get();
		Topic topic = topicRepository.findById(id).get();
//		Topic topic = topicRepository.findOne(id); //1.3.6 spring boot
		return topic;
	}

	public void addTopic(Topic topic) {
		//topics.add(topic);
		topicRepository.save(topic);
	}
		
	public void updateTopic(String id, Topic topic) {
//		Topic t = topics.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst().get();
//		topics.set(topics.indexOf(t), topic);	
		topicRepository.save(topic);	//<====== save does the add and the update
	}

	public void deleteTopic(String id) {
//		topics.removeIf(e-> e.getId().equalsIgnoreCase(id));
		topicRepository.deleteById(id);
//		topicRepository.delete(id); //1.3.6 spring boot
	}

}
