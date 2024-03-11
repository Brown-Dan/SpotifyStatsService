CREATE INDEX track_uri
ON stream_data (track_uri);

CREATE INDEX last_streamed
ON stream_data(date_time)