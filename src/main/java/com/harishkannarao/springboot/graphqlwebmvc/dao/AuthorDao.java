package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.RawDbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
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
public class AuthorDao {
	private static final String INSERT_SQL = """
		INSERT INTO authors(data, created_time, updated_time) \
		VALUES (:data::jsonb, timezone('UTC', now()), timezone('UTC', now()))
		""";
	private static final String UPDATE_SQL = """
		UPDATE authors SET data = :data::jsonb, updated_time = timezone('UTC', now()) \
		WHERE data->>'id'::text = :id
		""";
	private static final String DELETE_SQL = """
		DELETE FROM authors WHERE data->>'id'::text = :id
		""";
	private static final String SELECT_BY_ID = """
		SELECT data, created_time, updated_time FROM authors WHERE data->>'id'::text = :id
		""";
	private static final String SELECT_BY_IDS = """
		SELECT data, created_time, updated_time FROM authors WHERE data->>'id'::text in (:ids)
		""";
	private static final String PARAM_DATA = "data";
	private static final String PARAM_ID = "id";
	private static final String PARAM_IDS = "ids";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final JdbcClient jdbcClient;
	private final JsonUtil jsonUtil;

	public AuthorDao(JdbcClient jdbcClient, JsonUtil jsonUtil) {
		this.jdbcClient = jdbcClient;
		this.jsonUtil = jsonUtil;
	}

	public void create(Author author) {
		jdbcClient.sql(INSERT_SQL)
			.param(PARAM_DATA, jsonUtil.toJson(author))
			.update();
	}

	public void update(Author author) {
		jdbcClient.sql(UPDATE_SQL)
			.param(PARAM_DATA, jsonUtil.toJson(author))
			.param(PARAM_ID, author.id())
			.update();
	}

	public void upsert(Author author) {
		try {
			create(author);
		} catch (DuplicateKeyException ex) {
			logger.debug(ex.getMessage(), ex);
			logger.info("Author with id %s already exists, so upsert will be done".formatted(author.id()));
			update(author);
		}
	}

	public Optional<DbEntity<Author>> get(String id) {
		final Optional<RawDbEntity> rawDbEntity = jdbcClient
			.sql(SELECT_BY_ID)
			.param(PARAM_ID, id)
			.query(RawDbEntity.class)
			.optional();

		return rawDbEntity.map(this::createDbEntity);
	}

	public List<DbEntity<Author>> list(List<String> ids) {
		final List<RawDbEntity> rawDbEntities = jdbcClient
			.sql(SELECT_BY_IDS)
			.param(PARAM_IDS, ids)
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
	private DbEntity<Author> createDbEntity(RawDbEntity rawDbEntity) {
		return new DbEntity<>(
			jsonUtil.fromJson(rawDbEntity.data(), Author.class),
			rawDbEntity.createdTime(),
			rawDbEntity.updatedTime()
		);
	}
}
