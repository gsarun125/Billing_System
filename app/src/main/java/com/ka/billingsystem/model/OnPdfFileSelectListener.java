package com.ka.billingsystem.model;

import java.io.File;

public interface OnPdfFileSelectListener {
    void onpdfSelected(File file, String mPbillno,String filename);
    void Share(File file);
    void Delete(String mPbillno);
    void Download(File file);
}
