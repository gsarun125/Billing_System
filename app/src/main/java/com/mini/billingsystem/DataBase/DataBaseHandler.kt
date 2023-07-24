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
class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE " + TABLENAME1 + " (" + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COL_PRODUCT_NAME + " VARCHAR(1000)," + COl_QUANTITY + " INTEGER," + COL_COST + " INTEGER)"
        db?.execSQL(createTable)
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



    /*
    fun getData(Current_Time:String): Cursor? {
        val db = this.writableDatabase
        val query = "SELECT  * FROM $TABLENAME WHERE $COL_DATE > $Current_Time ORDER BY $COL_DATE ASC;"


        return db.rawQuery(query, null)
    }


    fun GetRequestID(id: String): Cursor? {

        val db = this.writableDatabase
        val  query="SELECT * FROM $TABLENAME WHERE $COL_ID=$id;"
        return db.rawQuery(query, null)
    }
    fun delete(id: String) {

        val db = this.writableDatabase

        db.delete(TABLENAME, "id=?", arrayOf<String>(id))
        db.close()
    }

     */
}