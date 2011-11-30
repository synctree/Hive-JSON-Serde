add jar ../../../target/json-serde-1.1.jar;

LOAD DATA LOCAL INPATH 'data.txt' OVERWRITE INTO TABLE  json_test1 ;
