-- INSERT INTO Animals VALUES (
-- 	'CAT'
-- );

-- INSERT INTO Animals VALUES (
-- 	'BIRD'
-- );

-- INSERT INTO Animals VALUES (
-- 	'DOG'
-- );

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
	pet_seq.NEXTVAL,
	'DOG',
	'Silver labrador',
	7,
	'2018-05-14',
	TRUE,
	'Duke'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'BIRD',
	'Chinese Goose',
	4,
	'2021-08-15',
	FALSE,
	'Empress'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'CAT',
	'Persian Cat',
	3,
	'2022-11-05',
	TRUE,
	'Ms. Whiskers'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'DOG',
	'Golden Retriever',
	5,
	'2020-06-10',
	TRUE,
	'Buddy'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'CAT',
	'Siamese',
	2,
	'2023-01-20',
	TRUE,
	'Luna'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'BIRD',
	'Cockatiel',
	1,
	'2024-03-05',
	TRUE,
	'Sunny'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'DOG',
	'Beagle',
	6,
	'2019-09-12',
	FALSE,
	'Charlie'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'CAT',
	'Maine Coon',
	4,
	'2021-11-30',
	TRUE,
	'Leo'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'BIRD',
	'Parakeet',
	2,
	'2022-07-18',
	TRUE,
	'Kiwi'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'DOG',
	'Poodle',
	8,
	'2016-04-22',
	FALSE,
	'Bella'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'CAT',
	'Bengal',
	5,
	'2019-05-14',
	TRUE,
	'Tiger'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'BIRD',
	'Macaw',
	3,
	'2021-12-01',
	FALSE,
	'Rio'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'DOG',
	'Shih Tzu',
	2,
	'2023-08-09',
	TRUE,
	'Max'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'CAT',
	'Russian Blue',
	1,
	'2024-02-15',
	TRUE,
	'Smokey'
);

INSERT INTO Pet VALUES (
	pet_seq.NEXTVAL,
	'BIRD',
	'Finch',
	4,
	'2020-10-25',
	TRUE,
	'Peep'
);

INSERT INTO PetNeed VALUES (
	'Food 3 times a day',
	1
);

INSERT INTO PetNeed VALUES (
	'Bath twice a month',
	2
);

INSERT INTO PetNeed VALUES (
	'Alone Time 1 hour/week',
	3
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
	member_seq.NEXTVAL,
	'John Wayne',
	'415-456-3028',
	'johnW@yahoo.com',
	'1994-03-16',
	'SILVER'
);

INSERT INTO Member VALUES (
	member_seq.NEXTVAL,
	'Emily Barker',
	'925-488-2415',
	'emmy4@gmail.com',
	'2001-08-25',
	'BRONZE'
);

INSERT INTO Member VALUES (
	member_seq.NEXTVAL,
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

INSERT INTO EmergencyContact VALUES (
	1,
	1,
	'Criss Wayne',
	'675-204-1242',
	'crissw@gmail.com'
);

INSERT INTO EmergencyContact VALUES (
	2,
	2,
	'Jeff Baker',
	'968-246-9275',
	'jefferyb@yahoo.com'
);

INSERT INTO EmergencyContact VALUES (
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
	INTERVAL '03:00:00' HOUR TO SECOND,
	'YES',
	'YES'
);

INSERT INTO Reservation VALUES (
	2,
	1,
	2,
	'25-11-19',
	INTERVAL '02:00:00' HOUR TO SECOND,
	'YES',
	'YES'
);

INSERT INTO Reservation VALUES (
	3,
	2,
	3,
	'25-04-17',
	INTERVAL '01:30:00' HOUR TO SECOND,
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

INSERT INTO Item VALUES (
	3,
	5.00,
	'Ham Sandwitch'
); 

INSERT INTO OrderItem VALUES (
	1,
	1,
	3
);

INSERT INTO OrderItem VALUES (
	1,
	3,
	1
);

INSERT INTO OrderItem VALUES (
	2,
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
	'BAR'
);

INSERT INTO Staff VALUES (
	103,
	'Jon Gray',
	'HDL'
);

INSERT INTO Staff VALUES (
	104,
	'Wayne Lamb',
	'CRD'
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
	NULL,
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
	'update',
	'2021-08-22',
	2,
	101,
	'VET',
	'Bird is doing well now',
	NULL,
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
	NULL,
	'Healthy'
);

INSERT INTO AdoptionApp VALUES (
	1,
	2,
	103,
	3,
	'2023-12-01',
	'PEN'
);

INSERT INTO AdoptionApp VALUES (
	2,
	2,
	103,
	2,
	'2008-05-06',
	'REJ'
);

INSERT INTO AdoptionApp VALUES (
	3,
	1,
	103,
	1,
	'2025-12-02',
	'APP'
);

INSERT INTO Adoption VALUES (
	1,
	1,
	'2024-01-03',
	200.00,
	'Once a month for the first 6 mos.'
);

INSERT INTO Adoption VALUES (
	2,
	3,
	NULL, -- hasnt happened yet
	400.00,
	'Once a month for the first 6 mos.'
);

INSERT INTO Adoption VALUES (
	3,
	2,
	'2008-05-27',
	10.00,
	'Once a month for the first 6 mos.'
);

INSERT INTO Event VALUES (
	1,
	104,
	'2020-03-13',
	'02:30:00' HOUR TO SECOND,
	3,
	'LAST DAY OF SCHOOL!',
	6
);

INSERT INTO Event VALUES (
	2,
	104,
	'2015-07-18',
	'16:30:00' HOUR TO SECOND,
	2,
	'Jamies Birthday!',
	50
);

INSERT INTO Event VALUES (
	3,
	104,
	'2023-05-01',
	'12:30:00' HOUR TO SECOND,
	1,
	'Adoption bonanza',
	2
);

INSERT INTO Booking VALUES (
	1,
	1,
	2,
	'REG',
	TRUE,
	FALSE
);

INSERT INTO Booking VALUES (
	2,
	1,
	3,
	'ATT',
	FALSE,
	FALSE
);

INSERT INTO Booking VALUES (
	3,
	2,
	2,
	'CAN',
	TRUE,
	TRUE
);