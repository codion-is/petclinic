/*
 * This file is part of Codion Petclinic Demo.
 *
 * Codion Petclinic Demo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codion Petclinic Demo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codion Petclinic Demo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2004 - 2023, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.petclinic.domain;

import is.codion.framework.demos.petclinic.domain.Petclinic.Owner.PhoneType;
import is.codion.framework.domain.DefaultDomain;
import is.codion.framework.domain.DomainType;
import is.codion.framework.domain.entity.Column;
import is.codion.framework.domain.entity.EntityType;
import is.codion.framework.domain.entity.ForeignKey;
import is.codion.framework.domain.entity.OrderBy;
import is.codion.framework.domain.entity.StringFactory;
import is.codion.framework.domain.property.ColumnProperty.ValueConverter;

import java.sql.Statement;
import java.time.LocalDate;

import static is.codion.framework.domain.DomainType.domainType;
import static is.codion.framework.domain.entity.EntityDefinition.definition;
import static is.codion.framework.domain.entity.KeyGenerator.identity;
import static is.codion.framework.domain.entity.OrderBy.ascending;
import static is.codion.framework.domain.property.Property.*;

// tag::petclinic[]
public final class Petclinic extends DefaultDomain {

  public static final DomainType DOMAIN = domainType("Petclinic");

  public Petclinic() {
    super(DOMAIN);
    vet();
    specialty();
    vetSpecialty();
    petType();
    owner();
    pet();
    visit();
  }
  // end::petclinic[]

  // tag::vet_api[]
  public interface Vet {
    EntityType TYPE = DOMAIN.entityType("petclinic.vet");

    Column<Integer> ID = TYPE.integerColumn("id");
    Column<String> FIRST_NAME = TYPE.stringColumn("first_name");
    Column<String> LAST_NAME = TYPE.stringColumn("last_name");
  }
  // end::vet_api[]

  // tag::vet_impl[]
  private void vet() {
    add(definition(
            primaryKeyProperty(Vet.ID),
            columnProperty(Vet.FIRST_NAME, "First name")
                    .searchProperty(true)
                    .maximumLength(30)
                    .nullable(false),
            columnProperty(Vet.LAST_NAME, "Last name")
                    .searchProperty(true)
                    .maximumLength(30)
                    .nullable(false))
            .keyGenerator(identity())
            .caption("Vets")
            .stringFactory(StringFactory.builder()
                    .value(Vet.LAST_NAME)
                    .text(", ")
                    .value(Vet.FIRST_NAME)
                    .build())
            .orderBy(ascending(Vet.LAST_NAME, Vet.FIRST_NAME))
            .smallDataset(true));
  }
  // end::vet_impl[]

  // tag::specialty_api[]
  public interface Specialty {
    EntityType TYPE = DOMAIN.entityType("petclinic.specialty");

    Column<Integer> ID = TYPE.integerColumn("id");
    Column<String> NAME = TYPE.stringColumn("name");
  }
  // end::specialty_api[]

  // tag::specialty_impl[]
  private void specialty() {
    add(definition(
            primaryKeyProperty(Specialty.ID),
            columnProperty(Specialty.NAME, "Name")
                    .searchProperty(true)
                    .maximumLength(80)
                    .nullable(false))
            .keyGenerator(identity())
            .caption("Specialties")
            .stringFactory(Specialty.NAME)
            .smallDataset(true));
  }
  // end::specialty_impl[]

  // tag::vet_specialty_api[]
  public interface VetSpecialty {
    EntityType TYPE = DOMAIN.entityType("petclinic.vet_specialty");

    Column<Integer> VET = TYPE.integerColumn("vet");
    Column<Integer> SPECIALTY = TYPE.integerColumn("specialty");

    ForeignKey VET_FK = TYPE.foreignKey("vet_fk", VET, Vet.ID);
    ForeignKey SPECIALTY_FK = TYPE.foreignKey("specialty_fk", SPECIALTY, Specialty.ID);
  }
  // end::vet_specialty_api[]

  // tag::vet_specialty_impl[]
  private void vetSpecialty() {
    add(definition(
            columnProperty(VetSpecialty.VET)
                    .primaryKeyIndex(0),
            columnProperty(VetSpecialty.SPECIALTY)
                    .primaryKeyIndex(1),
            foreignKeyProperty(VetSpecialty.VET_FK, "Vet"),
            foreignKeyProperty(VetSpecialty.SPECIALTY_FK, "Specialty"))
            .caption("Vet specialties")
            .stringFactory(StringFactory.builder()
                    .value(VetSpecialty.VET_FK)
                    .text(" - ")
                    .value(VetSpecialty.SPECIALTY_FK)
                    .build()));
  }
  // end::vet_specialty_impl[]

  // tag::pet_type_api[]
  public interface PetType {
    EntityType TYPE = DOMAIN.entityType("petclinic.pet_type");

    Column<Integer> ID = TYPE.integerColumn("id");
    Column<String> NAME = TYPE.stringColumn("name");
  }
  // end::pet_type_api[]

  // tag::pet_type_impl[]
  private void petType() {
    add(definition(
            primaryKeyProperty(PetType.ID),
            columnProperty(PetType.NAME, "Name")
                    .searchProperty(true)
                    .maximumLength(80)
                    .nullable(false))
            .keyGenerator(identity())
            .caption("Pet types")
            .stringFactory(PetType.NAME)
            .orderBy(ascending(PetType.NAME))
            .smallDataset(true));
  }
  // end::pet_type_impl[]

  // tag::owner_api[]
  public interface Owner {
    EntityType TYPE = DOMAIN.entityType("petclinic.owner");

    Column<Integer> ID = TYPE.integerColumn("id");
    Column<String> FIRST_NAME = TYPE.stringColumn("first_name");
    Column<String> LAST_NAME = TYPE.stringColumn("last_name");
    Column<String> ADDRESS = TYPE.stringColumn("address");
    Column<String> CITY = TYPE.stringColumn("city");
    Column<String> TELEPHONE = TYPE.stringColumn("telephone");
    Column<PhoneType> PHONE_TYPE = TYPE.column("phone_type", PhoneType.class);

    enum PhoneType {
      MOBILE, HOME, WORK
    }
  }
  // end::owner_api[]

  // tag::owner_impl[]
  private void owner() {
    add(definition(
            primaryKeyProperty(Owner.ID),
            columnProperty(Owner.FIRST_NAME, "First name")
                    .searchProperty(true)
                    .maximumLength(30)
                    .nullable(false),
            columnProperty(Owner.LAST_NAME, "Last name")
                    .searchProperty(true)
                    .maximumLength(30)
                    .nullable(false),
            columnProperty(Owner.ADDRESS, "Address")
                    .maximumLength(255),
            columnProperty(Owner.CITY, "City")
                    .maximumLength(80),
            columnProperty(Owner.TELEPHONE, "Telephone")
                    .maximumLength(20),
            columnProperty(Owner.PHONE_TYPE, "Phone type")
                    .columnClass(String.class, new PhoneTypeValueConverter()))
            .keyGenerator(identity())
            .caption("Owners")
            .stringFactory(StringFactory.builder()
                    .value(Owner.LAST_NAME)
                    .text(", ")
                    .value(Owner.FIRST_NAME)
                    .build())
            .orderBy(ascending(Owner.LAST_NAME, Owner.FIRST_NAME)));
  }

  private static final class PhoneTypeValueConverter implements ValueConverter<PhoneType, String> {

    @Override
    public String toColumnValue(PhoneType value, Statement statement) {
      return value.name();
    }

    @Override
    public PhoneType fromColumnValue(String columnValue) {
      return PhoneType.valueOf(columnValue);
    }
  }
  // end::owner_impl[]

  // tag::pet_api[]
  public interface Pet {
    EntityType TYPE = DOMAIN.entityType("petclinic.pet");

    Column<Integer> ID = TYPE.integerColumn("id");
    Column<String> NAME = TYPE.stringColumn("name");
    Column<LocalDate> BIRTH_DATE = TYPE.localDateColumn("birth_date");
    Column<Integer> PET_TYPE_ID = TYPE.integerColumn("type_id");
    Column<Integer> OWNER_ID = TYPE.integerColumn("owner_id");

    ForeignKey PET_TYPE_FK = TYPE.foreignKey("type_fk", PET_TYPE_ID, PetType.ID);
    ForeignKey OWNER_FK = TYPE.foreignKey("owner_fk", OWNER_ID, Owner.ID);
  }
  // end::pet_api[]

  // tag::pet_impl[]
  private void pet() {
    add(definition(
            primaryKeyProperty(Pet.ID),
            columnProperty(Pet.NAME, "Name")
                    .searchProperty(true)
                    .maximumLength(30)
                    .nullable(false),
            columnProperty(Pet.BIRTH_DATE, "Birth date")
                    .nullable(false),
            columnProperty(Pet.PET_TYPE_ID)
                    .nullable(false),
            foreignKeyProperty(Pet.PET_TYPE_FK, "Pet type"),
            columnProperty(Pet.OWNER_ID)
                    .nullable(false),
            foreignKeyProperty(Pet.OWNER_FK, "Owner"))
            .keyGenerator(identity())
            .caption("Pets")
            .stringFactory(Pet.NAME)
            .orderBy(ascending(Pet.NAME)));
  }
  // end::pet_impl[]

  // tag::visit_api[]
  public interface Visit {
    EntityType TYPE = DOMAIN.entityType("petclinic.visit");

    Column<Integer> ID = TYPE.integerColumn("id");
    Column<Integer> PET_ID = TYPE.integerColumn("pet_id");
    Column<LocalDate> VISIT_DATE = TYPE.localDateColumn("visit_date");
    Column<String> DESCRIPTION = TYPE.stringColumn("description");

    ForeignKey PET_FK = TYPE.foreignKey("pet_fk", PET_ID, Pet.ID);
  }
  // end::visit_api[]

  // tag::visit_impl[]
  private void visit() {
    add(definition(
            primaryKeyProperty(Visit.ID),
            columnProperty(Visit.PET_ID)
                    .nullable(false),
            foreignKeyProperty(Visit.PET_FK, "Pet"),
            columnProperty(Visit.VISIT_DATE, "Date")
                    .nullable(false),
            columnProperty(Visit.DESCRIPTION, "Description")
                    .maximumLength(255))
            .keyGenerator(identity())
            .orderBy(OrderBy.builder()
                    .ascending(Visit.PET_ID)
                    .descending(Visit.VISIT_DATE)
                    .build())
            .caption("Visits"));
  }
  // end::visit_impl[]
}
