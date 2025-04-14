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
package jease.cmf.service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jease.cmf.domain.Node;
import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.Role;
import jease.cms.domain.User;
import jfix.db4o.Database;
import jfix.db4o.Persistent;
import jfix.util.Zipfiles;
import jfix.zk.Modal;

/**
 * Service for dumping/restoring nodes into/from XML.
 */
public class Backup extends Serializer {

    /**
     * Create a new Backup-Service. The service needs to know about all nodes
     * which exist to configure the serializer properly.
     */
    public Backup(Node... nodes) {
        for (Node node : nodes) {
            for (Field field : getFields(node)) {
                if (isNotSerialized(field)) {
                    omitField(field);
                    continue;
                }
                if (!isNodeDeclaringClass(field) && isNode(field)) {
                    registerConverter(field);
                }
            }
        }
    }

    /**
     * Dump contents of node (and all children) into a file.
     */
    public File dump(Node node) {
        if (node == null) {
            return null;
        }
        try {
            Node nodeCopy = node.copy(true);
            nodeCopy.setParent(null);
            String filename = (StringUtils.isEmpty(nodeCopy.getId()) ? nodeCopy
                    .getType() : nodeCopy.getId()) + ".xml";
            File tmpDirectory = Files.createTempDirectory("jease-backup")
                    .toFile();
            tmpDirectory.deleteOnExit();
            File dumpFile = new File(tmpDirectory, filename);
            dumpFile.deleteOnExit();
            Writer writer = Files.newBufferedWriter(dumpFile.toPath());
            toXML(nodeCopy, writer);
            writer.close();
            File zipFile = Zipfiles.zip(dumpFile);
            zipFile.deleteOnExit();
            return zipFile;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Restore node-graph from file dump.
     */
    public Node restore(File dumpFile) {
        if (dumpFile == null) {
            return null;
        }
        try {
            Reader reader = Files.newBufferedReader(Zipfiles.unzip(dumpFile)
                    .toPath());
            Node node = fromXML(reader);
            node.setId(Filenames.asId(dumpFile.getName()).replace(".xml.zip",
                    ""));
            reader.close();
            return node;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static User createDumpUser(User user) {
        User rslt = user.shallowClone();
        Content[] r = rslt.getRoots();
        if (r.length > 0) {
            Content[] newRoots = new Content[r.length];
            for (int i = 0; i < r.length; i++) {
                Content root = r[i];
                newRoots[i] = root.copy(false/*recursive*/);
                List<Node> parents = new ArrayList<>();
                Node parentNode = root.getParent();
                while (parentNode != null) {
                    parents.add(parentNode);
                    parentNode = parentNode.getParent();
                }
                if (!parents.isEmpty()) {
                    List<Node> newParents = new ArrayList<>(parents.size());
                    for (Node j : parents) newParents.add(j.copy(false/*recursive*/));
                    for (int k = newParents.size() - 2; k >= 0; k--) {
                        newParents.get(k).setParent(newParents.get(k + 1));
                    }
                    newRoots[i].setParent(newParents.get(0));
                }
            }
            rslt.setRoots(newRoots);
        }
        return rslt;
    }

    public File dumpUsers() {
        List<User> users = Database.query(User.class);
        if (users.isEmpty()) return null;
        List<Role> roles = Database.query(Role.class);
        try {
            String filename = "users-dump.xml";
            File tmpDirectory = Files.createTempDirectory("jease-backup").toFile();
            tmpDirectory.deleteOnExit();
            File dumpFile = new File(tmpDirectory, filename);
            dumpFile.deleteOnExit();
            Writer writer = Files.newBufferedWriter(dumpFile.toPath());
            List<Persistent> lst = new ArrayList<>();
            for (Role role : roles) lst.add(role);
            for (User user : users) lst.add(createDumpUser(user));
            objToXML(lst, writer);
            writer.close();
            File zipFile = Zipfiles.zip(dumpFile);
            zipFile.deleteOnExit();
            return zipFile;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public boolean restoreUsers(File dumpFile) {
        if (dumpFile == null) return false;

        try {
            Reader reader = Files.newBufferedReader(Zipfiles.unzip(dumpFile).toPath());
            Object obj = objFromXML(reader);
            reader.close();
            if (!(obj instanceof List)) return false;
            @SuppressWarnings("unchecked")
            List<Persistent> objs = (List<Persistent>) obj;
            List<Role> roles = new ArrayList<>();
            List<User> users = new ArrayList<>();
            for (Persistent i : objs) {
                if (i instanceof Role) roles.add((Role) i);
                else if (i instanceof User) users.add((User) i);
            }

            int savedUsersCnt = 0;
            int savedRolesCnt = 0;
            if (!users.isEmpty()) {
                List<User> dbUsers = Database.query(User.class);
                List<Role> dbRoles = Database.query(Role.class);
                List<User> usersToSave = new ArrayList<>();
                List<Role> rolesToSave = new ArrayList<>();
                for (User u : users) {
                    boolean exists = false;
                    for (User dbu : dbUsers) {
                        if (u.getLogin().equals(dbu.getLogin())) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) continue;
                    Role dbRole = null;
                    for (Role dbr : dbRoles) {
                        if (u.getRole().getName().equals(dbr.getName())) {
                            dbRole = dbr;
                            break;
                        }
                    }
                    if (dbRole == null) {
                        Role roleToSave = null;
                        for (Role r : rolesToSave) {
                            if (u.getRole().getName().equals(r.getName())) {
                                roleToSave = r;
                                break;
                            }
                        }
                        if (roleToSave != null) u.setRole(roleToSave);
                        else rolesToSave.add(u.getRole());
                    } else {
                        u.setRole(dbRole);
                    }
                    usersToSave.add(u);
                }
                if (!usersToSave.isEmpty()) {
                    resolveRoots(usersToSave);
                    Database.write(new Runnable() {
                        @Override
                        public void run() {
                            for (User u : usersToSave) Database.save(u);
                        }});

                    savedUsersCnt = usersToSave.size();
                    savedRolesCnt = rolesToSave.size();
                }
            }
            Modal.info("Loaded from " + dumpFile.getName() + "\n"
                       + "-> Users: " + users.size() + "; Roles: " + roles.size() + "\n\n"
                       + "Saved to DB\n"
                       + "-> Users: " + savedUsersCnt + "; Roles: " + savedRolesCnt);
            return savedUsersCnt > 0;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static void resolveRoots(List<User> users) {
        List<Folder> folders = Database.query(Folder.class);
        for (User u : users) {
            Content[] roots = u.getRoots();
            if (roots.length == 0) continue;
            List<Content> newRoots = new ArrayList<>(roots.length);
            for (Content r : roots) {
                for (Folder f : folders) {
                    if (r.getId().equals(f.getId())) {
                        Node[] rParents = r.getParents();
                        Node[] fParents = f.getParents();
                        if (rParents.length == fParents.length) {
                            boolean matched = true;
                            for (int j = 0; j < rParents.length; j++) {
                                if (!rParents[j].getId().equals(fParents[j].getId())) {
                                    matched = false;
                                    break;
                                }
                            }
                            if (matched) newRoots.add(f);
                        }
                    }
                }
            }
            u.setRoots(newRoots.toArray(new Folder[] {}));
        }
    }

}
