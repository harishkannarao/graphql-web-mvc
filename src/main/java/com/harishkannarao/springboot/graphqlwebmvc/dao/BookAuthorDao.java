package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.RawDbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookAuthorDao {
	private static final String INSERT_SQL = """
		INSERT INTO books_authors(data, created_time, updated_time) \
		VALUES (:data::jsonb, timezone('UTC', now()), timezone('UTC', now()))
		""";
	private static final String DELETE_SQL = """
		DELETE FROM books_authors \
		WHERE data->>'bookId'::text = :bookId and data->>'authorId'::text = :authorId
		""";

	private static final String SELECT_BY_BOOK_IDS = """
		SELECT data, created_time, updated_time \
		FROM books_authors WHERE data->>'bookId'::text in (:bookIds)
		""";

	private static final String SELECT_BY_AUTHOR_IDS = """
		SELECT data, created_time, updated_time \
		FROM books_authors WHERE data->>'authorId'::text in (:authorIds)
		""";
	private static final String PARAM_DATA = "data";
	private static final String PARAM_BOOK_ID = "bookId";
	private static final String PARAM_AUTHOR_ID = "authorId";
	private static final String PARAM_BOOK_IDS = "bookIds";
	private static final String PARAM_AUTHOR_IDS = "authorIds";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final JdbcClient jdbcClient;
	private final JsonUtil jsonUtil;

	public BookAuthorDao(JdbcClient jdbcClient, JsonUtil jsonUtil) {
		this.jdbcClient = jdbcClient;
		this.jsonUtil = jsonUtil;
	}

	public void create(BookAuthor bookAuthor) {
		try {
			jdbcClient.sql(INSERT_SQL)
				.param(PARAM_DATA, jsonUtil.toJson(bookAuthor))
				.update();
		} catch (DuplicateKeyException ex) {
			logger.debug(ex.getMessage(), ex);
			logger.info("Entry with book id %s and author id %s already exists"
				.formatted(bookAuthor.bookId(), bookAuthor.authorId()));
		}
	}

	public List<DbEntity<BookAuthor>> listByBookIds(List<String> bookIds) {
		final List<RawDbEntity> rawDbEntities = jdbcClient
			.sql(SELECT_BY_BOOK_IDS)
			.param(PARAM_BOOK_IDS, bookIds)
			.query(RawDbEntity.class)
			.list();
		return rawDbEntities.stream()
			.map(this::createDbEntity)
			.toList();
	}

	public List<DbEntity<BookAuthor>> listByAuthorIds(List<String> authorIds) {
		final List<RawDbEntity> rawDbEntities = jdbcClient
			.sql(SELECT_BY_AUTHOR_IDS)
			.param(PARAM_AUTHOR_IDS, authorIds)
			.query(RawDbEntity.class)
			.list();
		return rawDbEntities.stream()
			.map(this::createDbEntity)
			.toList();
	}

	public void delete(String bookId, String authorId) {
		jdbcClient.sql(DELETE_SQL)
			.param(PARAM_BOOK_ID, bookId)
			.param(PARAM_AUTHOR_ID, authorId)
			.update();
	}

	@NotNull
	private DbEntity<BookAuthor> createDbEntity(RawDbEntity rawDbEntity) {
		return new DbEntity<>(
			jsonUtil.fromJson(rawDbEntity.data(), BookAuthor.class),
			rawDbEntity.createdTime(),
			rawDbEntity.updatedTime()
		);
	}
}
