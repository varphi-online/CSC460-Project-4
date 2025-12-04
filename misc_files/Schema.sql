CREATE TABLE Room (
    roomId INTEGER,
    maxCapacity INTEGER,

    PRIMARY KEY (roomId)
);

CREATE TABLE Member (
    memberNum INTEGER,
    name VARCHAR(255),
    tele_num VARCHAR(20),
    email VARCHAR(255),
    dob DATE,
    membershipTier VARCHAR(6) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD')),

    PRIMARY KEY (memberNum)
);

CREATE TABLE MemberHistory (
    memberNum INTEGER,
    s DATE,
    e DATE,
    membershipTier VARCHAR(6) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD')),

    FOREIGN KEY (memberNum) REFERENCES Member(memberNum)
);

CREATE TABLE EmergencyContact (
    contactId INTEGER,
    memberNum INTEGER,
    contactName VARCHAR(255),
    tele_num VARCHAR(20),
    email VARCHAR(255),

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
    checkedOut DATE, -- TODO: Add trigger to compare checkedIn and checkedOut

    PRIMARY KEY (reservationId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (roomId) REFERENCES Room(RoomId)
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