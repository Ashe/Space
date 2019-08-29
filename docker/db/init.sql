-- Users of the forum
-- @TODO: Encrypt password
-- @TODO: Factor out admin privilages to create multiple layers of authority
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

-- Forum content
-- Discussions, questions etc are all examples of posts
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

-- Tags add value to content when paired with points
-- This value will be used to sort, rank and incentivise content
-- @TODO: Store tag background and font colours
CREATE TABLE tags (
  tag_id BIGSERIAL NOT NULL,
  tag_label VARCHAR(18) NOT NULL,
  PRIMARY KEY (tag_id)
);

-- Posts have many tags and tags can belong to many posts
-- These are base values and its up to the API to calculate the total
-- The base point value comes from the poster's level at that point in time
--   (this also needs to be calculated)
CREATE TABLE post_tags (
  post_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  base_value INT NOT NULL CHECK (base_value > 0) DEFAULT 1,
  PRIMARY KEY (post_id, tag_id),
  FOREIGN KEY (post_id) REFERENCES posts(post_id),
  FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
);

-- Users can praise contributions for points
-- Users can only praise once per tag per contribution
-- Praises are counted to give a user's point totals for future posts
CREATE TABLE praises (
  user_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, post_id, tag_id),
  FOREIGN KEY (post_id, tag_id) REFERENCES post_tags(post_id, tag_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (post_id) REFERENCES posts(post_id),
  FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
);

-- Create an initial admin user
INSERT INTO users (user_nick, username, password, user_bio, is_admin)
VALUES (
  'Space Team', 
  'space', 
  'nebula', 
  '(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ Your friendly neighbourhood Space Team!', 
  true);

-- Create an initial post for the database
INSERT INTO posts (poster_id, post_date, post_title, post_image, post_content)
VALUES (
  (SELECT user_id FROM users WHERE username = 'space'),
  current_timestamp,
  'Welcome to Space!',
  'https://cdn.pixabay.com/photo/2011/12/14/12/17/galaxy-11098_960_720.jpg',
  'Hello there and welcome to Space! If you''re seeing this, 
    Space has been set up correctly and is ready for use!'
);

-- Create some tags
INSERT INTO tags (tag_label)
VALUES 
  ('space'),
  ('clojure'),
  ('reagent'),
  ('re-frame');

-- Associate the above post with these tags
-- @TODO: Neaten this up a bit
INSERT INTO post_tags (post_id, tag_id)
VALUES
  (
    (SELECT post_id FROM posts LIMIT 1), 
    (SELECT tag_id FROM tags WHERE tag_label = 'space')
  ),
  (
    (SELECT post_id FROM posts LIMIT 1), 
    (SELECT tag_id FROM tags WHERE tag_label = 'clojure')
  ),
  (
    (SELECT post_id FROM posts LIMIT 1), 
    (SELECT tag_id FROM tags WHERE tag_label = 'reagent')
  ),
  (
    (SELECT post_id FROM posts LIMIT 1), 
    (SELECT tag_id FROM tags WHERE tag_label = 're-frame')
  );
