CREATE TABLE users (
    user_id         VARCHAR2(30)    CONSTRAINT PKID PRIMARY KEY, 
    user_password   VARCHAR2(30)    NOT NULL, 
    user_gender     CHAR(1)         NOT NULL, 
    user_area       VARCHAR2(30)    NOT NULL, 
    user_nickname   VARCHAR2(30)    default 'nick' NOT NULL, 
    search_permit   CHAR(1)         default 'Y' NOT NULL
);

CREATE TABLE friendlist(
	user_id			 VARCHAR2(30)	 NOT NULL,
	friend_id		 VARCHAR2(30)	 NOT NULL,
	friend_permit    CHAR(1)           default 'X' NOT NULL
);

CREATE TABLE messages (
	user_id			 VARCHAR2(30)	  NOT NULL,
    send_id         VARCHAR2(30)     NOT NULL,
	receive_id	     VARCHAR2(30)	  NOT NULL,
	message_content     VARCHAR2(1000), 
    send_date     TIMESTAMP   NOT NULL
);

-- 중복값 확인후 디비 저장
INSERT into friendlist (user_id, friend_id, friend_permit)
select 'test1', 'test2', 'X'
from dual 
where not exists 
(select * from friendlist where user_id = 'test1' and friend_id = 'test2');