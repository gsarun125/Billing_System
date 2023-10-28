package com.ka.billingsystem.java;

import android.os.Environment;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Import {

    public static String ImportData(String packageName) {
        try {
            String zipFilePath = Environment.getExternalStorageDirectory() + "/DATA/Backup/backup.zip";
            File zipFile = new File(zipFilePath);

            File dir = new File(Environment.getExternalStorageDirectory(), "DATA/Backup");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (FileInputStream fis = new FileInputStream(zipFile);
                 ArchiveInputStream ais = new ArchiveStreamFactory()
                         .createArchiveInputStream(ArchiveStreamFactory.ZIP, fis)) {

                ArchiveEntry entry;
                while ((entry = ais.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    File outFile = new File(dir, fileName);
                    if (entry.isDirectory()) {
                        outFile.mkdirs();
                    } else {
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            IOUtils.copy(ais, fos);
                        }
                    }
                }
            }

            String currentDBPath = "/data/data/" + packageName + "/databases/BILLING_SYSTEM";
            String currentSHPath = "/data/data/" + packageName + "/shared_prefs/shared_prefs.xml";
            String copyPath = "BILLING_SYSTEM.db";
            String sharedCopyPath = "shared_prefs.xml";

            File currentDB = new File(currentDBPath);
            File currentSH = new File(currentSHPath);

            File backupDB = new File(dir, copyPath);
            File backupSH = new File(dir, sharedCopyPath);

            if (currentDB.exists() && currentSH.exists()) {
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());

                FileChannel src2 = new FileInputStream(backupSH).getChannel();
                FileChannel dst2 = new FileOutputStream(currentSH).getChannel();
                dst2.transferFrom(src2, 0, src2.size());

                src.close();
                dst.close();

                return "Successfully imported data";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed to import data";
    }
}
