CREATE INDEX idx_track_uri
ON stream_data (track_uri);

CREATE INDEX idx_last_streamed
ON stream_data(date_time)