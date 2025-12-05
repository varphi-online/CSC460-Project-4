INSERT INTO Animals VALUES (
	'CAT'
);

INSERT INTO Animals VALUES (
	'Bird'
);

INSERT INTO Animals VALUES (
	'DOG'
);

INSERT INTO AnimalNeed VALUES (
	'Food 3 times a day',
	'CAT'
);

INSERT INTO AnimalNeed VALUES (
	'Bath twice a month',
	'DOG'
);

INSERT INTO AnimalNeed VALUES (
	'Alone Time 1 hour/week',
	'BIRD'
);

INSERT INTO Room VALUES (
	1,
	12
);

INSERT INTO Room VALUES (
	2,
	8
);

INSERT INTO Room VALUES (
	3,
	8
);

INSERT INTO Room VALUES (
	4,
	5
);

INSERT INTO Pet VALUES (
	1,
	'DOG',
	'Silver labrador',
	7,
	'2018-05-14',
	TRUE
);

INSERT INTO Pet VALUES (
	2,
	'BIRD',
	'Chinese Goose',
	4,
	'2021-08-15',
	FALSE
);

INSERT INTO Pet VALUES (
	3,
	'CAT',
	'Persian Cat',
	3,
	'2022-11-05',
	TRUE
);

INSERT INTO PetTemperment VALUES (
	'Overwhelmed when a lot of people in room',
	2
);

INSERT INTO PetTemperment VALUES (
	'Very sociable',
	1
);

INSERT INTO PetTemperment VALUES (
	'does not play but will hang out',
	3
);

INSERT INTO Area VALUES (
	1,
	1,
	'CAT',
	TRUE
);

INSERT INTO Area VALUES (
	2,
	1,
	'DOG',
	TRUE
);

INSERT INTO Area VALUES (
	1,
	2,
	'BIRD',
	FALSE
);

INSERT INTO Area VALUES (
	1,
	3,
	'DOG',
	TRUE
);

INSERT INTO Area VALUES (
	1,
	4,
	'CAT',
	FALSE
);

INSERT INTO PetRoomHistory VALUES (
	1,
	2,
	1,
	'2025-04-16',
	'2025-04-16'
);

INSERT INTO PetRoomHistory VALUES (
	3,
	1,
	1,
	'2025-11-19',
	'2025-11-19'
);

INSERT INTO PetRoomHistory VALUES (
	2,
	2,
	1,
	'2025-04-17',
	'2025-04-17'
);


INSERT INTO Member VALUES (
	1,
	'John Wayne',
	'415-456-3028',
	'johnW@yahoo.com',
	'1994-03-16',
	'SILVER'
);

INSERT INTO Member VALUES (
	2,
	'Emily Barker',
	'925-488-2415',
	'emmy4@gmail.com',
	'2001-08-25',
	'BRONZE'
);

INSERT INTO Member VALUES (
	3,
	'Justin Lakes',
	'713-331-8703',
	'thisjustin@gmail.com',
	'2000-02-05',
	'GOLD'
);

INSERT INTO MemberHistory VALUES (
	3,
	'2024-11-07',
	'2024-12-07',
	'SILVER'
);

INSERT INTO MemberHistory VALUES (
	3,
	'2024-12-08',
	'2025-12-08',
	'GOLD'
);

INSERT INTO MemberHistory VALUES (
	2,
	'2025-04-16',
	'2025-04-17',
	'BRONZE'
);

INSERT INTO MemberHistory VALUES (
	1,
	'2025-11-19',
	'2025-12-20',
	'SILVER'
);

INSERT INTO EmergancyContact VALUES (
	1,
	1,
	'Criss Wayne',
	'675-204-1242',
	'crissw@gmail.com'
);

INSERT INTO EmergancyContact VALUES (
	2,
	2,
	'Jeff Baker',
	'968-246-9275',
	'jefferyb@yahoo.com'
);

INSERT INTO EmergancyContact VALUES (
	3,
	3,
	'Sandra Locks',
	'415-785-2034',
	'sandral@gmail.com'
);

INSERT INTO Reservation VALUES (
	1,
	3,
	2,
	'2025-04-16',
	'03:00:00' HOUR TO SECOND,
	'YES',
	'YES'
);

INSERT INTO Reservation VALUES (
	2,
	1,
	'25-11-19',
	'02:00:00' HOUR TO SECOND,
	'YES',
	'YES'
);

INSERT INTO Reservation VALUES (
	3,
	2,
	'25-04-17',
	'01:30:00' HOUR TO SECOND,
	'NO',
	'NO'
);

INSERT INTO FoodOrder VALUES ( -- Needs to be changed when coming up with discount
	1,
	3,
	1,
	'2025-04-16',
	9.50,
	TRUE
);

INSERT INTO FoodOrder VALUES ( -- same as above
	2,
	1,
	2,
	'25-11-19',
	5.00,
	FALSE
);

INSERT INTO Item VALUES (
	1,
	1.50,
	'Water'
);

INSERT INTO Item VALUES (
	2,
	8.00,
	'Burger with fries'
);

INSERT INTO Item VALEUS (
	3,
	5.00,
	'Ham Sandwitch'
); 

INSERT INTO OrderItem VALUES (
	1,
	1,
	1
);

INSERT INTO OrderItem VALUES (
	2,
	1,
	1,
);

INSERT INTO OrderItem VALUES (
	3,
	2,
	1
);

INSERT INTO Staff VALUES (
	100,
	'Charles Darwin',
	'MGR'
);

INSERT INTO Staff VALUES (
	101,
	'Alexa Levy',
	'VET'
);

INSERT INTO Staff VALUES (
	102,
	'Ariel Lamb',
	'CHK'
);

INSERT INTO HealthRecord VALUES (
	1,
	1,
	'insert',
	'2018-05-14',
	1,
	102,
	'CHK',
	'Everything is good!',
	,
	'Healthy'
);

INSERT INTO HealthRecord VALUES (
	2,
	1,
	'insert',
	'2021-08-15',
	2,
	102,
	'CHK',
	'Hurt Wing',
	'2021-08-22',
	'Injured'
);

INSERT INTO HealthRecord VALUES (
	3,
	2,
	'update'
	'2021-08-22'
	2,
	101,
	'VET',
	'Bird is doing well now',
	,
	'Healthy'
);

INSERT INTO HealthRecord VALUES (
	4,
	1,
	'insert',
	'2022-11-05',
	3,
	102,
	'CHK',
	'Everything is good!',
	,
	'Healthy'
);
