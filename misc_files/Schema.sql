CREATE TABLE Animals (
    animalType VARCHAR2(10) NOT NULL CHECK (animalType IN ('CAT', 'DOG', 'BIRD')),
    PRIMARY KEY (animalType)
);

CREATE TABLE AnimalNeed (
    need VARCHAR2(255),
    animalType VARCHAR2(10),

    PRIMARY KEY (need),
    FOREIGN KEY (animalType) REFERENCES Animals(animalType)
);

CREATE TABLE Room(
    roomId INTEGER,
    maxCapacity INTEGER,

    PRIMARY KEY (roomId)
);

CREATE TABLE Pet (
    petId INTEGER,
    animalType VARCHAR2(10),
    breed VARCHAR2(255),
    age INTEGER NOT NULL CHECK (age > -1),
    doa DATE, 
    adoptable BOOLEAN, 

    PRIMARY KEY (petId),
    FOREIGN KEY (animalType) REFERENCES Animals(animalType)
);

CREATE TABLE PetTemperment (
    temperment VARCHAR2(255),
    petId INTEGER,

    PRIMARY KEY (temperment, petId),
    FOREIGN KEY (petId) REFERENCES Pet(petId)
);

CREATE TABLE Area (
    sector INTEGER,
    roomId INTEGER,
    animalType VARCHAR2(10),

    designatedAdoptArea BOOLEAN,

    PRIMARY KEY (sector, roomId),
    FOREIGN KEY (roomId) REFERENCES Room(roomId),
    FOREIGN KEY (animalType) REFERENCES Animals(animalType)
);

CREATE TABLE PetRoomHistory (
    petId INTEGER,
    roomId INTEGER,
    sector INTEGER,
    startDate DATE,
    endDate DATE,

    PRIMARY KEY (petId, startDate),
    FOREIGN KEY (petId) REFERENCES Pet(petId),
    FOREIGN KEY (roomId) REFERENCES Room(roomId),
    FOREIGN KEY (sector, roomId) REFERENCES Area(sector, roomId) 
);

CREATE TABLE Member (
    memberNum INTEGER,
    name VARCHAR2(255),
    tele_num VARCHAR2(20),
    email VARCHAR2(255),
    dob DATE,
    membershipTier VARCHAR2(6) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD')),

    PRIMARY KEY (memberNum)
);

CREATE TABLE MemberHistory (
    memberNum INTEGER,
    startDate DATE,
    endDate DATE,
    membershipTier VARCHAR2(6) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD')),

    FOREIGN KEY (memberNum) REFERENCES Member(memberNum)
);

CREATE TABLE EmergencyContact (
    contactId INTEGER,
    memberNum INTEGER,
    name VARCHAR2(255),
    tele_num VARCHAR2(20),
    email VARCHAR2(255),

    PRIMARY KEY (contactId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum)
);

CREATE TABLE Reservation (
    reservationId INTEGER,
    memberNum INTEGER,
    roomId INTEGER,
    reservationDate DATE,
    timeSlot INTERVAL DAY TO SECOND,
    checkedIn DATE,
    checkedOut DATE, 

    PRIMARY KEY (reservationId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (roomId) REFERENCES Room(roomId)
);

CREATE TABLE FoodOrder (
    orderId INTEGER,
    memberNum INTEGER,
    reservationId INTEGER,
    orderTime DATE,
    totalPrice NUMBER(6, 2),
    paymentStatus BOOLEAN,

    PRIMARY KEY (orderId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (reservationId) REFERENCES Reservation(reservationId)
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

    FOREIGN KEY (orderId) REFERENCES FoodOrder(orderId),
    FOREIGN KEY (itemId) REFERENCES Item(itemId)
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
    recType VARCHAR2(10) NOT NULL CHECK (revAction IN ('VET', 'CHK', 'SCH', 'GRM', 'BHN')),
    description VARCHAR2(255),
    nextDue DATE,
    status VARCHAR2(10),

    PRIMARY KEY (recId, revNum),
    FOREIGN KEY (petId) REFERENCES Pet(petId),
    FOREIGN KEY (empId) REFERENCES Staff(empId)
);

CREATE TABLE AdoptionApp (
    appId INTEGER,
    memberNum INTEGER,
    empId INTEGER,
    petId INTEGER,
    appDate DATE,
    status VARCHAR2(10) NOT NULL CHECK (status IN ('PEN', 'APP', 'REJ', 'WIT')),

    PRIMARY KEY (appId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (empId) REFERENCES Staff(empId),
    FOREIGN KEY (petId) REFERENCES Pet(petId)
);

CREATE TABLE Adoption (
    adoptId INTEGER,
    appId INTEGER,
    memberNum INTEGER,
    empId INTEGER,
    petId INTEGER,
    adoptDate DATE,
    fee NUMBER(6, 2),
    followUpSchedule VARCHAR2(255),

    PRIMARY KEY (adoptId),
    FOREIGN KEY (appId) REFERENCES AdoptionApp(appId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (empId) REFERENCES Staff(empId),
    FOREIGN KEY (petId) REFERENCES Pet(petId)
);

CREATE TABLE Event (
    eventId INTEGER,
    coordinator INTEGER, -- TODO: Trigger check coordinator?
    eventDate DATE,
    eventTime INTERVAL DAY TO MINUTE,
    roomId INTEGER,
    description VARCHAR2(255),
    maxCapacity INTEGER, -- TODO: TRIGGER TO MAKE SURE EVENT MAX DOES NOT EXCEED ROOM MAX

    PRIMARY KEY (eventId),
    FOREIGN KEY (coordinator) REFERENCES Staff(empId),
    FOREIGN KEY (roomId) REFERENCES Room(roomId)
);

CREATE TABLE Booking (
    bookingId INTEGER,
    eventId INTEGER,
    member INTEGER,
    status VARCHAR2(10) NOT NULL CHECK (status IN ('REG', 'ATT', 'NOS', 'CAN')),
    paid BOOLEAN,
    refunded BOOLEAN,

    PRIMARY KEY (bookingId),
    FOREIGN KEY (eventId) REFERENCES Event(eventId),
    FOREIGN KEY (member) REFERENCES Member(memberNum)
);