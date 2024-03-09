create table streaming_data(
    id uuid primary key,
    username text,
    stream_count int,
    first_stream_date date,
    last_stream_data date
);


create table stream_data(
    streaming_data_id uuid references streaming_data(id),
    date date,
    country varchar(10),
    time_streamed int,
    track_uri text,
    track_name text,
    artist_name text,
    album_name text,
    platform text
)