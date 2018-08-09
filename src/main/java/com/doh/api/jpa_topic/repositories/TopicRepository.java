package com.doh.api.jpa_topic.repositories;

import org.springframework.data.repository.CrudRepository;

import com.doh.api.jpa_topic.models.Topic;

public interface TopicRepository extends CrudRepository<Topic, String>
{ 
	//<Topic, String>... String is the @Id type in Topic class
}
