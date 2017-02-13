/* MetaGroup.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */
package com.himamis.retex.editor.share.meta;

import java.util.List;

/**
 * Group of abstract Meta Model Components.
 *
 * @author Bea Petrovicova
 */
public class ListMetaGroup implements MetaGroup {

    private List<MetaComponent> components;
    private String name;
    private String group;

    ListMetaGroup(String name, String group, List<MetaComponent> components) {
        this.name = name;
        this.components = components;
        this.group = group;
    }

    @Override
	public MetaComponent getComponent(String name) {
        for (MetaComponent component : components) {
            if (component.getName().equals(name) || name.equals(Character.toString(component.getUnicode()))) {
                return component;
            }
        }
        return null;
    }

    public List<MetaComponent> getComponents() {
        return components;
    }

    @Override
	public String getName() {
        return name;
    }

    @Override
    public String getGroup() {
        return group;
    }
}
