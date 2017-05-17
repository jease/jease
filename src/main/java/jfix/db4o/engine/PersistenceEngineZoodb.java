package jfix.db4o.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.zoodb.api.impl.ZooPC;
import org.zoodb.jdo.ZooJdoHelper;
import org.zoodb.schema.ZooSchema;

import jfix.db4o.Persistent;

public class PersistenceEngineZoodb extends PersistenceEngineBase implements PersistenceEngine {

    private PersistenceManager pm;

    @Override
    protected String getEngineName() {
        return "zoodb";
    }

    @Override
    protected String getEngineFileName() {
        return "jease.zdb";
    }

    @Override
    public void open(String database) {
        initDirectory(database);
        openDb();
    }

    @Override
    public void close() {
        closeDb();
    }

    private void openDb() {
        pm = ZooJdoHelper.openOrCreateDB(filename);
    }

    /** Close the database connection. */
    private void closeDb() {
        if (pm == null) return;
        if (pm.currentTransaction().isActive()) {
            pm.currentTransaction().rollback();
        }
        pm.close();
        pm.getPersistenceManagerFactory().close();
        pm = null;
    }

    @Override
    public void backup() {
        backupAsCopyFile();
    }

    @Override
    public void save(Object object) {
        if (!(object instanceof ZooPC)) return;
        ZooPC zoo = (ZooPC) object;
        zoo.zooActivateWrite();
        pm.makePersistent(zoo);
    }

    @Override
    public void delete(Object object) {
        if (!(object instanceof ZooPC)) return;
        ZooPC zoo = (ZooPC) object;
        if (zoo.jdoZooIsPersistent()) pm.deletePersistent(zoo);
    }

    @Override
    public void begin() {
        if (pm.currentTransaction().isActive()) return;
        pm.currentTransaction().begin();
    }

    @Override
    public void commit() {
        if (!pm.currentTransaction().isActive()) return;
        pm.currentTransaction().setRetainValues(true); // important, jease continues usage of domain objects
        pm.currentTransaction().commit();
    }

    @Override
    public void rollback() {
        if (!pm.currentTransaction().isActive()) return;
        pm.currentTransaction().rollback();
    }

    @Override
    public Collection<Object> query() {
        List<Object> rslt = new ArrayList<>();
        pm.currentTransaction().setNontransactionalRead(true);
        Extent<Persistent> objs = pm.getExtent(Persistent.class);
        if (objs != null) {
            for (Persistent i : objs) {
                i.zooActivateRead();
                rslt.add(i);
            }
        }
        pm.currentTransaction().setNontransactionalRead(false);
        return rslt;
    }

    public void checkSchema(Class<?> clazz) {
        if (pm == null || clazz == null) return;
        pm.currentTransaction().begin();
        ZooSchema schema = ZooJdoHelper.schema(pm);
        if (schema.getClass(clazz) == null) schema.addClass(clazz);
        pm.currentTransaction().commit();
    }

}
