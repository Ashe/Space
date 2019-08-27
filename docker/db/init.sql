CREATE TABLE tags (
  tag_id bigserial NOT NULL,
  taglabel varchar(18)
);

CREATE TABLE users (
  user_id BIGSERIAL NOT NULL,
  user_nick VARCHAR(18) NOT NULL,
  username VARCHAR(12) NOT NULL UNIQUE,
  password VARCHAR(50) NOT NULL,
  user_bio VARCHAR(350),
  user_image TEXT NOT NULL DEFAULT 
    'https://cdn.pixabay.com/photo/2018/10/16/09/55/astronaut-3751046_960_720.png',
  join_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  is_admin bool NOT NULL,
  PRIMARY KEY (user_id)
);

CREATE TABLE posts (
  post_id BIGSERIAL NOT NULL,
  poster_id BIGINT,
  post_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  post_title VARCHAR(100) NOT NULL,
  post_summary VARCHAR(100),
  post_content TEXT,
  post_image TEXT,
  is_anonymous BOOL,
  PRIMARY KEY (post_id),
  FOREIGN KEY (poster_id) REFERENCES users(user_id)
);

-- Create an initial admin user
INSERT INTO users (user_nick, username, password, user_bio, is_admin)
VALUES ('Space Team', 'space', 'nebula', '(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ Your friendly neighbourhood Space Team!', true);

-- Create an initial post for the database
INSERT INTO posts (poster_id, post_date, post_title, post_image, post_content)
VALUES (
  (SELECT user_id FROM users WHERE username = 'space'),
  current_timestamp,
  'Welcome to Space!',
  'https://cdn.pixabay.com/photo/2011/12/14/12/17/galaxy-11098_960_720.jpg',
  'Hello there and welcome to Space! If you''re seeing this, Space has been set up correctly and is ready for use!'
);

