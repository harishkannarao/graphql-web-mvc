package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.RawDbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookSort;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookDao {
	private static final String INSERT_SQL = """
		INSERT INTO books(data, created_time, updated_time) \
		VALUES (:data::jsonb, timezone('UTC', now()), timezone('UTC', now()))
		""";
	private static final String UPDATE_SQL = """
		UPDATE books SET data = :data::jsonb, updated_time = timezone('UTC', now()) \
		WHERE data->>'id'::text = :id
		""";
	private static final String DELETE_SQL = """
		DELETE FROM books WHERE data->>'id'::text = :id
		""";
	private static final String SELECT_BY_ID = """
		SELECT data, created_time, updated_time FROM books WHERE data->>'id'::text = :id
		""";
	private static final String SELECT_BY_IDS = """
		SELECT data, created_time, updated_time FROM books WHERE data->>'id'::text in (:ids)
		""";
	private static final String LIST_AND_ORDER_BY = """
		SELECT data, created_time, updated_time FROM books \
		ORDER BY %s NULLS LAST LIMIT :limit OFFSET :offset
		""";
	private static final String PARAM_DATA = "data";
	private static final String PARAM_ID = "id";
	private static final String PARAM_IDS = "ids";
	private static final String PARAM_LIMIT = "limit";
	private static final String PARAM_OFFSET = "offset";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final JdbcClient jdbcClient;
	private final JsonUtil jsonUtil;

	public BookDao(JdbcClient jdbcClient, JsonUtil jsonUtil) {
		this.jdbcClient = jdbcClient;
		this.jsonUtil = jsonUtil;
	}

	public void create(Book book) {
		jdbcClient.sql(INSERT_SQL)
			.param(PARAM_DATA, jsonUtil.toJson(book))
			.update();
	}

	public void update(Book book) {
		jdbcClient.sql(UPDATE_SQL)
			.param(PARAM_DATA, jsonUtil.toJson(book))
			.param(PARAM_ID, book.id())
			.update();
	}

	public void upsert(Book book) {
		try {
			create(book);
		} catch (DuplicateKeyException ex) {
			logger.debug(ex.getMessage(), ex);
			logger.info("Book with id %s already exists, so upsert will be done".formatted(book.id()));
			update(book);
		}
	}

	public Optional<DbEntity<Book>> get(String id) {
		final Optional<RawDbEntity> rawDbEntity = jdbcClient
			.sql(SELECT_BY_ID)
			.param(PARAM_ID, id)
			.query(RawDbEntity.class)
			.optional();

		return rawDbEntity.map(this::createDbEntity);
	}

	public List<DbEntity<Book>> list(List<String> ids) {
		final List<RawDbEntity> rawDbEntities = jdbcClient
			.sql(SELECT_BY_IDS)
			.param(PARAM_IDS, ids)
			.query(RawDbEntity.class)
			.list();
		return rawDbEntities.stream()
			.map(this::createDbEntity)
			.toList();
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	public List<DbEntity<Book>> listOrderedBy(BookSort sort, int offset, int limit) {
		final String orderByColumn = switch (sort) {
			case RATING -> "data->>'rating'::numeric DESC";
			default -> "created_time DESC";
		};
		final List<RawDbEntity> rawDbEntities = jdbcClient
			.sql(LIST_AND_ORDER_BY.formatted(orderByColumn))
			.param(PARAM_LIMIT, limit)
			.param(PARAM_OFFSET, offset)
			.query(RawDbEntity.class)
			.list();
		return rawDbEntities.stream()
			.map(this::createDbEntity)
			.toList();
	}

	public void delete(String id) {
		jdbcClient.sql(DELETE_SQL)
			.param(PARAM_ID, id)
			.update();
	}

	@NotNull
	private DbEntity<Book> createDbEntity(RawDbEntity rawDbEntity) {
		return new DbEntity<>(
			jsonUtil.fromJson(rawDbEntity.data(), Book.class),
			rawDbEntity.createdTime(),
			rawDbEntity.updatedTime()
		);
	}
}
