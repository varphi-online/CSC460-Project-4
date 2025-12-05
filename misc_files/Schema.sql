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
    date DATE,
    timeSlot INTERVAL DAY TO SECOND,

    checkedIn DATE,
    checkedOut DATE, 

    PRIMARY KEY (reservationId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (roomId) REFERENCES Room(roomId)
);

CREATE TABLE Orders (
    orderId INTEGER,
    memberNum INTEGER,
    reservationId INTEGER,
    orderTime DATE,
    totalPrice NUMBER,
    paymentStatus BOOLEAN,

    PRIMARY KEY (orderId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum)
);

CREATE TABLE Item (
    itemId INTEGER,
    price NUMBER(6, 2), 
    name VARCHAR2(255),
    
    PRIMARY KEY (itemId)
);

CREATE TABLE OrderItem (
    orderId INTEGER,
    itemId INTEGER,
    quantity INTEGER,

    PRIMARY KEY (orderId, itemId),
    FOREIGN KEY (orderId) REFERENCES Orders(orderId),
    FOREIGN KEY (itemId) REFERENCES Item(itemId)
);

CREATE TABLE Staff (
    empId INTEGER,
    name VARCHAR2(255),
    empType VARCHAR2(255) NOT NULL CHECK (empType IN ('VET', 'CRD', 'HDL', 'BAR', 'MGR')),

    PRIMARY KEY (empId)
);