CREATE USER space WITH PASSWORD 'nebula';
CREATE DATABASE space;
GRANT ALL PRIVILEGES ON DATABASE space TO space;
\connect space

CREATE TABLE Users (
  UserID bigserial NOT NULL,
  Username varchar(18) NOT NULL,
  UserHandle varchar(12) NOT NULL,
  JoinDate timestamptz NOT NULL,
  IsAdmin bool NOT NULL,
  PRIMARY KEY (UserID)
);

CREATE TABLE Posts (
  PostID bigserial NOT NULL,
  PosterID bigserial NOT NULL,
  PostDate timestamptz NOT NULL,
  Content text NOT NULL,
  PRIMARY KEY (PostID),
  FOREIGN KEY (PosterID) REFERENCES Users(UserID)
);
