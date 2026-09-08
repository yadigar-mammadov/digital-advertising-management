CREATE TABLE outbox_event (
                              id UUID PRIMARY KEY,
                              aggregate_id BIGINT NOT NULL,
                              event_type VARCHAR(100) NOT NULL,
                              payload TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              published_at TIMESTAMP NULL
);