/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.settings;

/**
 *
 * @author jab
 */
public class PersistenceSettings {

    /**
     * Settings
     */
    private static final Setting IMPLEMENTATION_CLASS = new SettingImp("persistenceManagerImplementationClass", "de.fraunhofer.iosb.ilt.sta.persistence.postgres.longid.PostgresPersistenceManagerLong");
    private static final Setting ALWAYS_ORDERBY_ID = new SettingImp("alwaysOrderbyId", true);
    private static final Setting ID_GENERATION_MODE = new SettingImp("idGenerationMode", "ServerGeneratedOnly");
    private static final Setting AUTO_UPDATE_DATABASE = new SettingImp("autoUpdateDatabase", false);

    /**
     * Fully-qualified class name of the PersistenceManager implementation class
     */
    private String persistenceManagerImplementationClass;
    private boolean alwaysOrderbyId;
    private String idGenerationMode;
    private boolean autoUpdateDatabase;
    /**
     * Extension point for implementation specific settings
     */
    private Settings customSettings;

    public PersistenceSettings(Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("settings most be non-null");
        }
        init(settings);
    }

    private void init(Settings settings) {
        persistenceManagerImplementationClass = settings.get(IMPLEMENTATION_CLASS.getKey(), IMPLEMENTATION_CLASS.getDefault());
        alwaysOrderbyId = settings.getBoolean(ALWAYS_ORDERBY_ID.getKey(), ALWAYS_ORDERBY_ID.getDefaultBool());
        idGenerationMode = settings.get(ID_GENERATION_MODE.getKey(), ID_GENERATION_MODE.getDefault());
        autoUpdateDatabase = settings.getBoolean(AUTO_UPDATE_DATABASE.getKey(), AUTO_UPDATE_DATABASE.getDefaultBool());
        customSettings = settings;
    }

    public String getPersistenceManagerImplementationClass() {
        return persistenceManagerImplementationClass;
    }

    public boolean getAlwaysOrderbyId() {
        return alwaysOrderbyId;
    }

    public boolean isAutoUpdateDatabase() {
        return autoUpdateDatabase;
    }

    public Settings getCustomSettings() {
        return customSettings;
    }

    public String getIdGenerationMode() {
        return idGenerationMode;
    }
}
