package com.zak.springboot.topic.repositories;

import org.springframework.data.repository.CrudRepository;

import com.zak.springboot.topic.models.Topic;

public interface TopicRepository extends CrudRepository<Topic, String>
{ 
	//<Topic, String>... String is the @Id type in Topic class
}
