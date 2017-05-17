package jfix.db4o.engine;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public abstract class PersistenceEngineBase {

    protected String directory;
    protected String filename;

    /**
     * @return - something like: "perst", "xstream", "db4o", ...
     */
    protected abstract String getEngineName();

    /**
     * @return - something like: "perst.odb", "odb.xml", "db4o.yap", ...
     */
    protected abstract String getEngineFileName();

    protected void initDirectory(String database) {
        if (database.contains(File.separator)) {
            directory = database.endsWith(File.separator) ? database : database + File.separator;
        } else {
            directory = System.getProperty("user.home") + File.separator + getEngineName()
                    + File.separator + database + File.separator;
        }
        filename = directory + getEngineFileName();
        new File(directory).mkdirs();
    }

    public String getBlobDirectory() {
        return directory;
    }

    protected String getBackupFileName() {
        String backupFilename = filename + new SimpleDateFormat("-yyyyMMdd").format(new Date());
        return backupFilename;
    }

    protected void backupAsCopyFile() {
        try {
            FileUtils.copyFile(new File(filename), new File(getBackupFileName()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return filename;
    }

}
