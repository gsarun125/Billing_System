package com.ka.billingsystem.model;

import java.io.File;

public interface onpdfDelete {
    void onpdfSelected(File file, String mPbillno, String filename);
    void Share(File file);
    void Undo(String mPbillno);
}
