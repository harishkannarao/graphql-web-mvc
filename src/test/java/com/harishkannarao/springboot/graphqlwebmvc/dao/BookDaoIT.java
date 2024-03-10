package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookDaoIT extends AbstractBaseIT {

	private final BookDao bookDao;

	@Autowired
	public BookDaoIT(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@Test
	public void upsert_and_get_by_id() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID().toString());

		bookDao.upsert(book);
		Book result = bookDao.get(book.id());

		assertThat(result).isEqualTo(book);
	}
}
