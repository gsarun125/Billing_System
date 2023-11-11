package com.ka.billingsystem.java;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

            File zipFile = new File(subdir, "kirthana_agencies_backup.zip");

            if (currentDB.exists() && currentSH.exists()) {
                try (FileOutputStream fos = new FileOutputStream(zipFile);
                     ZipOutputStream zos = new ZipOutputStream(fos)) {
                    addToZip(currentDB, copyPath, zos);
                    addToZip(currentSH, sharedCopyPath, zos);
                }

                return "Successfully stored";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "File not found";
    }

    private static void addToZip(File file, String fileName, ZipOutputStream out) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry entry = new ZipEntry(fileName);
        out.putNextEntry(entry);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        out.closeEntry();
        fis.close();
    }
}
