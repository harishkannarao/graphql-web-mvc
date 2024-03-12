package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookAuthorDaoIT extends AbstractBaseIT {

	private final BookAuthorDao bookAuthorDao;

	@Autowired
	public BookAuthorDaoIT(BookAuthorDao bookAuthorDao) {
		this.bookAuthorDao = bookAuthorDao;
	}

	@Test
	public void list_by_book_ids() {
		var entity1 = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		var entity2 = new BookAuthor(entity1.bookId(), UUID.randomUUID().toString());
		var entity3 = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		var entity4 = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		bookAuthorDao.create(entity1);
		bookAuthorDao.create(entity2);
		bookAuthorDao.create(entity3);
		bookAuthorDao.create(entity4);

		List<BookAuthor> result = bookAuthorDao.listByBookIds(List.of(entity1.bookId(), entity3.bookId()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(result)
			.contains(entity1, entity2, entity3)
			.hasSize(3);
	}

	@Test
	public void list_by_book_ids_returns_empty_for_unknown_id() {
		List<BookAuthor> result = bookAuthorDao.listByBookIds(List.of(UUID.randomUUID().toString()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(result)
			.isEmpty();
	}

	@Test
	public void list_by_author_ids() {
		var entity1 = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		var entity2 = new BookAuthor(UUID.randomUUID().toString(), entity1.authorId());
		var entity3 = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		var entity4 = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		bookAuthorDao.create(entity1);
		bookAuthorDao.create(entity2);
		bookAuthorDao.create(entity3);
		bookAuthorDao.create(entity4);

		List<BookAuthor> result = bookAuthorDao
			.listByAuthorIds(List.of(entity1.authorId(), entity3.authorId()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(result)
			.contains(entity1, entity2, entity3)
			.hasSize(3);
	}

	@Test
	public void list_by_author_ids_returns_empty_for_unknown_id() {
		List<BookAuthor> result = bookAuthorDao.listByAuthorIds(List.of(UUID.randomUUID().toString()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(result)
			.isEmpty();
	}

	@Test
	public void create_is_idempotent() {
		var entity = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		bookAuthorDao.create(entity);

		List<BookAuthor> result = bookAuthorDao.listByBookIds(List.of(entity.bookId()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(result)
			.contains(entity)
			.hasSize(1);

		bookAuthorDao.create(entity);

		List<BookAuthor> secondResult = bookAuthorDao.listByBookIds(List.of(entity.bookId()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(secondResult)
			.contains(entity)
			.hasSize(1);
	}

	@Test
	public void deletes_the_enty() {
		var entity = new BookAuthor(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		bookAuthorDao.create(entity);

		List<BookAuthor> result = bookAuthorDao.listByBookIds(List.of(entity.bookId()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(result)
			.contains(entity)
			.hasSize(1);

		bookAuthorDao.delete(entity.bookId(), entity.authorId());

		List<BookAuthor> secondResult = bookAuthorDao.listByBookIds(List.of(entity.bookId()))
			.stream()
			.map(DbEntity::data)
			.toList();

		assertThat(secondResult)
			.isEmpty();
	}
}
