USE data_quality;

INSERT INTO data_quality.source_connection(
  connection_name,source_type,connection_info
) VALUES(
  "conn","hive","host=localhost&port=3307..."
);


USE note;
CREATE TABLE IF NOT EXISTS noteuser (
   user_name VARCHAR(20),
   user_pass VARCHAR(50),
   ds VARCHAR(20)
)
CREATE TABLE IF NOT EXISTS noteshow (
   show_name VARCHAR(20),
   show_info VARCHAR(50),
   ds VARCHAR(20)
)
INSERT INTO noteshow(show_name,show_info,ds) VALUES ("zizi","sfsdf","20180122");
INSERT INTO noteshow(show_name,show_info,ds) VALUES ("zidi","sfssdf","20180122");
INSERT INTO noteshow(show_name,show_info,ds) VALUES ("zisi","sfssdf","20180123");
USE text;
CREATE TABLE IF NOT EXISTS textuser (
   user_name VARCHAR(20),
   user_pass VARCHAR(50),
   ds VARCHAR(20)
)
CREATE TABLE IF NOT EXISTS textshow (
   show_name VARCHAR(20),
   show_info VARCHAR(50),
   ds VARCHAR(20)
)
SELECT DATEDIFF(created_at,getdate()) AS DiffDate
SELECT NOW()
select datediff(NOW(),created_at)>=3 AS datediff FROM alarm

SELECT * FROM alarm WHERE
        created_at IN (SELECT MAX(created_at) FROM alarm WHERE rule_id = '5f2a0a5d-0001-11e8-a997-0242ac110004')

INSERT INTO user_email(user_email_id,user_chinese_name,user_english_name,email) VALUES
  ("1199","wangtengfei","evan","evan@jollycorp.com");