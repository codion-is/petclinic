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
 * Copyright (c) 2004 - 2025, Björn Darri Sigurðsson.
 */
package is.codion.demos.petclinic.model;

import is.codion.common.version.Version;
import is.codion.demos.petclinic.domain.Petclinic.Owner;
import is.codion.demos.petclinic.domain.Petclinic.Pet;
import is.codion.demos.petclinic.domain.Petclinic.Visit;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.swing.framework.model.SwingEntityApplicationModel;
import is.codion.swing.framework.model.SwingEntityModel;

import java.util.List;

public final class PetclinicAppModel extends SwingEntityApplicationModel {

	public static final Version VERSION = Version.parse(PetclinicAppModel.class, "/version.properties");

	public PetclinicAppModel(EntityConnectionProvider connectionProvider) {
		super(connectionProvider, List.of(createOwnersModel(connectionProvider)));
	}

	private static SwingEntityModel createOwnersModel(EntityConnectionProvider connectionProvider) {
		SwingEntityModel ownersModel = new SwingEntityModel(Owner.TYPE, connectionProvider);
		SwingEntityModel petsModel = new SwingEntityModel(Pet.TYPE, connectionProvider);
		petsModel.editModel().initializeComboBoxModels(Pet.OWNER_FK, Pet.PET_TYPE_FK);
		SwingEntityModel visitModel = new SwingEntityModel(Visit.TYPE, connectionProvider);
		visitModel.editModel().initializeComboBoxModels(Visit.PET_FK);

		ownersModel.detailModels().add(petsModel);
		petsModel.detailModels().add(visitModel);

		ownersModel.tableModel().items().refresh();

		return ownersModel;
	}
}
