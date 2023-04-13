/*
 * Copyright (c) 2004 - 2023, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.petclinic.domain;

import is.codion.framework.domain.DefaultDomain;
import is.codion.framework.domain.DomainType;
import is.codion.framework.domain.entity.Attribute;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.EntityType;
import is.codion.framework.domain.entity.ForeignKey;
import is.codion.framework.domain.entity.OrderBy;
import is.codion.framework.domain.entity.StringFactory;

import java.time.LocalDate;

import static is.codion.framework.domain.entity.EntityDefinition.definition;
import static is.codion.framework.domain.entity.KeyGenerator.identity;
import static is.codion.framework.domain.entity.OrderBy.ascending;
import static is.codion.framework.domain.property.Property.*;

public final class Petclinic extends DefaultDomain {

  private static final DomainType DOMAIN = DomainType.domainType("Petclinic");

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

  public interface Vet extends Entity {
    EntityType TYPE = DOMAIN.entityType("petclinic.vet", Vet.class);

    Attribute<Integer> ID = TYPE.integerAttribute("id");
    Attribute<String> FIRST_NAME = TYPE.stringAttribute("first_name");
    Attribute<String> LAST_NAME = TYPE.stringAttribute("last_name");
  }

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

  public interface Specialty extends Entity {
    EntityType TYPE = DOMAIN.entityType("petclinic.specialty", Specialty.class);

    Attribute<Integer> ID = TYPE.integerAttribute("id");
    Attribute<String> NAME = TYPE.stringAttribute("name");
  }

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

  public interface VetSpecialty extends Entity {
    EntityType TYPE = DOMAIN.entityType("petclinic.vet_specialty", VetSpecialty.class);

    Attribute<Integer> VET = TYPE.integerAttribute("vet");
    Attribute<Integer> SPECIALTY = TYPE.integerAttribute("specialty");

    ForeignKey VET_FK = TYPE.foreignKey("vet_fk", VET, Vet.ID);
    ForeignKey SPECIALTY_FK = TYPE.foreignKey("specialty_fk", SPECIALTY, Specialty.ID);
  }

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

  public interface PetType extends Entity {
    EntityType TYPE = DOMAIN.entityType("petclinic.pet_type", PetType.class);

    Attribute<Integer> ID = TYPE.integerAttribute("id");
    Attribute<String> NAME = TYPE.stringAttribute("name");
  }

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


  public interface Owner extends Entity {
    EntityType TYPE = DOMAIN.entityType("petclinic.owner", Owner.class);

    Attribute<Integer> ID = TYPE.integerAttribute("id");
    Attribute<String> FIRST_NAME = TYPE.stringAttribute("first_name");
    Attribute<String> LAST_NAME = TYPE.stringAttribute("last_name");
    Attribute<String> ADDRESS = TYPE.stringAttribute("address");
    Attribute<String> CITY = TYPE.stringAttribute("city");
    Attribute<String> TELEPHONE = TYPE.stringAttribute("telephone");
  }

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
                    .maximumLength(20))
            .keyGenerator(identity())
            .caption("Owners")
            .stringFactory(StringFactory.builder()
                    .value(Owner.LAST_NAME)
                    .text(", ")
                    .value(Owner.FIRST_NAME)
                    .build())
            .orderBy(ascending(Owner.LAST_NAME, Owner.FIRST_NAME)));
  }

  public interface Pet extends Entity {
    EntityType TYPE = DOMAIN.entityType("petclinic.pet", Pet.class);

    Attribute<Integer> ID = TYPE.integerAttribute("id");
    Attribute<String> NAME = TYPE.stringAttribute("name");
    Attribute<LocalDate> BIRTH_DATE = TYPE.localDateAttribute("birth_date");
    Attribute<Integer> PET_TYPE_ID = TYPE.integerAttribute("type_id");
    Attribute<Integer> OWNER_ID = TYPE.integerAttribute("owner_id");

    ForeignKey PET_TYPE_FK = TYPE.foreignKey("type_fk", PET_TYPE_ID, PetType.ID);
    ForeignKey OWNER_FK = TYPE.foreignKey("owner_fk", OWNER_ID, Owner.ID);
  }

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

  public interface Visit extends Entity {
    EntityType TYPE = DOMAIN.entityType("petclinic.visit", Visit.class);

    Attribute<Integer> ID = TYPE.integerAttribute("id");
    Attribute<Integer> PET_ID = TYPE.integerAttribute("pet_id");
    Attribute<LocalDate> DATE = TYPE.localDateAttribute("\"date\"");
    Attribute<String> DESCRIPTION = TYPE.stringAttribute("description");

    ForeignKey PET_FK = TYPE.foreignKey("pet_fk", PET_ID, Pet.ID);
  }

  private void visit() {
    add(definition(
            primaryKeyProperty(Visit.ID),
            columnProperty(Visit.PET_ID)
                    .nullable(false),
            foreignKeyProperty(Visit.PET_FK, "Pet"),
            columnProperty(Visit.DATE, "Date")
                    .nullable(false),
            columnProperty(Visit.DESCRIPTION, "Description")
                    .maximumLength(255))
            .keyGenerator(identity())
            .orderBy(OrderBy.builder()
                    .ascending(Visit.PET_ID)
                    .descending(Visit.DATE)
                    .build())
            .caption("Visits"));
  }
}
