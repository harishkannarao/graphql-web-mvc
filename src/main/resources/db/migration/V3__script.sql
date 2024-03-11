CREATE TABLE IF NOT EXISTS books_authors (
    data jsonb,
    created_time timestamptz,
    updated_time timestamptz
);

CREATE UNIQUE INDEX IF NOT EXISTS unique_index_books_authors_id ON books_authors using btree ((cast(data->>'bookId' as text)), (cast(data->>'authorId' as text)));
