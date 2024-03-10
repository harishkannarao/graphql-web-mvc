package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.DbEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
		var referenceStartTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		bookDao.create(book);
		var referenceEndTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		DbEntity<Book> result = bookDao.get(book.id());
		assertThat(result.data()).isEqualTo(book);
		assertThat(result.createdTime().truncatedTo(ChronoUnit.SECONDS))
			.isAfterOrEqualTo(referenceStartTime)
			.isBeforeOrEqualTo(referenceEndTime);
		assertThat(result.updatedTime().truncatedTo(ChronoUnit.SECONDS))
			.isAfterOrEqualTo(referenceStartTime)
			.isBeforeOrEqualTo(referenceEndTime);
	}

	@Test
	public void create_throws_exception_for_duplicate_entry() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());
		bookDao.create(book);

		assertThrows(DuplicateKeyException.class, () -> bookDao.create(book));
	}
}
