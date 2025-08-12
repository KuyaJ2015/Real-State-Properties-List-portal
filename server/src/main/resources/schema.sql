-- Drop table if it exists
DROP TABLE IF EXISTS property;

-- Create property table
CREATE TABLE property (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    description VARCHAR(1000),
    image_url VARCHAR(255)
);
