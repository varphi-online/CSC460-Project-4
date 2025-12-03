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
    start DATE,
    end DATE,
    membershipTier VARCHAR(6) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD')),

    FOREIGN KEY (memberNum) REFERENCES Member(memberNum)
);

CREATE TABLE EmergencyContact (
    contactId INTEGER,
    memberNum INTEGER,
    name VARCHAR(255)
    tele_num VARCHAR(20),
    email VARCHAR(255),

    PRIMARY KEY (contactId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum)
);

CREATE TABLE Reservation (
    reservationId INTEGER,
    memberNum INTEGER,
    roomId INTEGER,
    date DATE,
    timeSlot INTERVAL DAY TO SECOND

    checkedIn DATE,
    checkedOut DATE, -- TODO: Add trigger to compare checkedIn and checkedOut

    PRIMARY KEY (reservationId),
    FOREIGN KEY (memberNum) REFERENCES Member(memberNum),
    FOREIGN KEY (roomId) REFERENCES Room(RoomId)
);

CREATE TABLE Order (
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
    price NUMBER(6, 2), -- assuming we won't have prices higher than $9999.99
    name VARCHAR(255),
    
    PRIMARY KEY (itemId)
);

CREATE TABLE OrderItem (
    orderId INTEGER,
    itemId INTEGER,
    quantity INTEGER,

    FOREIGN KEY (orderId) REFERENCES Order(orderId),
    FOREIGN KEY (itemId) REFERENCES Item(itemId)
);