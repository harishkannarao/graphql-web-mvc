package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookDaoIT extends AbstractBaseIT {

	private final BookDao bookDao;

	@Autowired
	public BookDaoIT(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@Test
	public void create_and_get_by_id() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());

		bookDao.create(book);
		Book result = bookDao.get(book.id());
		assertThat(result).isEqualTo(book);
	}

	@Test
	public void create_throws_exception_for_duplicate_entry() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());
		bookDao.create(book);

		assertThrows(DuplicateKeyException.class, () -> bookDao.create(book));
	}
}
