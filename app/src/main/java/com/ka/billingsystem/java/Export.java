package com.ka.billingsystem.java;



import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Export {
    public static String ExportData(String pakagename) {
        try {
            File file = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (file.canWrite()) {
                String currentPath = "/data/data/" + pakagename + "/databases/BILLING_SYSTEM";
                String copyPath = "copydb_name.db";
                File currentDB = new File(currentPath);

                File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),copyPath);


                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    System.out.println("fhfhgfgh");
                    src.close();
                    dst.close();
                    return "Successfully stored in download file" ;
                }
            }
        } catch (Exception e) {
        }
        return "file not found";
    }
}
