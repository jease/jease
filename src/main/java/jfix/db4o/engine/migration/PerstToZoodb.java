package jfix.db4o.engine.migration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

import org.jease.cms.domain.property.Property;
import jfix.db4o.ObjectDatabase;
import jfix.db4o.Persistent;
import jfix.db4o.engine.PersistenceEngine;
import jfix.db4o.engine.PersistenceEnginePerst;
import jfix.db4o.engine.PersistenceEngineZoodb;

public class PerstToZoodb {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Database name required.");
            return;
        }

        String databaseName = args[0];

        PersistenceEngine perstEngine = new PersistenceEnginePerst();
        perstEngine.open(databaseName);

        ObjectDatabase perst = new ObjectDatabase(perstEngine);
        perst.open();

        PersistenceEngineZoodb zoodbEngine = new PersistenceEngineZoodb();
        zoodbEngine.open(databaseName);

        ObjectDatabase zoodb = new ObjectDatabase(zoodbEngine);
        zoodb.open();

        zoodbEngine.checkSchema(Property.class); // workaround for https://github.com/tzaeschke/zoodb/issues/98

        for (Persistent p : perst.query(Persistent.class)) {
            zoodb.save(p);
        }

        String perstBlobDir = perst.getBlobDirectory();
        String zoodbBlobDir = zoodb.getBlobDirectory();

        if (!Objects.equals(perstBlobDir, zoodbBlobDir)) {
            FileUtils.copyDirectory(new File(perstBlobDir + File.separator + "blob"),
                                    new File(zoodbBlobDir + File.separator + "blob"));
        } else {
            System.out.println("Blob directories are the same: "
                               + zoodbBlobDir + File.separator + "blob");
        }

        zoodb.close();
        perst.close();
    }

}
