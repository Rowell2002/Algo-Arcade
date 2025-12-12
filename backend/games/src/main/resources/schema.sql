CREATE DATABASE IF NOT EXISTS tsp_game_db;
USE tsp_game_db;

CREATE TABLE IF NOT EXISTS game_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255),
    home_city VARCHAR(50),
    selected_cities TEXT,
    shortest_path TEXT,
    min_distance INT,
    brute_force_time BIGINT,
    nearest_neighbor_time BIGINT,
    dp_time BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
