CREATE TABLE IF NOT EXISTS books (
    data jsonb,
    created_time timestamptz,
    updated_time timestamptz
);

CREATE UNIQUE INDEX IF NOT EXISTS unique_index_books_id ON books using btree ((cast(data->>'id' as text)));
