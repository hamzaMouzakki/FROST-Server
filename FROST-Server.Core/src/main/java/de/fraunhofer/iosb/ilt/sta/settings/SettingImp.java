/*
 * Copyright (C) 2018 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
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
 * @author scf
 */
public class SettingImp implements Setting {

    public final String key;
    private final String defltString;
    private final Integer defltInt;
    private final Boolean defltBool;

    public SettingImp(String key) {
        this.key = key;
        this.defltString = null;
        this.defltInt = null;
        this.defltBool = null;
    }

    public SettingImp(String key, String defltString) {
        this.key = key;
        this.defltString = defltString;
        this.defltInt = null;
        this.defltBool = null;
    }

    public SettingImp(String key, int defltInt) {
        this.key = key;
        this.defltString = null;
        this.defltInt = defltInt;
        this.defltBool = null;
    }

    public SettingImp(String key, boolean defltBool) {
        this.key = key;
        this.defltString = null;
        this.defltInt = null;
        this.defltBool = defltBool;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDefault() {
        if (defltString != null) {
            return defltString;
        } else if (defltInt != null) {
            return defltInt.toString();
        }
        throw new UnsupportedOperationException("This Setting has no default value.");
    }

    @Override
    public int getDefaultInt() {
        if (defltInt != null) {
            return defltInt;
        }
        return Setting.super.getDefaultInt();
    }

    @Override
    public boolean getDefaultBool() {
        if (defltBool != null) {
            return defltBool;
        }
        return Setting.super.getDefaultBool();
    }

}
