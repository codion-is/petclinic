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
package is.codion.framework.demos.petclinic.model;

import is.codion.common.db.exception.DatabaseException;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.petclinic.domain.Petclinic.VetSpecialty;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.exception.ValidationException;
import is.codion.swing.framework.model.SwingEntityEditModel;

import static is.codion.framework.db.condition.Condition.and;
import static is.codion.framework.db.condition.Condition.column;

public final class VetSpecialtyEditModel extends SwingEntityEditModel {

  public VetSpecialtyEditModel(EntityConnectionProvider connectionProvider) {
    super(VetSpecialty.TYPE, connectionProvider);
    initializeComboBoxModels(VetSpecialty.VET_FK, VetSpecialty.SPECIALTY_FK);
    setPersistValue(VetSpecialty.VET_FK, false);
    setPersistValue(VetSpecialty.SPECIALTY_FK, false);
  }

  @Override
  public boolean isEntityNew() {
    return entity().originalPrimaryKey().isNull();
  }

  @Override
  public void validate(Entity entity) throws ValidationException {
    super.validate(entity);
    try {
      int rowCount = connectionProvider().connection().rowCount(and(
              column(VetSpecialty.SPECIALTY).equalTo(entity.get(VetSpecialty.SPECIALTY)),
              column(VetSpecialty.VET).equalTo(entity.get(VetSpecialty.VET))));
      if (rowCount > 0) {
        throw new ValidationException(VetSpecialty.SPECIALTY_FK,
                entity.get(VetSpecialty.SPECIALTY_FK), "Vet/specialty combination already exists");
      }
    }
    catch (DatabaseException e) {
      throw new RuntimeException(e);
    }
  }
}
