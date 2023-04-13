/*
 * Copyright (c) 2004 - 2023, Björn Darri Sigurðsson. All Rights Reserved.
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
import is.codion.framework.domain.entity.test.EntityTestUnit;

import org.junit.jupiter.api.Test;

public final class PetclinicTest extends EntityTestUnit {

  public PetclinicTest() {
    super(Petclinic.class.getName());
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
