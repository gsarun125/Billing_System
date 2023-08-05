package com.mini.billingsystem.DataBase


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

val TABLENAME2 = "Sales"
val COL_BILL_NO="Bill_No"
val COL_PRODUCT_ID2 = "Product_Id"
val COl_QUANTITY2="quantity"
val COL_RATE = "rate"
val COL_AMOUNT="amount"
val COL_TIMESTAMP="time"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLENAME1 + " (" + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY + " INTEGER," + COL_COST + " INTEGER)"
        db?.execSQL(createTable)

       val createTable2 = "CREATE TABLE " + TABLENAME2 + " (" + COL_BILL_NO + " INTEGER," + COL_PRODUCT_ID2 + " INTEGER," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY2 + " INTEGER," + COL_RATE + " INTEGER," + COL_AMOUNT + " INTEGER," + COL_TIMESTAMP + " LONG)"

        db?.execSQL(createTable2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertData( product_name: String,quantity:Int,cost:Int) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_PRODUCT_NAME, product_name)
     //   contentValues.put(COL_PRODUCT_ID, product_id)
        contentValues.put(COl_QUANTITY, quantity)
        contentValues.put(COL_COST,cost)

        val result = database.insert(TABLENAME1, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }


    fun update( Product_id :Int , Product_Name: String,quantity:Int,cost:Int) {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_PRODUCT_NAME, Product_Name)
        values.put(COl_QUANTITY,quantity)
        values.put(COL_COST,cost)

        db.update(TABLENAME1, values, "Product_Id=?", arrayOf<String>(Product_id.toString()))
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





    fun insertData_to_sales(Bill_id:Int,product_id:String,Product_Name: String,quantity:String,rate:String,amount:Float,time:Long) {
        val database = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_BILL_NO, Bill_id)
        contentValues.put(COL_PRODUCT_ID2, product_id)

        contentValues.put(COL_PRODUCT_NAME,Product_Name)
        contentValues.put(COl_QUANTITY2, quantity)
        contentValues.put(COL_RATE,rate)
        contentValues.put(COL_AMOUNT,amount)
        contentValues.put(COL_TIMESTAMP,time)

        val result = database.insert(TABLENAME2, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

}