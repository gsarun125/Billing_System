package com.ka.billingsystem.java;

import android.os.Environment;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;

public class Export {
    public static String ExportData(String packageName) {
        try {
            String currentDBPath = "/data/data/" + packageName + "/databases/BILLING_SYSTEM";
            String currentSHPath = "/data/data/" + packageName + "/shared_prefs/shared_prefs.xml";
            String copyPath = "BILLING_SYSTEM.db";
            String sharedCopyPath = "shared_prefs.xml";

            File currentDB = new File(currentDBPath);
            File currentSH = new File(currentSHPath);
            File dir = new File(Environment.getExternalStorageDirectory(), "KIRTHANA AGENCIES");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File subdir = new File(dir, "Backup");
            if (!subdir.exists()) {
                subdir.mkdirs();
            }

            File zipFile = new File(subdir, "backup.zip");

            if (currentDB.exists() && currentSH.exists()) {

                try (FileOutputStream fos = new FileOutputStream(zipFile);
                     ArchiveOutputStream aos = new ZipArchiveOutputStream(fos)) {
                    addToZip(currentDB, copyPath, aos);
                    addToZip(currentSH, sharedCopyPath, aos);
                }

                return "Successfully stored";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "File not found";
    }

    private static void addToZip(File file, String fileName, ArchiveOutputStream out) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ArchiveEntry entry = out.createArchiveEntry(file, fileName);
        out.putArchiveEntry(entry);
        IOUtils.copy(fis, out);
        out.closeArchiveEntry();
        fis.close();
    }
}
