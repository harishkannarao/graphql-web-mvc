CREATE TABLE IF NOT EXISTS authors (
    data jsonb,
    created_time timestamptz,
    updated_time timestamptz
);

CREATE UNIQUE INDEX IF NOT EXISTS unique_index_authors_id ON authors using btree ((cast(data->>'id' as text)));
