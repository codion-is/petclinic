create user if not exists scott password 'tiger';
alter user scott admin true;

CREATE SCHEMA petclinic;
-- tag::vet[]
CREATE TABLE petclinic.vet (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL
);
-- end::vet[]
-- tag::specialty[]
CREATE TABLE petclinic.specialty (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(80) NOT NULL
);
-- end::specialty[]
-- tag::vet_specialty[]
CREATE TABLE petclinic.vet_specialty (
  vet INTEGER NOT NULL,
  specialty INTEGER NOT NULL,
  CONSTRAINT fk_vet_specialty_vet FOREIGN KEY (vet)
      REFERENCES petclinic.vet (id),
  CONSTRAINT fk_vet_specialty_specialty FOREIGN KEY (specialty)
      REFERENCES petclinic.specialty (id)
);
-- end::vet_specialty[]
-- tag::pet_type[]
CREATE TABLE petclinic.pet_type (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  CONSTRAINT pet_type_uk UNIQUE (name)
);
-- end::pet_type[]
-- tag::owner[]
CREATE TABLE petclinic.owner (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  address VARCHAR(255),
  city VARCHAR(80),
  telephone VARCHAR(20),
  phone_type VARCHAR(20) NOT NULL
);
-- end::owner[]
-- tag::pet[]
CREATE TABLE petclinic.pet (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  birth_date DATE,
  type_id INTEGER NOT NULL,
  owner_id INTEGER NOT NULL,
  CONSTRAINT fk_pet_owners FOREIGN KEY (owner_id)
      REFERENCES owner (id),
  CONSTRAINT fk_pet_pet_type FOREIGN KEY (type_id)
      REFERENCES pet_type (id)
);
-- end::pet[]
-- tag::visit[]
CREATE TABLE petclinic.visit (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  pet_id INTEGER NOT NULL,
  visit_date DATE NOT NULL,
  vet_id INTEGER NOT NULL,
  description VARCHAR(255),
  CONSTRAINT fk_visit_pets FOREIGN KEY (pet_id)
      REFERENCES pet (id),
  CONSTRAINT fk_visit_vets FOREIGN KEY (vet_id)
      REFERENCES vet (id)
);
-- end::visit[]

INSERT INTO petclinic.vet(first_name, last_name) VALUES ('James', 'Carter');
INSERT INTO petclinic.vet(first_name, last_name) VALUES ('Helen', 'Leary');
INSERT INTO petclinic.vet(first_name, last_name) VALUES ('Linda', 'Douglas');
INSERT INTO petclinic.vet(first_name, last_name) VALUES ('Rafael', 'Ortega');
INSERT INTO petclinic.vet(first_name, last_name) VALUES ('Henry', 'Stevens');
INSERT INTO petclinic.vet(first_name, last_name) VALUES ('Sharon', 'Jenkins');

INSERT INTO petclinic.specialty(name) VALUES ('radiology');
INSERT INTO petclinic.specialty(name) VALUES ('surgery');
INSERT INTO petclinic.specialty(name) VALUES ('dentistry');

INSERT INTO petclinic.vet_specialty VALUES (2, 1);
INSERT INTO petclinic.vet_specialty VALUES (3, 2);
INSERT INTO petclinic.vet_specialty VALUES (3, 3);
INSERT INTO petclinic.vet_specialty VALUES (4, 2);
INSERT INTO petclinic.vet_specialty VALUES (5, 1);

INSERT INTO petclinic.pet_type(name) VALUES ('cat');
INSERT INTO petclinic.pet_type(name) VALUES ('dog');
INSERT INTO petclinic.pet_type(name) VALUES ('lizard');
INSERT INTO petclinic.pet_type(name) VALUES ('snake');
INSERT INTO petclinic.pet_type(name) VALUES ('bird');
INSERT INTO petclinic.pet_type(name) VALUES ('hamster');

INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023', 'MOBILE');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749', 'MOBILE');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763', 'HOME');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198', 'MOBILE');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765', 'HOME');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654', 'WORK');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387', 'WORK');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683', 'MOBILE');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435', 'HOME');
INSERT INTO petclinic.owner(first_name, last_name, address, city, telephone, phone_type) VALUES ('Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487', 'WORK');

INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Leo', '2010-09-07', 1, 1);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Basil', '2012-08-06', 6, 2);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Rosy', '2011-04-17', 2, 3);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Jewel', '2010-03-07', 2, 3);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Iggy', '2010-11-30', 3, 4);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('George', '2010-01-20', 4, 5);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Samantha', '2012-09-04', 1, 6);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Max', '2012-09-04', 1, 6);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Lucky', '2011-08-06', 5, 7);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Mulligan', '2007-02-24', 2, 8);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Freddy', '2010-03-09', 5, 9);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Lucky', '2010-06-24', 2, 10);
INSERT INTO petclinic.pet(name, birth_date, type_id, owner_id) VALUES ('Sly', '2012-06-08', 1, 10);

INSERT INTO petclinic.visit(pet_id, visit_date, vet_id, description) VALUES (7, '2013-01-01', 1, 'rabies shot');
INSERT INTO petclinic.visit(pet_id, visit_date, vet_id, description) VALUES (8, '2013-01-02', 2, 'rabies shot');
INSERT INTO petclinic.visit(pet_id, visit_date, vet_id, description) VALUES (8, '2013-01-03', 3, 'neutered');
INSERT INTO petclinic.visit(pet_id, visit_date, vet_id, description) VALUES (7, '2013-01-04', 4, 'spayed');