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
 * Copyright (c) 2004 - 2024, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.petclinic.domain;

import is.codion.framework.demos.petclinic.domain.Petclinic.Owner.PhoneType;
import is.codion.framework.domain.DomainModel;
import is.codion.framework.domain.DomainType;
import is.codion.framework.domain.entity.EntityDefinition;
import is.codion.framework.domain.entity.EntityType;
import is.codion.framework.domain.entity.OrderBy;
import is.codion.framework.domain.entity.StringFactory;
import is.codion.framework.domain.entity.attribute.Column;
import is.codion.framework.domain.entity.attribute.Column.Converter;
import is.codion.framework.domain.entity.attribute.ForeignKey;

import java.sql.Statement;
import java.time.LocalDate;

import static is.codion.framework.domain.DomainType.domainType;
import static is.codion.framework.domain.entity.KeyGenerator.identity;
import static is.codion.framework.domain.entity.OrderBy.ascending;

// tag::petclinic[]
public final class Petclinic extends DomainModel {

	public static final DomainType DOMAIN = domainType("Petclinic");

	public Petclinic() {
		super(DOMAIN);
		add(vet(), specialty(), vetSpecialty(), petType(), owner(), pet(), visit());
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
	private EntityDefinition vet() {
		return Vet.TYPE.define(
										Vet.ID.define()
														.primaryKey(),
										Vet.FIRST_NAME.define()
														.column()
														.caption("First name")
														.searchable(true)
														.maximumLength(30)
														.nullable(false),
										Vet.LAST_NAME.define()
														.column()
														.caption("Last name")
														.searchable(true)
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
						.smallDataset(true)
						.build();
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
	private EntityDefinition specialty() {
		return Specialty.TYPE.define(
										Specialty.ID.define()
														.primaryKey(),
										Specialty.NAME.define()
														.column()
														.caption("Name")
														.searchable(true)
														.maximumLength(80)
														.nullable(false))
						.keyGenerator(identity())
						.caption("Specialties")
						.stringFactory(Specialty.NAME)
						.smallDataset(true)
						.build();
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
	private EntityDefinition vetSpecialty() {
		return VetSpecialty.TYPE.define(
										VetSpecialty.VET.define()
														.primaryKey(0)
														.updatable(true),
										VetSpecialty.SPECIALTY.define()
														.primaryKey(1)
														.updatable(true),
										VetSpecialty.VET_FK.define()
														.foreignKey()
														.caption("Vet"),
										VetSpecialty.SPECIALTY_FK.define()
														.foreignKey()
														.caption("Specialty"))
						.caption("Vet specialties")
						.stringFactory(StringFactory.builder()
										.value(VetSpecialty.VET_FK)
										.text(" - ")
										.value(VetSpecialty.SPECIALTY_FK)
										.build())
						.build();
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
	private EntityDefinition petType() {
		return PetType.TYPE.define(
										PetType.ID.define()
														.primaryKey(),
										PetType.NAME.define()
														.column()
														.caption("Name")
														.searchable(true)
														.maximumLength(80)
														.nullable(false))
						.keyGenerator(identity())
						.caption("Pet types")
						.stringFactory(PetType.NAME)
						.orderBy(ascending(PetType.NAME))
						.smallDataset(true)
						.build();
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
	private EntityDefinition owner() {
		return Owner.TYPE.define(
										Owner.ID.define()
														.primaryKey(),
										Owner.FIRST_NAME.define()
														.column()
														.caption("First name")
														.searchable(true)
														.maximumLength(30)
														.nullable(false),
										Owner.LAST_NAME.define()
														.column()
														.caption("Last name")
														.searchable(true)
														.maximumLength(30)
														.nullable(false),
										Owner.ADDRESS.define()
														.column()
														.caption("Address")
														.maximumLength(255),
										Owner.CITY.define()
														.column()
														.caption("City")
														.maximumLength(80),
										Owner.TELEPHONE.define()
														.column()
														.caption("Telephone")
														.maximumLength(20),
										Owner.PHONE_TYPE.define()
														.column()
														.caption("Phone type")
														.columnClass(String.class, new PhoneTypeConverter()))
						.keyGenerator(identity())
						.caption("Owners")
						.stringFactory(StringFactory.builder()
										.value(Owner.LAST_NAME)
										.text(", ")
										.value(Owner.FIRST_NAME)
										.build())
						.orderBy(ascending(Owner.LAST_NAME, Owner.FIRST_NAME))
						.build();
	}

	private static final class PhoneTypeConverter implements Converter<PhoneType, String> {

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
	private EntityDefinition pet() {
		return Pet.TYPE.define(
										Pet.ID.define()
														.primaryKey(),
										Pet.NAME.define()
														.column()
														.caption("Name")
														.searchable(true)
														.maximumLength(30)
														.nullable(false),
										Pet.BIRTH_DATE.define()
														.column()
														.caption("Birth date")
														.nullable(false),
										Pet.PET_TYPE_ID.define()
														.column()
														.nullable(false),
										Pet.PET_TYPE_FK.define()
														.foreignKey()
														.caption("Pet type"),
										Pet.OWNER_ID.define()
														.column()
														.nullable(false),
										Pet.OWNER_FK.define()
														.foreignKey()
														.caption("Owner"))
						.keyGenerator(identity())
						.caption("Pets")
						.stringFactory(Pet.NAME)
						.orderBy(ascending(Pet.NAME))
						.build();
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
	private EntityDefinition visit() {
		return Visit.TYPE.define(
										Visit.ID.define()
														.primaryKey(),
										Visit.PET_ID.define()
														.column()
														.nullable(false),
										Visit.PET_FK.define()
														.foreignKey()
														.caption("Pet"),
										Visit.VISIT_DATE.define()
														.column()
														.caption("Date")
														.nullable(false),
										Visit.DESCRIPTION.define()
														.column()
														.caption("Description")
														.maximumLength(255))
						.keyGenerator(identity())
						.orderBy(OrderBy.builder()
										.ascending(Visit.PET_ID)
										.descending(Visit.VISIT_DATE)
										.build())
						.caption("Visits")
						.build();
	}
	// end::visit_impl[]
}
