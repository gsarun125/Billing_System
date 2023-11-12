package com.ka.billingsystem.java;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Import {

    public static String ImportData(String packageName,String filepath ) {

        try {
            Log.i("ImportData()", "ImportData()-Line1");
            File zipFile = new File(filepath);

            File dir = new File(Environment.getExternalStorageDirectory(), "KIRTHANA AGENCIES/Restore");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Log.i("ImportData()", "ImportData()-Line2");

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
            Log.i("ImportData()", "ImportData()-Line3");


            String currentDBPath = "/data/data/" + packageName + "/databases/BILLING_SYSTEM";
            String copyPath = "BILLING_SYSTEM.db";

            File currentDB = new File(currentDBPath);

            File backupDB = new File(dir, copyPath);
            Log.i("ImportData()", "ImportData()-Line4");

            if (currentDB.exists() ) {
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                Log.i("ImportData()", "ImportData()-Line5");

                src.close();
                dst.close();
                deleteDirectory(dir);
                Log.i("ImportData()", "ImportData()-Line6");

                return "Importing started";

            }
        } catch (Exception e) {
            Log.e("ImportData()", "ImportData()-An exception occurred", e);
        }
        return "Failed to import data";


    }
    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
}
