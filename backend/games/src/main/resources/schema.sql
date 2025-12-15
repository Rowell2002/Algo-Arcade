CREATE DATABASE IF NOT EXISTS mini_games_db;
USE mini_games_db;


-- Snake & Ladder Game Results

CREATE TABLE IF NOT EXISTS snake_ladder_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255),
    board_size INT NOT NULL,
    min_dice_throws INT NOT NULL,  -- Correct answer (optimal moves)
    user_guess INT NOT NULL,        -- Player's guess
    bfs_time BIGINT,                -- BFS algorithm time (microseconds)
    dijkstra_time BIGINT,           -- Dijkstra algorithm time (microseconds)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- Traffic Simulation Game Results

CREATE TABLE IF NOT EXISTS traffic_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255),
    max_flow INT NOT NULL,          -- Correct answer (max flow)
    user_guess INT NOT NULL,        -- Player's guess
    ford_fulkerson_time BIGINT,     -- Ford-Fulkerson algorithm time (microseconds)
    edmonds_karp_time BIGINT,       -- Edmonds-Karp algorithm time (microseconds)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- Traveling Salesman Problem (TSP) Game Results

CREATE TABLE IF NOT EXISTS tsp_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255),
    home_city VARCHAR(50),
    selected_cities TEXT,           -- Cities visited (comma-separated)
    optimal_path TEXT,              -- Optimal path (e.g., "A -> B -> C -> A")
    min_distance INT NOT NULL,      -- Optimal distance (km)
    user_distance INT NOT NULL,     -- User's calculated distance (km)
    brute_force_time_ns BIGINT,     -- Brute Force time (nanoseconds)
    nearest_neighbor_time_ns BIGINT,-- Nearest Neighbor time (nanoseconds)
    dynamic_programming_time_ns BIGINT, -- DP (Held-Karp) time (nanoseconds)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- Tower of Hanoi Game Results

CREATE TABLE IF NOT EXISTS hanoi_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255),
    num_disks INT NOT NULL,         -- Number of disks (5-10)
    num_pegs INT NOT NULL,          -- Number of pegs (3 or 4)
    user_min_moves INT NOT NULL,    -- User's estimated moves
    user_sequence TEXT,             -- User's move sequence
    optimal_min_moves INT NOT NULL, -- Correct minimum moves
    algo1_name VARCHAR(50),         -- Algorithm 1 name (Recursive/Frame-Stewart)
    algo1_time BIGINT,              -- Algorithm 1 time (microseconds)
    algo2_name VARCHAR(50),         -- Algorithm 2 name (Iterative/BFS)
    algo2_time BIGINT,              -- Algorithm 2 time (microseconds)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- Eight Queens - Discovered Solutions

CREATE TABLE IF NOT EXISTS eight_queens_solutions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    solution_string VARCHAR(8) UNIQUE NOT NULL, -- Canonical solution (e.g., "04752613")
    discovered_by VARCHAR(255),     -- Player who discovered this solution
    discovered_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- Eight Queens - Algorithm Statistics

CREATE TABLE IF NOT EXISTS eight_queens_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sequential_time_ns BIGINT,      -- Sequential backtracking time (nanoseconds)
    threaded_time_ns BIGINT,        -- Threaded backtracking time (nanoseconds)
    run_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
