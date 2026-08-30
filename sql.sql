-- ==========================================
-- EMPLOYEES TABLE
-- ==========================================

CREATE TABLE employees (
    employee_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    passport_no VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    department VARCHAR(100),
    position VARCHAR(100),
    join_date DATE DEFAULT CURRENT_DATE
);


-- ==========================================
-- HR STAFF TABLE
-- ==========================================

CREATE TABLE hr_staff (
    hr_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100)
);


-- ==========================================
-- EMPLOYEE LOGIN TABLE
-- ==========================================

CREATE TABLE employee_login (
    login_id SERIAL PRIMARY KEY,
    employee_id INT UNIQUE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    CONSTRAINT fk_employee_login
        FOREIGN KEY(employee_id)
        REFERENCES employees(employee_id)
        ON DELETE CASCADE
);


-- ==========================================
-- FAMILY DETAILS TABLE
-- ==========================================

CREATE TABLE family_details (
    family_id SERIAL PRIMARY KEY,
    employee_id INT NOT NULL,
    member_name VARCHAR(100) NOT NULL,
    relationship VARCHAR(50),
    age INT,

    CONSTRAINT fk_employee_family
    FOREIGN KEY(employee_id)
    REFERENCES employees(employee_id)
    ON DELETE CASCADE
);


-- ==========================================
-- LEAVE BALANCE TABLE
-- ==========================================

CREATE TABLE leave_balance (
    balance_id SERIAL PRIMARY KEY,
    employee_id INT UNIQUE,
    total_leave INT DEFAULT 20,
    used_leave INT DEFAULT 0,
    remaining_leave INT DEFAULT 20,

    CONSTRAINT fk_leave_employee
    FOREIGN KEY(employee_id)
    REFERENCES employees(employee_id)
    ON DELETE CASCADE
);


-- ==========================================
-- LEAVE REQUEST TABLE
-- ==========================================

CREATE TABLE leave_requests (
    leave_id SERIAL PRIMARY KEY,
    employee_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    status VARCHAR(20) DEFAULT 'Pending',

    CONSTRAINT fk_leave_request_employee
    FOREIGN KEY(employee_id)
    REFERENCES employees(employee_id)
    ON DELETE CASCADE
);


-- ==========================================
-- INDEXES (FOR BETTER PERFORMANCE)
-- ==========================================

CREATE INDEX idx_employee_email
ON employees(email);

CREATE INDEX idx_leave_employee
ON leave_requests(employee_id);



INSERT INTO hr_staff (username, password, full_name, email)
VALUES ('siddartha_hr', '+E4PjxAc2TaHiNd5wtZFCQ==:VlhPXvfPxdxz/CH/BDKxcNH+k1wJD33C8hcPu1CXWEM=', 'Siddartha Shrestha', 'siddartha.hr@company.com');

SELECT * FROM hr_staff;

SELECT * FROM employee_login;

SELECT * FROM leave_requests;