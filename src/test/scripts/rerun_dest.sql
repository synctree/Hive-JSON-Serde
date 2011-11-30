add jar ../../../target/json-serde-1.1.jar;

INSERT OVERWRITE TABLE simple_table_json
	SELECT name, country,company,amount
	FROM simple_table;
