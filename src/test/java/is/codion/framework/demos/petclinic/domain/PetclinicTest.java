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

import is.codion.common.db.exception.DatabaseException;
import is.codion.framework.demos.petclinic.domain.Petclinic.Owner;
import is.codion.framework.demos.petclinic.domain.Petclinic.Pet;
import is.codion.framework.demos.petclinic.domain.Petclinic.PetType;
import is.codion.framework.demos.petclinic.domain.Petclinic.Specialty;
import is.codion.framework.demos.petclinic.domain.Petclinic.Vet;
import is.codion.framework.demos.petclinic.domain.Petclinic.VetSpecialty;
import is.codion.framework.demos.petclinic.domain.Petclinic.Visit;
import is.codion.framework.domain.test.DomainTest;

import org.junit.jupiter.api.Test;

public final class PetclinicTest extends DomainTest {

	public PetclinicTest() {
		super(new Petclinic());
	}

	@Test
	void vet() throws DatabaseException {
		test(Vet.TYPE);
	}

	@Test
	void specialty() throws DatabaseException {
		test(Specialty.TYPE);
	}

	@Test
	void vetSpecialty() throws DatabaseException {
		test(VetSpecialty.TYPE);
	}

	@Test
	void petType() throws DatabaseException {
		test(PetType.TYPE);
	}

	@Test
	void owner() throws DatabaseException {
		test(Owner.TYPE);
	}

	@Test
	void pet() throws DatabaseException {
		test(Pet.TYPE);
	}

	@Test
	void visit() throws DatabaseException {
		test(Visit.TYPE);
	}
}
