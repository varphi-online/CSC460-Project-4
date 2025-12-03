CREATE TABLE Member (
    memberNum Integer,
    name VARCHAR(255),
    tele_num VARCHAR(20),
    email VARCHAR(255),
    dob DATE,
    membershipTier VARCHAR(6) NOT NULL CHECK (membershipTier IN ('BRONZE', 'SILVER', 'GOLD')),
    emergencyCont VARCHAR(255)
);