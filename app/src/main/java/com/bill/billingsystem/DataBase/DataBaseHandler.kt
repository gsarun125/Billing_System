package com.bill.billingsystem.DataBase


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast


val DATABASENAME = "BILLING_SYSTEM"
val TABLENAME1 = "Stock"
val COL_PRODUCT_NAME = "Product_Name"
val COL_PRODUCT_ID = "Product_Id"
val COl_QUANTITY="quantity"
val COL_COST = "cost"

val TABLENAME2 = "Transation"
val COL_BILL_NO="Bill_No"
val COL_FILE_PATH="file_Path"
val COL_SALES_USER="sales_user"
val COL_PRODUCT_ID2 = "Product_Id"
val COl_QUANTITY2="quantity"
val COL_RATE = "rate"
val COL_AMOUNT="amount"
val COL_TIMESTAMP="time"
val CoL_TOTAL_AMOUNT="tamount"

val TABLENAME3 = "customer"
val Col_CUSTId="tratiction_id"
val COL_CUSID="cus_id"
val COL_CUSNAME= "cus_name"
val COl_CUSPHONE="cus_Phone"

val TABLENAME4 ="user"
val COL_ID="id"
val COL_USER="user_name"
val COL_PASS= "password"
val COL_TIMESTAMP_CREATE="created_date"
val COL_TIMESTAMP_MODIFIE="modified_date"
class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLENAME1 + " (" + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY + " INTEGER," + COL_COST + " INTEGER)"
        db?.execSQL(createTable)

       val createTable2 = "CREATE TABLE " + TABLENAME2 + " (" + COL_CUSID + " INTEGER ," + COL_BILL_NO + " INTEGER," + COL_PRODUCT_ID2 + " INTEGER," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY2 + " INTEGER," + COL_RATE + " INTEGER," + COL_AMOUNT + " INTEGER," + CoL_TOTAL_AMOUNT + " INTEGER," + COL_TIMESTAMP + " LONG,"+ COL_SALES_USER+" VARCHAR(1000),"+ COL_FILE_PATH+" VARCHAR(1000),FOREIGN KEY(cus_id) REFERENCES customer(cus_id))"

        db?.execSQL(createTable2)

        val createTable3 = "CREATE TABLE " + TABLENAME3 + " (" + COL_CUSID  + " INTEGER UNIQUE," + COL_CUSNAME + " VARCHAR(1000)," + COl_CUSPHONE + " INTEGER)"

        db?.execSQL(createTable3)

        val createTable4 = "CREATE TABLE " + TABLENAME4 + " ( "+ COL_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_USER + " VARCHAR(1000) UNIQUE," + COL_PASS + " VARCHAR(1000)," + COL_TIMESTAMP_CREATE+ " LONG," + COL_TIMESTAMP_MODIFIE + " LONG)"
        db?.execSQL(createTable4)

        this.UserData(db, "admin", "admin");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertData(product_name: String,quantity:Int,cost:Int) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_PRODUCT_NAME,product_name)

        contentValues.put(COl_QUANTITY,quantity)
        contentValues.put(COL_COST,cost)

        val result = database.insert(TABLENAME1, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }


    fun update( Product_Name: String,quantity:Int,cost:Int) {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COl_QUANTITY,quantity)
        values.put(COL_COST,cost)

        db.update(TABLENAME1, values, "Product_Name=?", arrayOf<String>(Product_Name))
        ///db.close()
    }

    fun update_stock( productcode :Int ,quantity:Int) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COl_QUANTITY,quantity)
        db.update(TABLENAME1, values, "Product_Id=?", arrayOf<String>(productcode.toString()))
        ///db.close()
    }


    fun get_value(query:String) :Cursor? {
        val db = this.writableDatabase

       return db.rawQuery(query,null);

    }


    fun getData(): Cursor? {
        val db = this.writableDatabase
        val query = "SELECT  * FROM $TABLENAME1;"


        return db.rawQuery(query, null)
    }





    fun insertData_to_trancation(Cus_Id:Int,Bill_id:Int,product_id:String,Product_Name: String,quantity:String,rate:Long,amount:Float,tamount:Float,time:Long,UserName:String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_CUSID, Cus_Id)
        contentValues.put(COL_BILL_NO, Bill_id)
        contentValues.put(COL_PRODUCT_ID2, product_id)

        contentValues.put(COL_PRODUCT_NAME,Product_Name)
        contentValues.put(COl_QUANTITY2, quantity)
        contentValues.put(COL_RATE,rate)
        contentValues.put(COL_AMOUNT,amount)
        contentValues.put(CoL_TOTAL_AMOUNT,tamount)
        contentValues.put(COL_TIMESTAMP,time)

        contentValues.put(COL_SALES_USER,UserName)


        val result = database.insert(TABLENAME2, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun insertData_to_Customer(Cus_Id:Int,Cus_name:String,Phoneno:String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_CUSID, Cus_Id)
        contentValues.put(COL_CUSNAME, Cus_name)
        contentValues.put(COl_CUSPHONE,Phoneno )


        val result = database.insert(TABLENAME3, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }
    open fun checkusernamepassword(query: String): Boolean {
        val MyDB = this.writableDatabase
        val cursor = MyDB.rawQuery(query,null);
        if (cursor.count > 0)
            return true
        else
            return false
    }

    fun insertData_to_user(username:String,pass: String,cDate:Long,mDate:Long) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_USER, username)
        contentValues.put(COL_PASS, pass)
        contentValues.put(COL_TIMESTAMP_CREATE,cDate)
        contentValues.put(COL_TIMESTAMP_MODIFIE,mDate)


        val result = database.insert(TABLENAME4, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun UserData( db:SQLiteDatabase?,user: String?, pass: String?) {
        val values = ContentValues()
        values.put(COL_USER, user)
        values.put(COL_PASS, pass)
        db?.insert(TABLENAME4, null, values)
    }

    fun filePath( Bill_No :Int ,Path:String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_FILE_PATH,Path)
        db.update(TABLENAME2, values, "Bill_No=?", arrayOf<String>(Bill_No.toString()))
        ///db.close()
    }
}