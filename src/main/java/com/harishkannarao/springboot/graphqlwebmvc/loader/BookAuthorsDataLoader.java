package com.harishkannarao.springboot.graphqlwebmvc.loader;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
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
public class BookAuthorsDataLoader
	implements BiFunction<Set<Book>, BatchLoaderEnvironment, Mono<Map<Book, List<DbEntity<Author>>>>>{

	private final BookAuthorDao bookAuthorDao;
	private final AuthorDao authorDao;

	public BookAuthorsDataLoader(BookAuthorDao bookAuthorDao, AuthorDao authorDao) {
		this.bookAuthorDao = bookAuthorDao;
		this.authorDao = authorDao;
	}

	@Override
	public Mono<Map<Book, List<DbEntity<Author>>>> apply(
		Set<Book> books, BatchLoaderEnvironment batchLoaderEnvironment
	) {
		return Mono.fromSupplier(() -> listAuthors(books));
	}

	private Map<Book, List<DbEntity<Author>>> listAuthors(final Set<Book> books) {
		List<String> bookIds = books.stream().map(Book::id).toList();
		List<BookAuthor> bookAuthorsList = bookAuthorDao.listByBookIds(bookIds)
			.stream()
			.map(DbEntity::data).toList();
		List<String> authorIds = bookAuthorsList.stream().map(BookAuthor::authorId)
			.collect(Collectors.toSet())
			.stream().toList();

		Map<String, List<BookAuthor>> bookIdAuthorMapping = bookAuthorsList.stream()
			.collect(Collectors.groupingBy(BookAuthor::bookId));
		Map<String, DbEntity<Author>> authorIdMap = authorDao.list(authorIds).stream()
			.collect(Collectors.toUnmodifiableMap(o -> o.data().id(), author -> author));

		return books.stream()
			.map(book -> {
				List<BookAuthor> bookAuthors = Optional.ofNullable(bookIdAuthorMapping.get(book.id()))
					.orElse(Collections.emptyList());
				List<DbEntity<Author>> authors = bookAuthors.stream()
					.map(bookAuthor -> authorIdMap.get(bookAuthor.authorId()))
					.filter(Objects::nonNull)
					.toList();
				return Map.entry(book, authors);
			})
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
