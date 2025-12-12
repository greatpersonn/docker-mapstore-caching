CREATE TABLE IF NOT EXISTS counters (id INT PRIMARY KEY, value INT NOT NULL);

INSERT INTO counters (id, value) VALUES (1, 0), (2, 0), (3, 0), (4, 0) ON CONFLICT (id) DO NOTHING;
