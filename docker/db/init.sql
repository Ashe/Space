CREATE TABLE Tags (
  TagID bigserial NOT NULL,
  TagLabel varchar(18)
);

CREATE TABLE Users (
  UserID bigserial NOT NULL,
  Username varchar(18) NOT NULL,
  UserHandle varchar(12) NOT NULL UNIQUE,
  JoinDate timestamptz NOT NULL,
  IsAdmin bool NOT NULL,
  PRIMARY KEY (UserID)
);

CREATE TABLE Posts (
  PostID bigserial NOT NULL,
  PosterID bigint,
  PostDate timestamptz NOT NULL DEFAULT NOW(),
  PostTitle varchar(100) NOT NULL,
  PostContent text NOT NULL,
  IsAnonymous bool,
  PRIMARY KEY (PostID),
  FOREIGN KEY (PosterID) REFERENCES Users(UserID)
);

-- Create an initial admin user
INSERT INTO Users (Username, UserHandle, JoinDate, IsAdmin)
VALUES ('Space Team', 'space', current_timestamp, true);

-- Create an initial post for the database
INSERT INTO Posts (PosterID, PostDate, PostTitle, PostContent)
VALUES (
  (SELECT UserId FROM Users WHERE UserHandle = 'space'),
  current_timestamp,
  'Welcome to Space!',
  'Hello there and welcome to Space! If you''re seeing this, Space has been set up correctly and is ready for use!'
);

