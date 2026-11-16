CREATE DATABASE IF NOT EXISTS livestock_disease_tracker;
USE livestock_disease_tracker;

CREATE TABLE IF NOT EXISTS diseases (
    disease_id         INT           AUTO_INCREMENT PRIMARY KEY,
    disease_name       VARCHAR(100)  NOT NULL UNIQUE,
    description        TEXT,
    recommended_action TEXT
);

CREATE TABLE IF NOT EXISTS regions (
    region_id    INT          AUTO_INCREMENT PRIMARY KEY,
    region_name  VARCHAR(100) NOT NULL,
    county_name  VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS disease_reports (
    report_id       INT          AUTO_INCREMENT PRIMARY KEY,
    disease_id      INT          NOT NULL,
    region_id       INT          NOT NULL,
    livestock_type  VARCHAR(50)  NOT NULL,
    report_date     DATE         NOT NULL,
    affected_count  INT          NOT NULL CHECK (affected_count > 0),
    report_status   VARCHAR(20)  NOT NULL DEFAULT 'suspected',
    notes           TEXT,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (disease_id) REFERENCES diseases(disease_id),
    FOREIGN KEY (region_id)  REFERENCES regions(region_id)
);

INSERT INTO diseases (disease_name, description, recommended_action) VALUES
('Foot and Mouth Disease', 'Highly contagious viral disease affecting cloven-hoofed animals.', 'Isolate affected animals and contact a licensed veterinarian immediately.'),
('East Coast Fever', 'Tick-borne disease caused by Theileria parva, common in East Africa.', 'Consult a veterinarian for tick-control and treatment options.'),
('Newcastle Disease', 'Viral disease affecting poultry with high mortality rates.', 'Vaccinate flock and report to local animal-health officer.'),
('Lumpy Skin Disease', 'Viral disease in cattle causing skin nodules and fever.', 'Seek veterinary advice; vaccination is available.'),
('Anthrax', 'Bacterial disease that can affect most mammals including livestock.', 'Do not move animals; contact animal-health authorities immediately.');

INSERT INTO regions (region_name, county_name) VALUES
('Nakuru North', 'Nakuru'),
('Naivasha', 'Nakuru'),
('Eldoret East', 'Uasin Gishu'),
('Kitale Central', 'Trans Nzoia'),
('Thika Town', 'Kiambu'),
('Machakos East', 'Machakos');