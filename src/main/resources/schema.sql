
CREATE TABLE IF NOT EXISTS users (
user_id INT PRIMARY KEY AUTO_INCREMENT,
user_name VARCHAR(70) NOT NULL,
login VARCHAR(70) NOT NULL,
email VARCHAR(70) NOT NULL,
birthday DATE
);

CREATE TABLE IF NOT EXISTS genres (
genre_id INT PRIMARY KEY  AUTO_INCREMENT,
genre_name VARCHAR (100) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
mpa_id INT PRIMARY KEY AUTO_INCREMENT,
mpa_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
film_id INT PRIMARY KEY AUTO_INCREMENT,
film_name VARCHAR(70) NOT NULL,
description VARCHAR(250),
released DATE,
duration INT,
mpa_id INT,
FOREIGN KEY (mpa_id)  REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS film_genres(
film_id INT,
genre_id INT,
FOREIGN KEY (film_id) REFERENCES films(film_id),
FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
film_id INT,
user_id INT,
FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends (
user_id INT,
friend_id INT,
FOREIGN KEY (user_id) REFERENCES users(user_id),
FOREIGN KEY (friend_id) REFERENCES users(user_id)
);



