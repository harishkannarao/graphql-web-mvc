package com.harishkannarao.springboot.graphqlwebmvc.loader;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class AuthorBooksDataLoader
	implements BiFunction<Set<Author>, BatchLoaderEnvironment, Mono<Map<Author, List<DbEntity<Book>>>>> {

	private final BookAuthorDao bookAuthorDao;
	private final BookDao bookDao;

	public AuthorBooksDataLoader(BookAuthorDao bookAuthorDao, BookDao bookDao) {
		this.bookAuthorDao = bookAuthorDao;
		this.bookDao = bookDao;
	}

	@Override
	public Mono<Map<Author, List<DbEntity<Book>>>> apply(
		Set<Author> authors, BatchLoaderEnvironment batchLoaderEnvironment
	) {
		return Mono.fromSupplier(() -> listBooks(authors));
	}

	private Map<Author, List<DbEntity<Book>>> listBooks(final Set<Author> authors) {
		List<String> authorIds = authors.stream().map(Author::id).toList();
		List<BookAuthor> bookAuthorsList = bookAuthorDao.listByAuthorIds(authorIds)
			.stream()
			.map(DbEntity::data).toList();
		List<String> bookIds = bookAuthorsList.stream().map(BookAuthor::bookId)
			.collect(Collectors.toSet())
			.stream().toList();

		Map<String, List<BookAuthor>> authorIdBooksMapping = bookAuthorsList.stream()
			.collect(Collectors.groupingBy(BookAuthor::authorId));
		Map<String, DbEntity<Book>> bookIdMap = bookDao.list(bookIds).stream()
			.collect(Collectors.toUnmodifiableMap(o -> o.data().id(), book -> book));

		return authors.stream()
			.map(author -> {
				List<BookAuthor> bookAuthors = Optional.ofNullable(authorIdBooksMapping.get(author.id()))
					.orElse(Collections.emptyList());
				List<DbEntity<Book>> books = bookAuthors.stream()
					.map(bookAuthor -> bookIdMap.get(bookAuthor.bookId()))
					.filter(Objects::nonNull)
					.toList();
				return Map.entry(author, books);
			})
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
