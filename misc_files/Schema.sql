-- CREATE TABLE Animals (
--     animalType VARCHAR2(15) NOT NULL,
--     PRIMARY KEY (animalType)
-- );

-- SET SQLBLANKLINES ON;

CREATE TABLE Room(
    roomId INTEGER,
    maxCapacity INTEGER,

    PRIMARY KEY (roomId)
);

-- This can be used for an auto-incrementing PK for Pet. 
CREATE SEQUENCE pet_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE Pet (
    petId INTEGER NOT NULL,
    animalType VARCHAR2(15) NOT NULL,
    breed VARCHAR2(255),
    age INTEGER NOT NULL CHECK (age > -1),
    doa DATE NOT NULL, 
    adoptable NUMBER(1) DEFAULT 0 NOT NULL,
    name VARCHAR2(255),

    PRIMARY KEY (petId)
);

CREATE TABLE PetNeed (
    need VARCHAR2(255),
    petId INTEGER,

    PRIMARY KEY (need, petID),
    FOREIGN KEY (petId) REFERENCES Pet(petId) ON DELETE CASCADE
);

CREATE TABLE PetTemperment (
    temperment VARCHAR2(255),
    petId INTEGER,

    PRIMARY KEY (temperment, petId),
    FOREIGN KEY (petId) REFERENCES Pet(petId) ON DELETE CASCADE
);

CREATE TABLE Area (
    sector INTEGER,
    roomId INTEGER,
    animalType VARCHAR2(10),

    designatedAdoptArea NUMBER(1) DEFAULT 0 NOT NULL,

    PRIMARY KEY (sector, roomId),
    FOREIGN KEY (roomId) REFERENCES Room(roomId) ON DELETE CASCADE
);

CREATE TABLE PetRoomHistory (
    petId INTEGER,
    roomId INTEGER,
    sector INTEGER,
    startDate DATE,
    endDate DATE,

    PRIMARY KEY (petId, startDate),
    FOREIGN KEY (petId) REFERENCES Pet(petId) ON DELETE CASCADE,
    FOREIGN KEY (roomId) REFERENCES Room(roomId) ON DELETE CASCADE,
    FOREIGN KEY (sector, roomId) REFERENCES Area(sector, roomId) ON DELETE CASCADE
);

-- This can be used for an auto-incrementing PK for Member. 
CREATE SEQUENCE member_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE Member (
    memberNum INTEGER NOT NULL,
    name VARCHAR2(255),
    tele_num VARCHAR2(20),
    email VARCHAR2(255),
    dob DATE,
    membershipTier VARCHAR2(20) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD', 'NOT CURRENTLY MEMBER')),

    PRIMARY KEY (memberNum)
);

CREATE TABLE MemberHistory (
    memberNum INTEGER,
    startDate DATE,
    endDate DATE,
    membershipTier VARCHAR2(20) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD', 'NOT CURRENTLY MEMBER')),

    FOREIGN KEY (memberNum) REFERENCES Member(memberNum) ON DELETE CASCADE
);

CREATE TABLE EmergencyContact (
    contactId INTEGER,
    memberNum INTEGER,
    name VARCHAR2(255),
    tele_num VARCHAR2(20),
    email VARCHAR2(255),

    PRIMARY KEY (contactId, memberNum),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum) ON DELETE CASCADE
);

CREATE TABLE Reservation (
    reservationId INTEGER NOT NULL,
    memberNum INTEGER NOT NULL,
    roomId INTEGER NOT NULL,
    reservationDate DATE,
    timeSlot INTERVAL DAY TO SECOND,
    checkedIn VARCHAR2(3) NOT NULL CHECK (checkedIn IN('YES', 'NO')),
    checkedOut VARCHAR2(3) NOT NULL CHECK (checkedOut IN('YES', 'NO')),

    PRIMARY KEY (reservationId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum) ON DELETE CASCADE,
    FOREIGN KEY (roomId) REFERENCES Room(roomId) ON DELETE CASCADE
);

-- CREATE OR REPLACE TRIGGER CheckReservationCheckIn
-- BEFORE INSERT OR UPDATE OF checkedIn ON Reservation
-- FOR EACH ROW
-- DECLARE
--     end_time DATE;
-- BEGIN
--     IF :NEW.checkedIn = 'YES' THEN
--         end_time := :NEW.reservationDate + :NEW.timeSlot;
--         IF SYSDATE < :NEW.reservationDate THEN
--             RAISE_APPLICATION_ERROR(
--                 -20002, 
--                 'Check-in failed: It is too early for this reservation.'
--             );
--         END IF;
--         IF SYSDATE > end_time THEN
--             RAISE_APPLICATION_ERROR(
--                 -20003, 
--                 'Check-in failed: The time slot for this reservation has passed.'
--             );
--         END IF;   
--     END IF;
-- END;

CREATE TABLE FoodOrder (
    orderId INTEGER,
    memberNum INTEGER,
    reservationId INTEGER,
    orderTime DATE,
    totalPrice NUMBER(6, 2),
    paymentStatus NUMBER(1) DEFAULT 0 NOT NULL,

    PRIMARY KEY (orderId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (reservationId) REFERENCES Reservation(reservationId) ON DELETE CASCADE
);

CREATE TABLE Item (
    itemId INTEGER,
    price NUMBER(6, 2), -- assuming we won't have prices higher than $9999.99
    itemName VARCHAR(255),
    
    PRIMARY KEY (itemId)
);

CREATE TABLE OrderItem (
    orderId INTEGER,
    itemId INTEGER,
    quantity INTEGER,

    PRIMARY KEY (orderId, itemId),
    FOREIGN KEY (orderId) REFERENCES FoodOrder(orderId) ON DELETE CASCADE,
    FOREIGN KEY (itemId) REFERENCES Item(itemId) ON DELETE CASCADE
);

CREATE TABLE Staff (
    empId INTEGER,
    name VARCHAR2(255),
    empType VARCHAR2(255) NOT NULL CHECK (empType IN ('VET', 'CRD', 'HDL', 'BAR', 'MGR')),

    PRIMARY KEY (empId)
);

CREATE TABLE HealthRecord (
    recId INTEGER,
    revNum INTEGER, -- Add triggers for auto making revs on table changes
    revAction VARCHAR2(10) NOT NULL CHECK (revAction IN ('insert', 'update', 'delete')),
    revDate DATE,

    petId INTEGER,
    empId INTEGER,

    -- vaccination | checkup | feeding schedule | grooming | behavioral note
    recType VARCHAR2(10) NOT NULL CHECK (recType IN ('VET', 'CHK', 'SCH', 'GRM', 'BHN')),
    description VARCHAR2(255),
    nextDue DATE,
    status VARCHAR2(10),

    PRIMARY KEY (recId, revNum),
    FOREIGN KEY (petId) REFERENCES Pet(petId) ON DELETE CASCADE,
    FOREIGN KEY (empId) REFERENCES Staff(empId) ON DELETE SET NULL
);

CREATE TABLE AdoptionApp (
    appId INTEGER,
    memberNum INTEGER,
    empId INTEGER,
    petId INTEGER,
    appDate DATE,
    status VARCHAR2(10) NOT NULL CHECK (status IN ('PEN', 'APP', 'REJ', 'WIT')),

    PRIMARY KEY (appId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum) ON DELETE CASCADE,
    FOREIGN KEY (empId) REFERENCES Staff(empId) ON DELETE SET NULL,
    FOREIGN KEY (petId) REFERENCES Pet(petId) ON DELETE CASCADE
);

CREATE TABLE Adoption (
    adoptId INTEGER,
    appId INTEGER, -- removed pet, member, and emp as the application has fields already
    adoptDate DATE,
    fee NUMBER(6, 2),
    followUpSchedule VARCHAR2(255),

    PRIMARY KEY (adoptId),
    FOREIGN KEY (appId) REFERENCES AdoptionApp(appId) ON DELETE CASCADE
);

CREATE TABLE Event (
    eventId INTEGER,
    coordinator INTEGER,
    eventDate DATE,
    eventDuration INTERVAL DAY TO MINUTE,
    roomId INTEGER,
    description VARCHAR2(255),
    maxCapacity INTEGER,
    canceled NUMBER(1) DEFAULT 0 NOT NULL,

    PRIMARY KEY (eventId),
    FOREIGN KEY (coordinator) REFERENCES Staff(empId) ON DELETE SET NULL,
    FOREIGN KEY (roomId) REFERENCES Room(roomId) ON DELETE CASCADE
);

-- CREATE OR REPLACE TRIGGER CheckEventCoordinator
-- BEFORE INSERT ON Event FOR EACH ROW
-- DECLARE
--   v_count INTEGER;
-- BEGIN
--   SELECT COUNT(*)
--     INTO v_count
--     FROM Staff
--    WHERE Staff.empId = :NEW.coordinator
--      AND Staff.empType = 'CRD';

--   IF v_count = 0 THEN
--     RAISE_APPLICATION_ERROR(
--       -20001,
--       'Coordinator for event must be a coordinator employee.'
--     );
--   END IF;
-- END;

-- CREATE OR REPLACE TRIGGER CheckEventCapacity
-- BEFORE INSERT OR UPDATE OF roomId, maxCapacity
-- ON Event FOR EACH ROW
-- DECLARE
--   rm_cap INTEGER;
-- BEGIN
--   SELECT Room.maxCapacity
--     INTO rm_cap
--     FROM Room
--    WHERE Room.roomId = :NEW.roomId;

--   IF :NEW.maxCapacity > rm_cap THEN
--     RAISE_APPLICATION_ERROR(
--       -20001,
--       'Event capacity exceeds selected Room'
--     );
--   END IF;
-- END;

CREATE TABLE Booking (
    bookingId INTEGER,
    eventId INTEGER,
    member INTEGER,
    status VARCHAR2(10) NOT NULL CHECK (status IN ('REG', 'ATT', 'NOS', 'CAN')),
    paid NUMBER(1) DEFAULT 0 NOT NULL,
    refunded NUMBER(1) DEFAULT 0 NOT NULL,

    PRIMARY KEY (bookingId),
    FOREIGN KEY (eventId) REFERENCES Event(eventId) ON DELETE CASCADE,
    FOREIGN KEY (member) REFERENCES Member(memberNum) ON DELETE CASCADE
);

-- CREATE OR REPLACE TRIGGER CheckBookingCapacity
-- BEFORE INSERT ON Booking
-- FOR EACH ROW
-- DECLARE
--     maxCap INTEGER;
--     attendees INTEGER;
-- BEGIN
--     SELECT maxCapacity 
--     INTO maxCap 
--     FROM Event
--     WHERE eventId = :NEW.eventId;

--     SELECT COUNT(*) 
--     INTO attendees 
--     FROM Booking 
--     WHERE eventId = :NEW.eventId;

--     IF (attendees + 1) > maxCap THEN
--         RAISE_APPLICATION_ERROR(-20001, 'Max capacity for event has been met.');
--     END IF;
-- END;