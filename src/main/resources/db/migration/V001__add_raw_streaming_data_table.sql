create table streaming_data(
    username text primary key,
    stream_count int not null,
    first_stream_date timestamp not null,
    last_stream_data timestamp not null
);


create table stream_data(
    username text references streaming_data(username),
    date timestamp not null,
    country varchar(10),
    time_streamed int,
    track_uri text,
    track_name text,
    artist_name text,
    album_name text,
    platform text
)