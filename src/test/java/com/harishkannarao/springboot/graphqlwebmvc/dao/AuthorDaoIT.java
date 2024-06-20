package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthorDaoIT extends AbstractBaseIT {

	private final AuthorDao authorDao;

	@Autowired
	public AuthorDaoIT(AuthorDao authorDao) {
		this.authorDao = authorDao;
	}

	@Test
	public void create_and_get_by_id() {
		var author = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		authorDao.create(author);
		Optional<DbEntity<Author>> result = authorDao.get(author.id());

		assertThat(result)
			.hasValueSatisfying(entity -> {
				assertThat(entity.data()).isEqualTo(author);
				assertThat(entity.createdTime())
					.isAfterOrEqualTo(Instant.now().minusSeconds(3))
					.isBeforeOrEqualTo(Instant.now().plusSeconds(3));
				assertThat(entity.updatedTime().truncatedTo(ChronoUnit.SECONDS))
					.isAfterOrEqualTo(Instant.now().minusSeconds(3))
					.isBeforeOrEqualTo(Instant.now().plusSeconds(3));
			});
	}

	@Test
	public void create_throws_exception_for_duplicate_entry() {
		var author = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());

		authorDao.create(author);

		assertThrows(DuplicateKeyException.class, () -> authorDao.create(author));
	}

	@Test
	public void upsert_creates_and_updates_author() {
		var author = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());

		authorDao.upsert(author);

		DbEntity<Author> createdAuthor = authorDao.get(author.id()).orElseThrow();
		assertThat(createdAuthor.data()).isEqualTo(author);

		var authorUpdate = new Author(author.id(), "author-" + UUID.randomUUID());

		authorDao.upsert(authorUpdate);

		DbEntity<Author> updatedAuthor = authorDao.get(author.id()).orElseThrow();
		assertThat(updatedAuthor.data()).isEqualTo(authorUpdate);
		assertThat(updatedAuthor.createdTime()).isEqualTo(createdAuthor.createdTime());
		assertThat(updatedAuthor.updatedTime())
			.isAfterOrEqualTo(createdAuthor.createdTime())
			.isBeforeOrEqualTo(Instant.now().plusSeconds(3));
	}

	@Test
	public void update_and_get_by_id() {
		var author = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());

		authorDao.create(author);
		var dbEntityBeforeUpdate = authorDao.get(author.id()).orElseThrow();

		var authorUpdate = new Author(author.id(), "author-" + UUID.randomUUID());

		authorDao.update(authorUpdate);

		Optional<DbEntity<Author>> result = authorDao.get(author.id());

		assertThat(result)
			.hasValueSatisfying(entity -> {
				assertThat(entity.data()).isEqualTo(authorUpdate);
				assertThat(entity.createdTime())
					.isEqualTo(dbEntityBeforeUpdate.createdTime());
				assertThat(entity.updatedTime().truncatedTo(ChronoUnit.SECONDS))
					.isAfterOrEqualTo(Instant.now().minusSeconds(2))
					.isBeforeOrEqualTo(Instant.now().plusSeconds(2));
			});
	}

	@Test
	public void delete_by_id() {
		var author = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());

		authorDao.create(author);

		assertThat(authorDao.get(author.id())).isNotEmpty();

		authorDao.delete(author.id());

		assertThat(authorDao.get(author.id())).isEmpty();
	}

	@Test
	public void list_by_ids_returns_empty_list_given_input_is_empty_list() {
		List<DbEntity<Author>> result = authorDao.list(Collections.emptyList());

		assertThat(result).isEmpty();
	}

	@Test
	public void list_by_ids_returns_entities() {
		var author1 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		var author2 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		var author3 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());

		authorDao.create(author1);
		authorDao.create(author2);
		authorDao.create(author3);

		List<DbEntity<Author>> result = authorDao.list(List.of(author1.id(), author3.id()));

		assertThat(result)
			.anySatisfy(entity -> assertThat(entity.data()).isEqualTo(author1))
			.anySatisfy(entity -> assertThat(entity.data()).isEqualTo(author3))
			.hasSize(2);
	}
}
