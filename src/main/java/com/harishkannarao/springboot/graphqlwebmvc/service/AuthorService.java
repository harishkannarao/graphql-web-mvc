package com.harishkannarao.springboot.graphqlwebmvc.service;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.AuthorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthorService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final AuthorDao authorDao;

	public AuthorService(AuthorDao authorDao) {
		this.authorDao = authorDao;
	}

	@Transactional
	public Optional<Author> createAuthor(
		AuthorInput authorInput) {
		logger.info("createAuthor authorInput received as {}", authorInput);
		if (authorInput.name().equals("bad-author")) {
			throw new RuntimeException("Bad Author");
		}
		authorDao.create(new Author(authorInput.id(), authorInput.name()));
		Optional<Author> createdAuthor = authorDao.get(authorInput.id()).map(DbEntity::data);
		logger.info("createAuthor authorInput completed for {}", authorInput);
		return createdAuthor;
	}
}
