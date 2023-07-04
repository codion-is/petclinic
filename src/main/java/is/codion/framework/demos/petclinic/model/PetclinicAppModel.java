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

import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.petclinic.domain.Petclinic.Owner;
import is.codion.framework.demos.petclinic.domain.Petclinic.Pet;
import is.codion.framework.demos.petclinic.domain.Petclinic.Vet;
import is.codion.framework.demos.petclinic.domain.Petclinic.Visit;
import is.codion.swing.framework.model.SwingEntityApplicationModel;
import is.codion.swing.framework.model.SwingEntityModel;

public final class PetclinicAppModel extends SwingEntityApplicationModel {

  public PetclinicAppModel(EntityConnectionProvider connectionProvider) {
    super(connectionProvider);
    setupEntityModels(connectionProvider);
  }

  private void setupEntityModels(EntityConnectionProvider connectionProvider) {
    SwingEntityModel ownersModel = new SwingEntityModel(Owner.TYPE, connectionProvider);
    SwingEntityModel petsModel = new SwingEntityModel(Pet.TYPE, connectionProvider);
    petsModel.editModel().initializeComboBoxModels(Pet.OWNER_FK, Pet.PET_TYPE_FK);
    SwingEntityModel visitModel = new SwingEntityModel(Visit.TYPE, connectionProvider);
    visitModel.editModel().initializeComboBoxModels(Visit.PET_FK);

    ownersModel.addDetailModel(petsModel);
    petsModel.addDetailModel(visitModel);

    SwingEntityModel vetsModel = new SwingEntityModel(Vet.TYPE, connectionProvider);
    SwingEntityModel vetSpecialtiesModel = new SwingEntityModel(new VetSpecialtyEditModel(connectionProvider));

    vetsModel.addDetailModel(vetSpecialtiesModel);

    ownersModel.tableModel().refresh();
    vetsModel.tableModel().refresh();

    addEntityModels(ownersModel, vetsModel);
  }
}
