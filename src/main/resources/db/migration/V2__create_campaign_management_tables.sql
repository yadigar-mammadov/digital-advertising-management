CREATE TABLE campaign_groups (
                                 id BIGSERIAL PRIMARY KEY,
                                 owner_id BIGINT NOT NULL,
                                 name VARCHAR(255) NOT NULL,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL
);

CREATE TABLE campaign_configurations (
                                         id BIGSERIAL PRIMARY KEY,
                                         campaign_group_id BIGINT NOT NULL,
                                         owner_id BIGINT NOT NULL,

                                         objective VARCHAR(100) NOT NULL,
                                         budget_type VARCHAR(50) NOT NULL,
                                         budget_amount NUMERIC(12, 2) NOT NULL,

                                         start_date TIMESTAMP,
                                         end_date TIMESTAMP,

                                         created_at TIMESTAMP NOT NULL,
                                         updated_at TIMESTAMP NOT NULL,

                                         CONSTRAINT fk_campaign_configurations_campaign_group
                                             FOREIGN KEY (campaign_group_id)
                                                 REFERENCES campaign_groups(id)
                                                 ON DELETE CASCADE
);

CREATE TABLE campaigns (
                           id BIGSERIAL PRIMARY KEY,
                           campaign_group_id BIGINT NOT NULL,
                           owner_id BIGINT NOT NULL,

                           platform VARCHAR(50) NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           status VARCHAR(50) NOT NULL,

                           external_campaign_id VARCHAR(255),
                           external_resource_name VARCHAR(500),
                           failure_reason TEXT,

                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL,

                           CONSTRAINT fk_campaigns_campaign_group
                               FOREIGN KEY (campaign_group_id)
                                   REFERENCES campaign_groups(id)
                                   ON DELETE CASCADE
);

CREATE INDEX idx_campaigns_campaign_group_id
    ON campaigns(campaign_group_id);

CREATE INDEX idx_campaigns_owner_id
    ON campaigns(owner_id);

CREATE INDEX idx_campaign_configurations_campaign_group_id
    ON campaign_configurations(campaign_group_id);