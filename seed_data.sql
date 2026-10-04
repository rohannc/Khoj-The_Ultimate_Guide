-- ====================================================================
-- SEED SCRIPT FOR KHOJ HEALTHCARE DATABASE (10 Realistic Rows Per Table)
-- Password for all users: 'Password@123' (BCrypt hashed)
-- ====================================================================

-- 1. PATIENTS (10 rows)
INSERT INTO patients (id, username, password, email_id, role, created_at, updated_at, primary_mobile, secondary_mobile, first_name, last_name, date_of_birth, gender, blood_group, street, city, state, pin_code, country) VALUES
('a0000000-0000-0000-0000-000000000001', 'aarav.sharma', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'aarav.sharma@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543210', '9876543211', 'Aarav', 'Sharma', '1990-05-15', 'MALE', 'O+', '12 MG Road', 'Mumbai', 'Maharashtra', '400001', 'India'),
('a0000000-0000-0000-0000-000000000002', 'diya.patel', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'diya.patel@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543212', '9876543213', 'Diya', 'Patel', '1995-08-22', 'FEMALE', 'B+', '45 SG Highway', 'Ahmedabad', 'Gujarat', '380015', 'India'),
('a0000000-0000-0000-0000-000000000003', 'rohan.mehta', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'rohan.mehta@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543214', '9876543215', 'Rohan', 'Mehta', '1988-12-01', 'MALE', 'A+', '78 Park Street', 'Kolkata', 'West Bengal', '700016', 'India'),
('a0000000-0000-0000-0000-000000000004', 'ananya.iyer', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'ananya.iyer@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543216', '9876543217', 'Ananya', 'Iyer', '1993-03-10', 'FEMALE', 'AB+', '34 Anna Salai', 'Chennai', 'Tamil Nadu', '600002', 'India'),
('a0000000-0000-0000-0000-000000000005', 'vihaan.verma', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'vihaan.verma@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543218', '9876543219', 'Vihaan', 'Verma', '2001-11-25', 'MALE', 'O-', '89 Connaught Place', 'New Delhi', 'Delhi', '110001', 'India'),
('a0000000-0000-0000-0000-000000000006', 'ishita.sen', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'ishita.sen@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543220', '9876543221', 'Ishita', 'Sen', '1985-07-19', 'FEMALE', 'A-', '102 Indiranagar', 'Bengaluru', 'Karnataka', '560038', 'India'),
('a0000000-0000-0000-0000-000000000007', 'kabir.reddy', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'kabir.reddy@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543222', '9876543223', 'Kabir', 'Reddy', '1992-09-05', 'MALE', 'B-', '5 Banjara Hills', 'Hyderabad', 'Telangana', '500034', 'India'),
('a0000000-0000-0000-0000-000000000008', 'sanya.malhotra', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'sanya.malhotra@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543224', '9876543225', 'Sanya', 'Malhotra', '1998-04-14', 'FEMALE', 'O+', '23 Sector 17', 'Chandigarh', 'Punjab', '160017', 'India'),
('a0000000-0000-0000-0000-000000000009', 'arjun.nair', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'arjun.nair@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543226', '9876543227', 'Arjun', 'Nair', '1982-01-30', 'MALE', 'AB-', '67 Marine Drive', 'Kochi', 'Kerala', '682031', 'India'),
('a0000000-0000-0000-0000-000000000010', 'meera.joshi', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'meera.joshi@example.com', 'ROLE_PATIENT', NOW(), NOW(), '9876543228', '9876543229', 'Meera', 'Joshi', '1996-06-18', 'FEMALE', 'B+', '90 FC Road', 'Pune', 'Maharashtra', '411004', 'India')
ON CONFLICT (id) DO NOTHING;

-- 2. DOCTORS (10 rows)
INSERT INTO doctors (id, username, password, email_id, role, created_at, updated_at, primary_mobile, secondary_mobile, first_name, last_name, gender, registration_number, registration_issue_date, specializations, qualifications) VALUES
('b0000000-0000-0000-0000-000000000001', 'dr.rajesh.gupta', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.rajesh.gupta@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456780', '9123456781', 'Rajesh', 'Gupta', 'MALE', 'MCI-10001', '2010-06-12', 'Cardiology', 'MBBS, MD (Cardiology)'),
('b0000000-0000-0000-0000-000000000002', 'dr.priya.deshmukh', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.priya.deshmukh@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456782', '9123456783', 'Priya', 'Deshmukh', 'FEMALE', 'MCI-10002', '2012-08-20', 'Dermatology', 'MBBS, DVD'),
('b0000000-0000-0000-0000-000000000003', 'dr.amit.bose', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.amit.bose@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456784', '9123456785', 'Amit', 'Bose', 'MALE', 'MCI-10003', '2008-03-15', 'Orthopedics', 'MBBS, MS (Ortho)'),
('b0000000-0000-0000-0000-000000000004', 'dr.sunita.rao', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.sunita.rao@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456786', '9123456787', 'Sunita', 'Rao', 'FEMALE', 'MCI-10004', '2015-11-05', 'Pediatrics', 'MBBS, DCH'),
('b0000000-0000-0000-0000-000000000005', 'dr.vikram.chawla', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.vikram.chawla@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456788', '9123456789', 'Vikram', 'Chawla', 'MALE', 'MCI-10005', '2005-09-18', 'Neurology', 'MBBS, DM (Neurology)'),
('b0000000-0000-0000-0000-000000000006', 'dr.neha.kulkarni', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.neha.kulkarni@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456790', '9123456791', 'Neha', 'Kulkarni', 'FEMALE', 'MCI-10006', '2016-04-22', 'Gynecology', 'MBBS, MS (OBG)'),
('b0000000-0000-0000-0000-000000000007', 'dr.manish.kapoor', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.manish.kapoor@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456792', '9123456793', 'Manish', 'Kapoor', 'MALE', 'MCI-10007', '2011-01-10', 'General Medicine', 'MBBS, MD (Medicine)'),
('b0000000-0000-0000-0000-000000000008', 'dr.pooja.singh', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.pooja.singh@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456794', '9123456795', 'Pooja', 'Singh', 'FEMALE', 'MCI-10008', '2018-07-30', 'Ophthalmology', 'MBBS, MS (Ophthalmology)'),
('b0000000-0000-0000-0000-000000000009', 'dr.sanjay.menon', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.sanjay.menon@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456796', '9123456797', 'Sanjay', 'Menon', 'MALE', 'MCI-10009', '2007-12-04', 'ENT', 'MBBS, MS (ENT)'),
('b0000000-0000-0000-0000-000000000010', 'dr.shalini.nair', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'dr.shalini.nair@example.com', 'ROLE_DOCTOR', NOW(), NOW(), '9123456798', '9123456799', 'Shalini', 'Nair', 'FEMALE', 'MCI-10010', '2014-05-19', 'Psychiatry', 'MBBS, MD (Psychiatry)')
ON CONFLICT (id) DO NOTHING;

-- 3. CLINICS (10 rows)
INSERT INTO clinics (id, username, password, email_id, role, created_at, updated_at, primary_mobile, secondary_mobile, name, street, city, state, pin_code, country, website, monday_start, monday_end, tuesday_start, tuesday_end, wednesday_start, wednesday_end, thursday_start, thursday_end, friday_start, friday_end, saturday_start, saturday_end, sunday_start, sunday_end) VALUES
('c0000000-0000-0000-0000-000000000001', 'apollo.bandra', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'contact@apollobandra.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567801', '9234567802', 'Apollo Clinic Bandra', 'Hill Road, Bandra West', 'Mumbai', 'Maharashtra', '400050', 'India', 'https://apollo-bandra.com', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '18:00', '10:00', '14:00'),
('c0000000-0000-0000-0000-000000000002', 'fortis.koramangala', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'info@fortiskora.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567803', '9234567804', 'Fortis Medical Center', '80 Feet Road, Koramangala', 'Bengaluru', 'Karnataka', '560034', 'India', 'https://fortis-blr.com', '08:30', '20:30', '08:30', '20:30', '08:30', '20:30', '08:30', '20:30', '08:30', '20:30', '09:00', '17:00', NULL, NULL),
('c0000000-0000-0000-0000-000000000003', 'max.saket', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'care@maxsaket.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567805', '9234567806', 'Max Healthcare Clinic', 'Press Enclave Road, Saket', 'New Delhi', 'Delhi', '110017', 'India', 'https://maxhealthcare.com', '09:00', '21:00', '09:00', '21:00', '09:00', '21:00', '09:00', '21:00', '09:00', '21:00', '09:00', '19:00', '09:00', '13:00'),
('c0000000-0000-0000-0000-000000000004', 'manipal.whitefield', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'help@manipalwf.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567807', '9234567808', 'Manipal Polyclinic', 'ITPL Main Road', 'Bengaluru', 'Karnataka', '560066', 'India', 'https://manipalhospitals.com', '08:00', '20:00', '08:00', '20:00', '08:00', '20:00', '08:00', '20:00', '08:00', '20:00', '08:00', '16:00', NULL, NULL),
('c0000000-0000-0000-0000-000000000005', 'care.banjara', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'care@carehospitals.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567809', '9234567810', 'Care Outpatient Center', 'Road No. 1, Banjara Hills', 'Hyderabad', 'Telangana', '500034', 'India', 'https://carehospitals.com', '09:00', '19:00', '09:00', '19:00', '09:00', '19:00', '09:00', '19:00', '09:00', '19:00', '09:00', '14:00', NULL, NULL),
('c0000000-0000-0000-0000-000000000006', 'medanta.dlf', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'info@medantaclinic.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567811', '9234567812', 'Medanta Mediclinic', 'DLF Cyber City, Phase 2', 'Gurugram', 'Haryana', '122002', 'India', 'https://medanta.org', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '18:00', '10:00', '13:00'),
('c0000000-0000-0000-0000-000000000007', 'rubyhall.kothrud', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'kothrud@rubyhall.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567813', '9234567814', 'Ruby Hall Clinic Kothrud', 'Paud Road, Kothrud', 'Pune', 'Maharashtra', '411038', 'India', 'https://rubyhall.com', '08:00', '21:00', '08:00', '21:00', '08:00', '21:00', '08:00', '21:00', '08:00', '21:00', '08:00', '19:00', '09:00', '13:00'),
('c0000000-0000-0000-0000-000000000008', 'peerless.kolkata', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'care@peerlesshospital.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567815', '9234567816', 'Peerless Clinic', 'EM Bypass, Panchasayar', 'Kolkata', 'West Bengal', '700094', 'India', 'https://peerlesshospital.com', '09:00', '19:00', '09:00', '19:00', '09:00', '19:00', '09:00', '19:00', '09:00', '19:00', '09:00', '15:00', NULL, NULL),
('c0000000-0000-0000-0000-000000000009', 'aster.edapally', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'help@astermedcity.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567817', '9234567818', 'Aster Medcity Clinic', 'Cheranallur, Edapally', 'Kochi', 'Kerala', '682027', 'India', 'https://astermedcity.com', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '20:00', '09:00', '17:00', '10:00', '14:00'),
('c0000000-0000-0000-0000-000000000010', 'sims.vadapalani', '$2a$10$0v0/vHO0CpprQxm9om7olOfEGyKRomvmJlZJFIzIMchLEhmirkeNi', 'contact@simshospitals.com', 'ROLE_CLINIC', NOW(), NOW(), '9234567819', '9234567820', 'SIMS Specialty Center', 'Jawaharlal Nehru Road, Vadapalani', 'Chennai', 'Tamil Nadu', '600026', 'India', 'https://simshospitals.com', '08:30', '20:00', '08:30', '20:00', '08:30', '20:00', '08:30', '20:00', '08:30', '20:00', '09:00', '16:00', NULL, NULL)
ON CONFLICT (id) DO NOTHING;

-- 4. DOCTOR_CLINIC_AFFILIATIONS (10 rows)
INSERT INTO doctor_clinic_affiliations (id, doctor_id, clinic_id, status, initiated_by, joining_date, doctor_charge, clinic_charge, action_required_by, daily_patient_limit, monday_start, monday_end, tuesday_start, tuesday_end, wednesday_start, wednesday_end, thursday_start, thursday_end, friday_start, friday_end, saturday_start, saturday_end, sunday_start, sunday_end, requested_at, updated_at, version) VALUES
('d0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'APPROVED', 'DOCTOR', '2023-01-10', 1000.0, 300.0, 'NONE', 20, '10:00', '14:00', '10:00', '14:00', '10:00', '14:00', '10:00', '14:00', '10:00', '14:00', '10:00', '13:00', NULL, NULL, NOW() - interval '1 year', NOW(), 1),
('d0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001', 'APPROVED', 'CLINIC', '2023-03-15', 800.0, 250.0, 'NONE', 15, '15:00', '19:00', '15:00', '19:00', '15:00', '19:00', '15:00', '19:00', '15:00', '19:00', NULL, NULL, NULL, NULL, NOW() - interval '10 months', NOW(), 1),
('d0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000002', 'APPROVED', 'DOCTOR', '2023-02-01', 900.0, 300.0, 'NONE', 18, '10:00', '13:00', '10:00', '13:00', '10:00', '13:00', '10:00', '13:00', '10:00', '13:00', '10:00', '13:00', NULL, NULL, NOW() - interval '11 months', NOW(), 1),
('d0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000004', 'c0000000-0000-0000-0000-000000000002', 'APPROVED', 'DOCTOR', '2023-04-10', 700.0, 200.0, 'NONE', 25, '09:00', '12:00', '09:00', '12:00', '09:00', '12:00', '09:00', '12:00', '09:00', '12:00', NULL, NULL, NULL, NULL, NOW() - interval '9 months', NOW(), 1),
('d0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000005', 'c0000000-0000-0000-0000-000000000003', 'APPROVED', 'CLINIC', '2023-05-01', 1200.0, 400.0, 'NONE', 12, '14:00', '18:00', '14:00', '18:00', '14:00', '18:00', '14:00', '18:00', '14:00', '18:00', NULL, NULL, NULL, NULL, NOW() - interval '8 months', NOW(), 1),
('d0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000004', 'APPROVED', 'DOCTOR', '2023-06-20', 850.0, 250.0, 'NONE', 16, '11:00', '15:00', '11:00', '15:00', '11:00', '15:00', '11:00', '15:00', '11:00', '15:00', '10:00', '13:00', NULL, NULL, NOW() - interval '7 months', NOW(), 1),
('d0000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000007', 'c0000000-0000-0000-0000-000000000005', 'APPROVED', 'CLINIC', '2023-07-01', 600.0, 200.0, 'NONE', 30, '09:30', '13:30', '09:30', '13:30', '09:30', '13:30', '09:30', '13:30', '09:30', '13:30', '10:00', '14:00', NULL, NULL, NOW() - interval '6 months', NOW(), 1),
('d0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000008', 'c0000000-0000-0000-0000-000000000006', 'PENDING', 'DOCTOR', NULL, 750.0, 250.0, 'CLINIC', 20, '15:00', '18:00', '15:00', '18:00', '15:00', '18:00', '15:00', '18:00', '15:00', '18:00', NULL, NULL, NULL, NULL, NOW() - interval '5 days', NOW(), 0),
('d0000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000009', 'c0000000-0000-0000-0000-000000000007', 'APPROVED', 'DOCTOR', '2023-08-15', 650.0, 200.0, 'NONE', 22, '16:00', '20:00', '16:00', '20:00', '16:00', '20:00', '16:00', '20:00', '16:00', '20:00', '15:00', '19:00', NULL, NULL, NOW() - interval '5 months', NOW(), 1),
('d0000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000010', 'c0000000-0000-0000-0000-000000000008', 'APPROVED', 'CLINIC', '2023-09-01', 1100.0, 300.0, 'NONE', 10, '10:00', '14:00', '10:00', '14:00', '10:00', '14:00', '10:00', '14:00', '10:00', '14:00', NULL, NULL, NULL, NULL, NOW() - interval '4 months', NOW(), 1)
ON CONFLICT (id) DO NOTHING;

-- 5. AFFILIATION_NEGOTIATION_HISTORY (10 rows)
INSERT INTO affiliation_negotiation_history (id, affiliation_id, doctor_charge, clinic_charge, daily_patient_limit, status, actor, created_at) VALUES
('e0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', 1200.0, 300.0, 20, 'PENDING', 'DOCTOR', NOW() - interval '1 year 5 days'),
('e0000000-0000-0000-0000-000000000002', 'd0000000-0000-0000-0000-000000000001', 1000.0, 300.0, 20, 'APPROVED', 'CLINIC', NOW() - interval '1 year'),
('e0000000-0000-0000-0000-000000000003', 'd0000000-0000-0000-0000-000000000002', 800.0, 300.0, 15, 'PENDING', 'CLINIC', NOW() - interval '10 months 4 days'),
('e0000000-0000-0000-0000-000000000004', 'd0000000-0000-0000-0000-000000000002', 800.0, 250.0, 15, 'APPROVED', 'DOCTOR', NOW() - interval '10 months'),
('e0000000-0000-0000-0000-000000000005', 'd0000000-0000-0000-0000-000000000003', 1000.0, 300.0, 15, 'PENDING', 'DOCTOR', NOW() - interval '11 months 3 days'),
('e0000000-0000-0000-0000-000000000006', 'd0000000-0000-0000-0000-000000000003', 900.0, 300.0, 18, 'APPROVED', 'CLINIC', NOW() - interval '11 months'),
('e0000000-0000-0000-0000-000000000007', 'd0000000-0000-0000-0000-000000000004', 700.0, 200.0, 25, 'APPROVED', 'DOCTOR', NOW() - interval '9 months'),
('e0000000-0000-0000-0000-000000000008', 'd0000000-0000-0000-0000-000000000005', 1300.0, 450.0, 10, 'PENDING', 'CLINIC', NOW() - interval '8 months 5 days'),
('e0000000-0000-0000-0000-000000000009', 'd0000000-0000-0000-0000-000000000005', 1200.0, 400.0, 12, 'APPROVED', 'DOCTOR', NOW() - interval '8 months'),
('e0000000-0000-0000-0000-000000000010', 'd0000000-0000-0000-0000-000000000008', 750.0, 250.0, 20, 'PENDING', 'DOCTOR', NOW() - interval '5 days')
ON CONFLICT (id) DO NOTHING;

-- 6. APPOINTMENTS (10 rows)
INSERT INTO appointments (appointment_id, patient_id, affiliation_id, appointment_date, appointment_time, token_number, status, reason, version) VALUES
('f0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', CURRENT_DATE + interval '1 day', '10:30', 1, 'CONFIRMED', 'Routine heart checkup and blood pressure monitoring', 0),
('f0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 'd0000000-0000-0000-0000-000000000002', CURRENT_DATE + interval '1 day', '15:15', 2, 'CONFIRMED', 'Skin rash and allergy consultation', 0),
('f0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', 'd0000000-0000-0000-0000-000000000003', CURRENT_DATE + interval '2 days', '11:00', 3, 'PENDING', 'Knee pain after jogging', 0),
('f0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', 'd0000000-0000-0000-0000-000000000004', CURRENT_DATE + interval '2 days', '09:45', 1, 'CONFIRMED', 'Child vaccination and health checkup', 0),
('f0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000005', 'd0000000-0000-0000-0000-000000000005', CURRENT_DATE + interval '3 days', '14:30', 2, 'CONFIRMED', 'Severe migraine and dizziness', 0),
('f0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', 'd0000000-0000-0000-0000-000000000006', CURRENT_DATE + interval '3 days', '11:30', 4, 'CONFIRMED', 'Pregnancy first trimester checkup', 0),
('f0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000007', 'd0000000-0000-0000-0000-000000000007', CURRENT_DATE - interval '1 day', '10:00', 5, 'COMPLETED', 'Persistent viral fever and body ache', 1),
('f0000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000008', 'd0000000-0000-0000-0000-000000000001', CURRENT_DATE - interval '2 days', '12:00', 6, 'COMPLETED', 'Chest discomfort evaluation', 1),
('f0000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000009', 'd0000000-0000-0000-0000-000000000009', CURRENT_DATE + interval '4 days', '16:30', 1, 'CONFIRMED', 'Ear congestion and hearing issue', 0),
('f0000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000010', 'd0000000-0000-0000-0000-000000000010', CURRENT_DATE + interval '5 days', '10:30', 2, 'CANCELLED', 'Follow-up anxiety consultation', 1)
ON CONFLICT (appointment_id) DO NOTHING;

-- 7. PRESCRIPTIONS (10 rows)
INSERT INTO prescriptions (id, patient_id, doctor_id, diagnosis, notes, is_active, issued_at) VALUES
('10000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Mild Hypertension Stage 1', 'Low sodium diet and 30 mins brisk walking daily', true, NOW() - interval '2 days'),
('10000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002', 'Contact Dermatitis', 'Avoid perfumed soaps and cosmetic chemicals', true, NOW() - interval '3 days'),
('10000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000003', 'Patellar Tendinitis', 'Ice compression and physiotherapy recommended', true, NOW() - interval '4 days'),
('10000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000004', 'Seasonal Flu / Pharyngitis', 'Plenty of warm fluids and saline gargling', true, NOW() - interval '5 days'),
('10000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000005', 'Episodic Migraine', 'Maintain regular sleep routine; avoid screen triggers', true, NOW() - interval '6 days'),
('10000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000006', 'Antenatal Care - Week 12', 'Folic acid supplements continued', true, NOW() - interval '7 days'),
('10000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000007', 'Acute Viral Bronchitis', 'Steam inhalation 3 times daily', true, NOW() - interval '1 day'),
('10000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000008', 'Allergic Conjunctivitis', 'Avoid rubbing eyes and wear protective sunglasses', true, NOW() - interval '8 days'),
('10000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000009', 'Middle Ear Infection (Otitis Media)', 'Keep ear dry while bathing', true, NOW() - interval '9 days'),
('10000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000010', 'Mild Anxiety Disorder', 'Cognitive behavioral therapy exercises given', true, NOW() - interval '10 days')
ON CONFLICT (id) DO NOTHING;

-- 8. PRESCRIPTION_ITEMS (10 rows)
INSERT INTO prescription_items (id, prescription_id, medication_name, dosage, frequency, started_at, duration_value, duration_unit, instructions, is_active, discontinue_reason) VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Telmisartan', '40 mg', 'Once daily', CURRENT_DATE - interval '2 days', 30, 'DAY', 'Take in the morning before breakfast', true, NULL),
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'Cetirizine', '10 mg', 'Once daily at night', CURRENT_DATE - interval '3 days', 7, 'DAY', 'Take after food at bedtime', true, NULL),
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 'Aceclofenac + Paracetamol', '100mg/325mg', 'Twice daily', CURRENT_DATE - interval '4 days', 5, 'DAY', 'Strictly after food with a glass of water', true, NULL),
('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', 'Paracetamol Syrup', '250 mg/5ml', 'Thrice daily if fever > 100 F', CURRENT_DATE - interval '5 days', 3, 'DAY', 'Use measuring cap provided', true, NULL),
('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000005', 'Naproxen', '500 mg', 'SOS during headache onset', CURRENT_DATE - interval '6 days', 15, 'DAY', 'Do not exceed 2 tablets in 24 hours', true, NULL),
('20000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000006', 'Folic Acid + Methylcobalamin', '5 mg', 'Once daily', CURRENT_DATE - interval '7 days', 90, 'DAY', 'Take with afternoon meal', true, NULL),
('20000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000007', 'Azithromycin', '500 mg', 'Once daily', CURRENT_DATE - interval '1 day', 3, 'DAY', 'Complete full 3-day course', true, NULL),
('20000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000008', 'Olopatadine Eye Drops', '0.1%', 'One drop twice daily', CURRENT_DATE - interval '8 days', 14, 'DAY', 'Instill 1 drop in each eye morning and night', true, NULL),
('20000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000009', 'Amoxicillin + Clavulanic Acid', '625 mg', 'Twice daily', CURRENT_DATE - interval '9 days', 5, 'DAY', 'Take after food at 12-hour intervals', true, NULL),
('20000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000010', 'Escitalopram', '10 mg', 'Once daily in the morning', CURRENT_DATE - interval '10 days', 30, 'DAY', 'Do not discontinue without consulting doctor', true, NULL)
ON CONFLICT (id) DO NOTHING;

-- 9. VITALS (10 rows)
INSERT INTO vitals (id, patient_id, systolic_bp, diastolic_bp, heart_rate, weight, temperature, height_cm, bmi, recorded_at) VALUES
('30000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 135, 88, 74, 75.5, 98.4, 175.0, 24.65, NOW() - interval '2 days'),
('30000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 118, 76, 70, 58.0, 98.6, 162.0, 22.10, NOW() - interval '3 days'),
('30000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', 122, 80, 78, 82.0, 98.2, 178.0, 25.88, NOW() - interval '4 days'),
('30000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', 110, 70, 85, 52.0, 99.1, 158.0, 20.83, NOW() - interval '5 days'),
('30000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000005', 120, 82, 80, 68.0, 98.6, 172.0, 22.99, NOW() - interval '6 days'),
('30000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', 114, 72, 76, 61.0, 98.5, 164.0, 22.68, NOW() - interval '7 days'),
('30000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000007', 128, 84, 88, 79.0, 100.2, 174.0, 26.09, NOW() - interval '1 day'),
('30000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000008', 116, 75, 72, 54.0, 98.4, 160.0, 21.09, NOW() - interval '8 days'),
('30000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000009', 124, 78, 75, 73.0, 98.8, 170.0, 25.26, NOW() - interval '9 days'),
('30000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000010', 120, 80, 82, 60.0, 98.6, 165.0, 22.04, NOW() - interval '10 days')
ON CONFLICT (id) DO NOTHING;

-- 10. HEALTH_RECORDS (10 rows)
INSERT INTO health_records (id, patient_id, document_title, document_type, document_url, test_date, uploaded_at) VALUES
('40000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Lipid Profile & ECG', 'LAB_REPORT', 'https://storage.khoj.health/records/ecg_aarav_001.pdf', '2026-09-20', NOW() - interval '14 days'),
('40000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 'Skin Allergy IgE Panel', 'LAB_REPORT', 'https://storage.khoj.health/records/allergy_diya_002.pdf', '2026-09-25', NOW() - interval '9 days'),
('40000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', 'Right Knee MRI Scan', 'MRI_SCAN', 'https://storage.khoj.health/records/mri_rohan_003.pdf', '2026-09-28', NOW() - interval '6 days'),
('40000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', 'Complete Blood Count (CBC)', 'LAB_REPORT', 'https://storage.khoj.health/records/cbc_ananya_004.pdf', '2026-09-15', NOW() - interval '19 days'),
('40000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000005', 'Brain CT Scan', 'CT_SCAN', 'https://storage.khoj.health/records/ct_vihaan_005.pdf', '2026-09-10', NOW() - interval '24 days'),
('40000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', 'First Trimester Ultrasound', 'ULTRASOUND', 'https://storage.khoj.health/records/usg_ishita_006.pdf', '2026-09-22', NOW() - interval '12 days'),
('40000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000007', 'Dengue & Malaria Serology', 'LAB_REPORT', 'https://storage.khoj.health/records/fever_kabir_007.pdf', '2026-10-01', NOW() - interval '3 days'),
('40000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000008', 'Dilated Eye Exam Report', 'CLINICAL_SUMMARY', 'https://storage.khoj.health/records/eye_sanya_008.pdf', '2026-09-18', NOW() - interval '16 days'),
('40000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000009', 'Pure Tone Audiometry', 'DIAGNOSTIC', 'https://storage.khoj.health/records/audio_arjun_009.pdf', '2026-09-26', NOW() - interval '8 days'),
('40000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000010', 'Thyroid Profile (T3, T4, TSH)', 'LAB_REPORT', 'https://storage.khoj.health/records/thyroid_meera_010.pdf', '2026-09-30', NOW() - interval '4 days')
ON CONFLICT (id) DO NOTHING;

-- 11. NOTIFICATIONS (10 rows)
INSERT INTO notifications (id, user_id, title, message, type, is_read, created_at) VALUES
('50000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Appointment Confirmed', 'Your appointment with Dr. Rajesh Gupta at Apollo Clinic Bandra is confirmed for tomorrow at 10:30 AM.', 'APPOINTMENT_REMINDER', false, NOW() - interval '1 day'),
('50000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'New Appointment Booked', 'Patient Aarav Sharma has booked a consultation for tomorrow at 10:30 AM.', 'INFO', true, NOW() - interval '1 day'),
('50000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001', 'Affiliation Request Approved', 'Dr. Rajesh Gupta approved the affiliation schedule.', 'ACTION_REQUIRED', true, NOW() - interval '2 days'),
('50000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000002', 'Prescription Issued', 'Dr. Priya Deshmukh has uploaded your prescription for Contact Dermatitis.', 'INFO', false, NOW() - interval '3 hours'),
('50000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000008', 'Pending Affiliation', 'You have a pending affiliation request from Medanta Mediclinic.', 'AFFILIATION_REQUEST', false, NOW() - interval '5 days'),
('50000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000007', 'Lab Results Ready', 'Your Dengue & Malaria serology report is now available in your health records.', 'ALERT', true, NOW() - interval '2 days'),
('50000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000005', 'Appointment Reminder', 'Reminder: Consultation with Dr. Vikram Chawla in 3 days.', 'APPOINTMENT_REMINDER', false, NOW() - interval '12 hours'),
('50000000-0000-0000-0000-000000000008', 'c0000000-0000-0000-0000-000000000002', 'Schedule Updated', 'Dr. Amit Bose updated available consultation timings for next week.', 'INFO', true, NOW() - interval '4 days'),
('50000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000003', 'Follow-up Recommended', 'Dr. Amit Bose recommends a follow-up physiotherapy checkup next week.', 'INFO', false, NOW() - interval '1 day'),
('50000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000004', 'Vaccination Schedule', 'Second dose for seasonal immunization is scheduled in 2 weeks.', 'APPOINTMENT_REMINDER', false, NOW() - interval '2 days')
ON CONFLICT (id) DO NOTHING;

-- 12. REFRESH_TOKENS (10 rows)
INSERT INTO refresh_tokens (id, token, user_id, username, expiry_date, revoked, created_at) VALUES
('60000000-0000-0000-0000-000000000001', 'token_aarav_sharma_9876543210_sample_01', 'a0000000-0000-0000-0000-000000000001', 'aarav.sharma', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000002', 'token_diya_patel_9876543212_sample_02', 'a0000000-0000-0000-0000-000000000002', 'diya.patel', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000003', 'token_rohan_mehta_9876543214_sample_03', 'a0000000-0000-0000-0000-000000000003', 'rohan.mehta', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000004', 'token_ananya_iyer_9876543216_sample_04', 'a0000000-0000-0000-0000-000000000004', 'ananya.iyer', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000005', 'token_dr_rajesh_gupta_sample_05', 'b0000000-0000-0000-0000-000000000001', 'dr.rajesh.gupta', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000006', 'token_dr_priya_deshmukh_sample_06', 'b0000000-0000-0000-0000-000000000002', 'dr.priya.deshmukh', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000007', 'token_dr_amit_bose_sample_07', 'b0000000-0000-0000-0000-000000000003', 'dr.amit.bose', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000008', 'token_apollo_bandra_clinic_08', 'c0000000-0000-0000-0000-000000000001', 'apollo.bandra', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000009', 'token_fortis_kora_clinic_09', 'c0000000-0000-0000-0000-000000000002', 'fortis.koramangala', NOW() + interval '7 days', false, NOW()),
('60000000-0000-0000-0000-000000000010', 'token_max_saket_clinic_10', 'c0000000-0000-0000-0000-000000000003', 'max.saket', NOW() + interval '7 days', false, NOW())
ON CONFLICT (id) DO NOTHING;
