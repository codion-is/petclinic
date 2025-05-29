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
package is.codion.demos.petclinic.ui;

import is.codion.common.db.database.Database;
import is.codion.common.model.CancelException;
import is.codion.common.user.User;
import is.codion.demos.petclinic.domain.Petclinic;
import is.codion.demos.petclinic.domain.Petclinic.Owner;
import is.codion.demos.petclinic.domain.Petclinic.Pet;
import is.codion.demos.petclinic.domain.Petclinic.PetType;
import is.codion.demos.petclinic.domain.Petclinic.Specialty;
import is.codion.demos.petclinic.domain.Petclinic.Vet;
import is.codion.demos.petclinic.domain.Petclinic.Visit;
import is.codion.demos.petclinic.model.PetclinicAppModel;
import is.codion.demos.petclinic.model.VetSpecialtyEditModel;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.plugin.flatlaf.intellij.themes.arc.Arc;
import is.codion.swing.common.ui.component.indicator.ValidIndicatorFactory;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.ui.EntityApplicationPanel;
import is.codion.swing.framework.ui.EntityPanel;
import is.codion.swing.framework.ui.ReferentialIntegrityErrorHandling;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class PetclinicAppPanel extends EntityApplicationPanel<PetclinicAppModel> {

	public PetclinicAppPanel(PetclinicAppModel appModel) {
		super(appModel, createPanels(appModel), createSupportPanelBuilders());
	}

	private static List<EntityPanel> createPanels(PetclinicAppModel applicationModel) {
		SwingEntityModel ownersModel = applicationModel.entityModels().get(Owner.TYPE);
		SwingEntityModel petsModel = ownersModel.detailModels().get(Pet.TYPE);
		SwingEntityModel visitsModel = petsModel.detailModels().get(Visit.TYPE);

		EntityPanel ownersPanel = new EntityPanel(ownersModel,
						new OwnerEditPanel(ownersModel.editModel()));
		EntityPanel petsPanel = new EntityPanel(petsModel,
						new PetEditPanel(petsModel.editModel()));
		EntityPanel visitsPanel = new EntityPanel(visitsModel,
						new VisitEditPanel(visitsModel.editModel()));

		ownersPanel.detailPanels().add(petsPanel);
		petsPanel.detailPanels().add(visitsPanel);

		return List.of(ownersPanel);
	}

	private static Collection<EntityPanel.Builder> createSupportPanelBuilders() {
		EntityPanel.Builder petTypePanelBuilder =
						EntityPanel.builder(PetType.TYPE,
										PetclinicAppPanel::createPetTypePanel);
		EntityPanel.Builder specialtyPanelBuilder =
						EntityPanel.builder(Specialty.TYPE,
										PetclinicAppPanel::createSpecialtyPanel);
		EntityPanel.Builder vetPanelBuilder =
						EntityPanel.builder(Vet.TYPE,
										PetclinicAppPanel::createVetPanel);

		return List.of(petTypePanelBuilder, specialtyPanelBuilder, vetPanelBuilder);
	}

	private static EntityPanel createPetTypePanel(EntityConnectionProvider connectionProvider) {
		SwingEntityModel petTypeModel =
						new SwingEntityModel(PetType.TYPE, connectionProvider);
		petTypeModel.tableModel().items().refresh();

		return new EntityPanel(petTypeModel,
						new PetTypeEditPanel(petTypeModel.editModel()), config ->
						config.caption("Pet types"));
	}

	private static EntityPanel createSpecialtyPanel(EntityConnectionProvider connectionProvider) {
		SwingEntityModel specialtyModel =
						new SwingEntityModel(Specialty.TYPE, connectionProvider);
		specialtyModel.tableModel().items().refresh();

		return new EntityPanel(specialtyModel,
						new SpecialtyEditPanel(specialtyModel.editModel()), config -> config
						.caption("Specialties"));
	}

	private static EntityPanel createVetPanel(EntityConnectionProvider connectionProvider) {
		SwingEntityModel vetModel =
						new SwingEntityModel(Vet.TYPE, connectionProvider);
		SwingEntityModel vetSpecialtyModel =
						new SwingEntityModel(new VetSpecialtyEditModel(connectionProvider));
		vetModel.detailModels().add(vetSpecialtyModel);
		vetModel.tableModel().items().refresh();

		EntityPanel vetPanel = new EntityPanel(vetModel,
						new VetEditPanel(vetModel.editModel()), config -> config
						.caption("Vets"));
		EntityPanel vetSpecialtyPanel = new EntityPanel(vetSpecialtyModel,
						new VetSpecialtyEditPanel(vetSpecialtyModel.editModel()), config -> config
						.caption("Specialty"));
		vetPanel.detailPanels().add(vetSpecialtyPanel);

		return vetPanel;
	}

	public static void main(String[] args) throws CancelException {
		Locale.setDefault(new Locale("en", "EN"));
		ValidIndicatorFactory.FACTORY_CLASS.set("is.codion.plugin.flatlaf.indicator.FlatLafValidIndicatorFactory");
		ReferentialIntegrityErrorHandling.REFERENTIAL_INTEGRITY_ERROR_HANDLING
						.set(ReferentialIntegrityErrorHandling.DISPLAY_DEPENDENCIES);
		Database.DATABASE_URL.set("jdbc:h2:mem:h2db");
		Database.DATABASE_INIT_SCRIPTS.set("classpath:create_schema.sql");
		EntityApplicationPanel.builder(PetclinicAppModel.class, PetclinicAppPanel.class)
						.applicationName("Petclinic")
						.applicationVersion(PetclinicAppModel.VERSION)
						.domainType(Petclinic.DOMAIN)
						.displayStartupDialog(false)
						.defaultLookAndFeel(Arc.class)
						.user(User.parse("scott:tiger"))
						.start();
	}
}
