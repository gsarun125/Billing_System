package com.ka.billingsystem.java;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Export {
    public static ExportResult ExportData(String packageName,String Backup_filename,File zipFile) {
        try {
            String currentDBPath = "/data/data/" + packageName + "/databases/BILLING_SYSTEM";
            String currentSHPath = "/data/data/" + packageName + "/shared_prefs/shared_prefs.xml";
            String copyPath = "BILLING_SYSTEM.db";
            String sharedCopyPath = "shared_prefs.xml";

            File currentDB = new File(currentDBPath);
            File currentSH = new File(currentSHPath);

            if (currentDB.exists() && currentSH.exists()) {
                try (FileOutputStream fos = new FileOutputStream(zipFile);
                     ZipOutputStream zos = new ZipOutputStream(fos)) {
                    addToZip(currentDB, copyPath, zos);
                    addToZip(currentSH, sharedCopyPath, zos);
                }

                return new ExportResult("Successfully exported", Backup_filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ExportResult("Unable to export", null);
    }

    public static class ExportResult {
        private final String message;
        private final String fileName;

        public ExportResult(String message, String fileName) {
            this.message = message;
            this.fileName = fileName;
        }

        public String getMessage() {
            return message;
        }

        public String getFileName() {
            return fileName;
        }
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
