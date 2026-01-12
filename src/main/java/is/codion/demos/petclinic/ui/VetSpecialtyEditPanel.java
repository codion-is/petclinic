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
 * Copyright (c) 2004 - 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.petclinic.ui;

import is.codion.demos.petclinic.domain.Petclinic.Specialty;
import is.codion.demos.petclinic.domain.Petclinic.VetSpecialty;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.ui.EntityEditPanel;

import static is.codion.swing.common.ui.layout.Layouts.gridLayout;

public final class VetSpecialtyEditPanel extends EntityEditPanel {

	public VetSpecialtyEditPanel(SwingEntityEditModel editModel) {
		super(editModel);
	}

	@Override
	protected void initializeUI() {
		createComboBox(VetSpecialty.VET_FK)
						.preferredWidth(200);
		createComboBoxPanel(VetSpecialty.SPECIALTY_FK, this::createSpecialtyEditPanel)
						.includeAddButton(true)
						.preferredWidth(200);

		setLayout(gridLayout(2, 1));

		addInputPanel(VetSpecialty.VET_FK);
		addInputPanel(VetSpecialty.SPECIALTY_FK);
	}

	private SpecialtyEditPanel createSpecialtyEditPanel() {
		return new SpecialtyEditPanel(new SwingEntityEditModel(Specialty.TYPE, editModel().connectionProvider()));
	}
}
