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
package jfix.db4o.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;
import org.garret.perst.XMLImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceEnginePerst extends PersistenceEngineBase implements PersistenceEngine {

    protected Storage db;
    protected Set<Object> root;

    private final Logger logger = LoggerFactory.getLogger(PersistenceEnginePerst.class);

    @Override
    protected String getEngineName() {
        return "perst";
    }

    @Override
    protected String getEngineFileName() {
        return "perst.odb";
    }

    @Override
    public void open(String database) {
        initDirectory(database);
        openEngine();
    }

    private void createDb() {
        db = StorageFactory.getInstance().createStorage();
        db.setProperty("perst.file.noflush", Boolean.FALSE); // from time to time perst db is getting corrupted for no obvious reason
        db.setProperty("perst.object.cache.kind", "strong");
    }

    @SuppressWarnings("unchecked")
    protected void openEngine() {
        createDb();
        db.open(filename);
        if (db.getRoot() == null) {
            root = db.createSet();
            db.setRoot(root);
        } else {
            if (!runGc()) { // let's try to fix/clean up database before start working
                runRecovery();
            }
            root = (Set<Object>) db.getRoot();
        }
    }

    private boolean runRecovery() {
        logger.info("Recovery started");
        try {
            Writer writer = new BufferedWriter(new FileWriter(filename + ".recovery"));
            // export the whole database to the specified writer in XML format
            db.exportXML(writer);
            writer.close(); // exportXML doesn't close the stream, close it here
        } catch (IOException e) {
            logger.error("Export failed: " + e);
            return false;
        }
        root = db.createSet();
        db.setRoot(root);
        try {
            Reader reader = new BufferedReader(new FileReader(filename + ".recovery"));
            db.importXML(reader);
            reader.close(); // importXML doesn't close the stream, close it here
        } catch (IOException | XMLImportException e) {
            logger.error("Import failed: " + e);
            return false;
        }
        logger.info("Recovery finished");
        return true;
    }

    @Override
    public Collection<Object> query() {
        Set<Object> rslt = new HashSet<>(root.size());
        Iterator<Object> iter = root.iterator();
        int added = 0;
        while (iter.hasNext()) {
            try {
                Object obj = iter.next();
                rslt.add(obj);
                added++;
            } catch (Exception e) {
                logger.error("Root size: {}; Added: {}; {}", root.size(), added, e);
                throw e; // we cannot continue, database is broken
            }
        }
        return rslt;
    }

    @Override
    public void save(Object object) {
        root.add(object);
        db.modify(object);
    }

    @Override
    public void delete(Object object) {
        root.remove(object);
        db.deallocate(object);
        runGc();
    }

    private boolean runGc() {
        try {
            db.gc();
            return true;
        } catch (Exception e) {
            logger.error("{}", e);
            return false;
            // perhaps we can safely continue
        }
    }

    @Override
    public void begin() {
        // Empty as Perst don't needs an explicit transaction begin.
    }

    @Override
    public void commit() {
        db.commit();
    }

    @Override
    public void rollback() {
        db.rollback();
    }

    @Override
    public void backup() {
        try {
            String backupFilename = getBackupFileName();
            db.backup(new FileOutputStream(new File(backupFilename)));
            logger.info("Backup successful: " + backupFilename);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        db.close();
    }

}
