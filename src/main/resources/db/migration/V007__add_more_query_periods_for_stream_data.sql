ALTER TABLE "StreamingData".stream_data ADD COLUMN if not exists day_of_the_week smallint;
ALTER TABLE "StreamingData".stream_data ADD COLUMN if not exists month smallint;
ALTER TABLE "StreamingData".stream_data ADD COLUMN if not exists year smallint;

CREATE INDEX if not exists idx_day_of_the_week ON stream_data (day_of_the_week);
CREATE INDEX if not exists idx_month ON stream_data (month);
CREATE INDEX if not exists idx_year ON stream_data (year);
