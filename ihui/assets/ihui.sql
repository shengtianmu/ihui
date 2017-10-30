DROP TABLE IF EXISTS user;

DROP TABLE IF EXISTS wallet;

DROP TABLE IF EXISTS adv;

DROP TABLE IF EXISTS setting;

DROP TABLE IF EXISTS location;

CREATE TABLE user(name TEXT PRIMARY KEY,
		phone_number TEXT,
		password TEXT,
		id INTEGER,
		sex TEXT,
		age INTEGER,
		country TEXT , 
		province TEXT ,
		city TEXT,
		invitation_code TEXT,
		referee_id INTEGER,
		referee_invitation_code TEXT ,
		credit DOUBLE,
		url TEXT);
		
CREATE TABLE wallet(phone_number TEXT,
		total_credit DOUBLE,
		total_donate DOUBLE,
		register_credit INTEGER,
		unlock_credit INTEGER,
		attention_credit INTEGER,
		referral_credit INTEGER,
		exchange_credit INTEGER,
		reward_credit INTEGER,
		punish_credit INTEGER,
		notify_credit INTEGER,
		version INTEGER,
		forceMsg TEXT,
		today_credit DOUBLE,
		total_incoming DOUBLE);
		
CREATE TABLE adv(
        adv_phone_id INTEGER,
        iClass       INTEGER,
        content      TEXT,
        link         TEXT,
        isFree       INTEGER,
	price        INTEGER,
		contentArray BINARY);
		
CREATE TABLE setting(enable_lockscreen INTEGER);

INSERT INTO setting VALUES (1);

CREATE TABLE location(
		lng DOUBLE ,
		lat DOUBLE,
		address TEXT);
		
