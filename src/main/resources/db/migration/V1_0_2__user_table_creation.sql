CREATE TABLE IF NOT EXISTS user (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR (50) UNIQUE NOT NULL,
email VARCHAR (100) UNIQUE NOT NULL,
password VARCHAR(100) NOT NULL,
user_role VARCHAR(50) NOT NULL,
FOREIGN KEY (user_role) REFERENCES user_role(role));