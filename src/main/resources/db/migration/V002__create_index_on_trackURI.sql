CREATE INDEX if not exists idx_track_uri
ON stream_data (track_uri);

CREATE INDEX if not exists idx_last_streamed
ON stream_data(date_time)