package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
		Optional<DbEntity<Book>> result = bookDao.get(book.id());

		assertThat(result)
			.hasValueSatisfying(entity -> {
				assertThat(entity.data()).isEqualTo(book);
				assertThat(entity.createdTime())
					.isAfterOrEqualTo(Instant.now().minusSeconds(2))
					.isBeforeOrEqualTo(Instant.now().plusSeconds(2));
				assertThat(entity.updatedTime().truncatedTo(ChronoUnit.SECONDS))
					.isAfterOrEqualTo(Instant.now().minusSeconds(2))
					.isBeforeOrEqualTo(Instant.now().plusSeconds(2));
			});
	}

	@Test
	public void create_throws_exception_for_duplicate_entry() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());

		bookDao.create(book);

		assertThrows(DuplicateKeyException.class, () -> bookDao.create(book));
	}

	@Test
	public void upsert_creates_and_updates_book() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());

		bookDao.upsert(book);

		DbEntity<Book> createdBook = bookDao.get(book.id()).orElseThrow();
		assertThat(createdBook.data()).isEqualTo(book);

		var bookUpdate = new Book(book.id(), "book-" + UUID.randomUUID());

		bookDao.upsert(bookUpdate);

		DbEntity<Book> updatedBook = bookDao.get(book.id()).orElseThrow();
		assertThat(updatedBook.data()).isEqualTo(bookUpdate);
		assertThat(updatedBook.createdTime()).isEqualTo(createdBook.createdTime());
		assertThat(updatedBook.updatedTime())
			.isAfterOrEqualTo(createdBook.createdTime())
			.isBeforeOrEqualTo(Instant.now().plusSeconds(2));
	}

	@Test
	public void update_and_get_by_id() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());

		bookDao.create(book);
		var dbEntityBeforeUpdate = bookDao.get(book.id()).orElseThrow();

		var bookUpdate = new Book(book.id(), "book-" + UUID.randomUUID());

		bookDao.update(bookUpdate);

		Optional<DbEntity<Book>> result = bookDao.get(book.id());

		assertThat(result)
			.hasValueSatisfying(entity -> {
				assertThat(entity.data()).isEqualTo(bookUpdate);
				assertThat(entity.createdTime())
					.isEqualTo(dbEntityBeforeUpdate.createdTime());
				assertThat(entity.updatedTime().truncatedTo(ChronoUnit.SECONDS))
					.isAfterOrEqualTo(Instant.now().minusSeconds(2))
					.isBeforeOrEqualTo(Instant.now().plusSeconds(2));
			});
	}

	@Test
	public void delete_by_id() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());

		bookDao.create(book);

		assertThat(bookDao.get(book.id())).isNotEmpty();

		bookDao.delete(book.id());

		assertThat(bookDao.get(book.id())).isEmpty();
	}

	@Test
	public void list_by_ids_returns_entities() {
		var book1 = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());
		var book2 = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());
		var book3 = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID());

		bookDao.create(book1);
		bookDao.create(book2);
		bookDao.create(book3);

		List<DbEntity<Book>> result = bookDao.list(List.of(book1.id(), book3.id()));

		assertThat(result)
			.anySatisfy(entity -> assertThat(entity.data()).isEqualTo(book1))
			.anySatisfy(entity -> assertThat(entity.data()).isEqualTo(book3))
			.hasSize(2);
	}
}
