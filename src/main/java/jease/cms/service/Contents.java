/*
    Copyright (C) 2016 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jease.cms.service;

import jease.Names;
import jease.Registry;
import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.Reference;
import jease.cms.domain.Trash;
import jease.cms.domain.User;
import jfix.db4o.Database;
import jfix.util.Reflections;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Contents {

    /**
     * Returns content object for given uuid.
     */
    public static Content getByUUID(final String uuid) {
        if (uuid == null) {
            return null;
        }
        return Database.queryUnique(Content.class, content -> uuid.equals(content.getUUID()));
    }

    /**
     * A <code>java.util.function.Consumer<Content></code> is used to customize
     * the given content before it is saved to the database. The customizer is
     * taken as Java class name from the global registry. If no customizer is
     * present, nothing is done.
     */
    public static Content customize(Content content) {
        String contentCustomizer = Registry.getParameter(Names.JEASE_CONTENT_CUSTOMIZER);
        if (contentCustomizer != null) {
            ((Consumer<Content>) Reflections.newInstance(contentCustomizer)).accept(content);
        }
        return content;
    }

    /**
     * Return array of all available content types.
     */
    public static Content[] getAvailableTypes() {
        return Registry.getContents();
    }

    public static String[] getClassNamesForAvailableTypes() {
        return Stream.of(getAvailableTypes())
                .map(content -> content.getClass().getName())
                .toArray(size -> new String[size]);
    }

    /**
     * Checks if content is a descendant of a user's root.
     */
    public static boolean isDeletable(Content content) {
        for (User user : Database.query(User.class)) {
            if (user.getRoots() != null) {
                for (Content folder : user.getRoots()) {
                    if (folder.isDescendant(content)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Deletes given content. If a Trash-Object is guarding the given content,
     * the content will be moved to Trash, otherwise it will be deleted
     * directly. If the given content is a Trash-Object, the Trash-Object is
     * deleted only when it is empty, otherwise the Trash will be emptied.
     */
    public static void delete(Content content) {
        if (content instanceof Trash) {
            Trash trash = (Trash) content;
            if (trash.isEmpty()) {
                Nodes.delete(trash);
            } else {
                for (Content child : trash.getChildren(Content.class)) {
                    if (child.getParent() != null && Contents.isDeletable(child)) {
                        delete(child);
                    }
                }
                Nodes.save(trash);
            }
        } else {
            Trash trash = null;
            if (content.getParent() != null) {
                Trash[] guards = ((Content) content.getParent()).getGuards(Trash.class);
                if (ArrayUtils.isNotEmpty(guards)) {
                    trash = guards[0];
                }
            }
            if (trash == null || content.isDescendant(trash)) {
                if (isDeletable(content)) {
                    deleteReferences(content);
                    Nodes.delete(content);
                }
            } else {
                trash.appendChild(content);
                Nodes.save(trash);
            }
        }
    }

    private static void deleteReferences(Content content) {
        for (Reference reference : Database.query(Reference.class)) {
            if (reference.getContent() != null && reference.getContent().isDescendant(content)) {
                deleteReferences(reference);
                Nodes.delete(reference);
            }
        }
    }

    /**
     * Deletes all objects older than given days in all Trash objects in the
     * system.
     */
    public static void emptyTrash(int daysToKeep) {
        Date pivotDate = DateUtils.addDays(new Date(), -daysToKeep);
        for (Trash trash : Database.query(Trash.class)) {
            if (ArrayUtils.isNotEmpty(trash.getChildren())) {
                for (Content content : trash.getChildren(Content.class)) {
                    if (content.getLastModified().before(pivotDate)) {
                        if (Contents.isDeletable(content)) {
                            Contents.delete(content);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns all descendants for given nodes.
     */
    public static Content[] getDescendants(Content[] nodes) {
        List<Content> contents = new ArrayList<>();
        if (nodes != null) {
            for (Content node : nodes) {
                Collections.addAll(contents, node.getDescendants(Content.class));
            }
        }
        return contents.toArray(new Content[contents.size()]);
    }

    /**
     * Returns all available content containers.
     */
    public static Content[] getContainer() {
        List<Content> query = Database.query(Content.class, Content::isContainer);
        return query.toArray(new Content[query.size()]);
    }
}
