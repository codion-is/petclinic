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

import is.codion.demos.petclinic.domain.Petclinic.VetSpecialty;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.exception.ValidationException;
import is.codion.swing.framework.model.SwingEntityEditModel;

import java.util.Collection;

import static is.codion.framework.db.EntityConnection.Count.where;
import static is.codion.framework.domain.entity.condition.Condition.and;

public final class VetSpecialtyEditModel extends SwingEntityEditModel {

	public VetSpecialtyEditModel(EntityConnectionProvider connectionProvider) {
		super(VetSpecialty.TYPE, connectionProvider);
		initializeComboBoxModels(VetSpecialty.VET_FK, VetSpecialty.SPECIALTY_FK);
		editor().value(VetSpecialty.SPECIALTY_FK).persist().set(false);
		beforeUpdate().addConsumer(this::validate);
		beforeInsert().addConsumer(this::validate);
	}

	private void validate(Collection<Entity> entities) {
		// Perform the standard validation in order to
		// assert that all required values are present
		editor().validate(entities);
		entities.forEach(this::validate);
	}

	private void validate(Entity entity) {
		int rowCount = connection().count(where(and(
						VetSpecialty.SPECIALTY.equalTo(entity.get(VetSpecialty.SPECIALTY)),
						VetSpecialty.VET.equalTo(entity.get(VetSpecialty.VET)))));
		if (rowCount > 0) {
			throw new ValidationException(VetSpecialty.SPECIALTY_FK,
							entity.get(VetSpecialty.SPECIALTY_FK), "Vet/specialty combination already exists");
		}
	}
}
