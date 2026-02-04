package com.example.bookportal.service;

import com.example.bookportal.entity.Publisher;

import java.util.List;

public interface PublisherService {

    List<Publisher> getAllPublishers();

    Publisher getPublisherById(Long id);
}

