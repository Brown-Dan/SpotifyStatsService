drop INDEX if exists idx_track_uri;
drop INDEX if exists idx_time_streamed;
drop INDEX if exists idx_username_stream_data;


CREATE INDEX if not exists INDEX_track_uri
    ON stream_data (track_uri);

CREATE INDEX if not exists INDEX_last_streamed
    ON stream_data(date_time);

CREATE INDEX if not exists INDEX_username_stream_data ON stream_data (username);
