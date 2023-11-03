package com.ka.billingsystem.DataBase


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.util.Calendar
import java.util.Random

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
val Col_Image_printer="printer_img"

val TABLENAME3 = "customer"
val COL_CUSID="cus_id"
val COL_CUSNAME= "cus_name"
val COl_CUSPHONE="cus_Phone"

val TABLENAME4 ="user"
val COL_ID="id"
val COL_USER_NAME="user_name"
val COL_USER_id="user_id"
val COL_PASS= "password"
val COL_TIMESTAMP_CREATE="created_date"
val COL_TIMESTAMP_MODIFIE="modified_date"

val TABLENAME5 ="Deleted"
class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLENAME1 + " (" + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY + " INTEGER," + COL_COST + " INTEGER)"
        db?.execSQL(createTable)

       val createTable2 = "CREATE TABLE " + TABLENAME2 + " (" + COL_CUSID + " INTEGER ," + COL_BILL_NO  + " INTEGER ," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY2 + " INTEGER," + COL_RATE + " INTEGER," + COL_AMOUNT + " INTEGER," + CoL_TOTAL_AMOUNT + " INTEGER," + COL_TIMESTAMP + " LONG,"+ COL_SALES_USER+" VARCHAR(1000),"+ COL_FILE_PATH+" VARCHAR(1000),"+ Col_Image_printer+" VARCHAR(1000),FOREIGN KEY(cus_id) REFERENCES customer(cus_id))"

        db?.execSQL(createTable2)

        val createTable3 = "CREATE TABLE " + TABLENAME3 + " (" + COL_CUSID  + " INTEGER UNIQUE," + COL_CUSNAME + " VARCHAR(1000)," + COl_CUSPHONE + " INTEGER)"

        db?.execSQL(createTable3)

        val createTable4 = "CREATE TABLE " + TABLENAME4 + " ( "+ COL_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_USER_id + " VARCHAR(1000) UNIQUE," + COL_USER_NAME + " VARCHAR(1000) ," + COL_PASS + " VARCHAR(1000)," + COL_TIMESTAMP_CREATE+ " LONG," + COL_TIMESTAMP_MODIFIE + " LONG)"
        db?.execSQL(createTable4)
        val createTable5 = "CREATE TABLE " + TABLENAME5 + " (" + COL_CUSID + " INTEGER ," + COL_BILL_NO + " INTEGER," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY2 + " INTEGER," + COL_RATE + " INTEGER," + COL_AMOUNT + " INTEGER," + CoL_TOTAL_AMOUNT + " INTEGER," + COL_TIMESTAMP + " LONG,"+ COL_SALES_USER+" VARCHAR(1000),"+ COL_FILE_PATH+" VARCHAR(1000),"+ Col_Image_printer+" VARCHAR(1000),FOREIGN KEY(cus_id) REFERENCES customer(cus_id))"
        db?.execSQL(createTable5)
        this.UserData(db, "Admin","admin", "admin");
        this.UserData(db, "ARUN","a", "a");

        if (db != null) {
          // this.generateRandomData(db)
        };
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





    fun insertData_to_trancation(Cus_Id:Int,Bill_id:Int,Product_Name: String,quantity:String,rate:Long,amount:Float,tamount:Float,time:Long,UserName:String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_CUSID, Cus_Id)
        contentValues.put(COL_BILL_NO, Bill_id)
        contentValues.put(COL_PRODUCT_NAME,Product_Name)
        contentValues.put(COl_QUANTITY2, quantity)
        contentValues.put(COL_RATE,rate)
        contentValues.put(COL_AMOUNT,amount)
        contentValues.put(CoL_TOTAL_AMOUNT,tamount)
        contentValues.put(COL_TIMESTAMP,time)

        contentValues.put(COL_SALES_USER,UserName)


        database.insert(TABLENAME2, null, contentValues)

    }

    fun insertData_to_Customer(Cus_Id:Int,Cus_name:String,Phoneno:String) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_CUSID, Cus_Id)
        contentValues.put(COL_CUSNAME, Cus_name)
        contentValues.put(COl_CUSPHONE,Phoneno )


        database.insert(TABLENAME3, null, contentValues)

    }
    open fun checkusernamepassword(query: String): Boolean {
        val MyDB = this.writableDatabase
        val cursor = MyDB.rawQuery(query,null);
        if (cursor.count > 0)
            return true
        else
            return false
    }

    fun insertData_to_user(username: String,userid:String,pass: String,cDate:Long,mDate:Long) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_USER_NAME,username)
        contentValues.put(COL_USER_id, userid)
        contentValues.put(COL_PASS, pass)
        contentValues.put(COL_TIMESTAMP_CREATE,cDate)
        contentValues.put(COL_TIMESTAMP_MODIFIE,mDate)

        database.insert(TABLENAME4, null, contentValues)

    }

    fun UserData( db:SQLiteDatabase?,username: String?,userid: String?, pass: String?) {
        val values = ContentValues()
        values.put(COL_USER_NAME,username)
        values.put(COL_USER_id, userid)
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
    fun PrinterImage( Bill_No :Int ,image:String) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(Col_Image_printer,image)
        db.update(TABLENAME2, values, "Bill_No=?", arrayOf<String>(Bill_No.toString()))
        ///db.close()
    }
    private  fun generateRandomData(db: SQLiteDatabase) {
        // Generate random data for Stock table
        for (i in 1..1000) {
            val productValues = ContentValues()
            productValues.put(COL_PRODUCT_NAME, "Product $i")
            productValues.put(COl_QUANTITY, Random().nextInt(100))
            productValues.put(COL_COST, Random().nextInt(100))
            db.insert(TABLENAME1, null, productValues)
        }

        val generatedDates = HashSet<Long>()
        for (i in 1..1000) {
            val transactionValues = ContentValues()
            transactionValues.put(COL_CUSID, i)
            transactionValues.put(COL_BILL_NO, i)
            transactionValues.put(COL_PRODUCT_NAME, "Product $i")
            transactionValues.put(COl_QUANTITY2, Random().nextInt(100))
            transactionValues.put(COL_RATE, Random().nextInt(100))
            transactionValues.put(COL_AMOUNT, Random().nextInt(100))
            transactionValues.put(CoL_TOTAL_AMOUNT, Random().nextInt(1000))

            val calendar = Calendar.getInstance()
            val year = 2018 + Random().nextInt(6) // Generate a random year between 2018 and 2023
            val month = Random().nextInt(12) // Generate a random month
            val day = 1 + Random().nextInt(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) // Generate a random day
            calendar.set(year, month, day)

            var randomTimestamp = calendar.timeInMillis
            if (randomTimestamp > System.currentTimeMillis()) {
                randomTimestamp = System.currentTimeMillis()
            }

            var attempts = 0
            while (generatedDates.contains(randomTimestamp) && attempts < 10) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                randomTimestamp = calendar.timeInMillis
                attempts++
            }
            generatedDates.add(randomTimestamp)

            transactionValues.put(COL_TIMESTAMP, randomTimestamp)
            transactionValues.put(COL_SALES_USER, "SalesUser $i")
            transactionValues.put(COL_FILE_PATH, "/storage/emulated/0/DATA/Invoice$i.pdf")
            db.insert(TABLENAME2, null, transactionValues)
        }



        // Generate random data for Customer table
        for (i in 1..1000) {
            val customerValues = ContentValues()
            customerValues.put(COL_CUSID, i)
            customerValues.put(COL_CUSNAME, "Customer $i")
            customerValues.put(COl_CUSPHONE, "123456789$i")
            db.insert(TABLENAME3, null, customerValues)
        }

        // Generate random data for User table
        for (i in 1..10) {
            val userValues = ContentValues()
            userValues.put(COL_USER_NAME, "User Name$i")
            userValues.put(COL_USER_id, "User$i")
            userValues.put(COL_PASS, "password$i")
            userValues.put(COL_TIMESTAMP_CREATE, System.currentTimeMillis())
            userValues.put(COL_TIMESTAMP_MODIFIE, System.currentTimeMillis())
            db.insert(TABLENAME4, null, userValues)
        }
    }
    @SuppressLint("Range")
    fun moveDataFromTable2ToTable5(billNo: String) {
        val db = this.writableDatabase
        val moveDataQuery = "INSERT INTO " + TABLENAME5 + " SELECT * FROM " + TABLENAME2 + " WHERE " + COL_BILL_NO + " = " + billNo

        db.execSQL(moveDataQuery)

        val deleteDataQuery ="UPDATE " + TABLENAME2 + " SET " + COL_CUSID + " = NULL, " + COL_PRODUCT_NAME + " = NULL, " + COl_QUANTITY2 + " = NULL, " + COL_RATE + " = NULL, " + COL_AMOUNT + " = NULL, " + CoL_TOTAL_AMOUNT + " = NULL, " + COL_TIMESTAMP + " = NULL, " + COL_SALES_USER + " = NULL, " + COL_FILE_PATH + " = NULL WHERE " + COL_BILL_NO + " = " + billNo

        db.execSQL(deleteDataQuery)
    }
    fun undoMoveDataFromTable2ToTable5(billNo: String) {
        val db = this.writableDatabase


       val deleteDataQuery1 = "DELETE FROM " + TABLENAME2 + " WHERE " + COL_BILL_NO + " = " + billNo

        db.execSQL(deleteDataQuery1)

        // Move data back to TABLENAME2
        val undoMoveDataQuery = "INSERT INTO " + TABLENAME2 + " SELECT * FROM " + TABLENAME5 + " WHERE " + COL_BILL_NO + " = " + billNo
        db.execSQL(undoMoveDataQuery)

        // Delete data from TABLENAME5
        val deleteDataQuery = "DELETE FROM " + TABLENAME5 + " WHERE " + COL_BILL_NO + " = " + billNo
        db.execSQL(deleteDataQuery)
    }



}