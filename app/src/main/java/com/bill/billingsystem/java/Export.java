package com.bill.billingsystem.java;



import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Export {
    public static String ExportData(String pakagename) {
        try {
                String currentPath = "/data/data/" + pakagename + "/databases/BILLING_SYSTEM";
                String sharedcurrentPath = "/data/data/" + pakagename + "/shared_prefs/shared_prefs.xml";
                String copyPath = "BILLING_SYSTEM.db";
                String sharedcopyPath ="shared_prefs.xml";

                File currentDB = new File(currentPath);
                File currentSH=new File(sharedcurrentPath);
               File dir= new  File(Environment.getExternalStorageDirectory(),"DATA");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File  subdir= new File(dir,"Backup");
                if (!subdir.exists()) {
                    subdir.mkdir();
                }



                File backupSH=new File(subdir,sharedcopyPath);
                File backupDB = new File(subdir,copyPath);


                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());

                    FileChannel src2=new FileInputStream(currentSH).getChannel();
                    FileChannel dst2=new FileOutputStream(backupSH).getChannel();
                    dst2.transferFrom(src2,0,src2.size());

                    System.out.println("fhfhgfgh");
                    src.close();
                    dst.close();
                    return "Successfully stored in download file" ;
                }

        } catch (Exception e) {
            System.out.println(e);
        }
        return "file not found";
    }
}
