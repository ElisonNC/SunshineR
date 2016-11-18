/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class TestDb extends AndroidTestCase implements Comparable<TestDb> {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
    */
  public void testCreateDb() throws Throwable {
         /*build a HashSet of all of the table names we wish to look for
         Note that there will be another table in the DB that stores the
         Android metadata (db version information)*/
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
       // locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
   }


    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {

        SQLiteDatabase db = createDb();
        Assert.assertEquals(true, db.isOpen());

        Long id = insertLocationDataInLocationTable(db);
        Assert.assertTrue(id != -1);

        ArrayList arrayValues = createLocationValues();

        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

        ArrayList result = getRowFromTable(cursor);
        Assert.assertEquals("are not equals", result, arrayValues);

        cursor.close();
        db.close();
        }

        public ArrayList getRowFromTable(Cursor cursor){

            ArrayList result = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                result.add(cursor.getString(1));
                result.add(cursor.getString(2));
                result.add(cursor.getString(3));
                cursor.moveToNext();
            }
            return  result;
         }

         public ArrayList getRowFromWeatherTable(Cursor cursor){

        ArrayList result = new ArrayList<>();

        while (!cursor.isAfterLast()) {


            result.add(cursor.getString(1));
            result.add(cursor.getString(2));
            result.add(cursor.getString(3));
            result.add(cursor.getString(4));
            result.add(cursor.getString(5));
            result.add(cursor.getString(6));
            result.add(cursor.getString(7));
            result.add(cursor.getString(8));
            result.add(cursor.getString(9));
            result.add(cursor.getString(10));


            cursor.moveToNext();
        }
        return  result;
    }

    public String getIDFromTable(Cursor cursor){

        String id = "";

        while (!cursor.isAfterLast()) {
            id = (cursor.getString(0));
            cursor.moveToNext();
        }
        return  id;
    }
        public ArrayList createLocationValues (){

            ArrayList arrayValues = new ArrayList<>();

            ContentValues values = TestUtilities.createNorthPoleLocationValues();

            arrayValues.add(values.getAsString("city_name"));
            arrayValues.add(values.getAsString("coord_lat"));
            arrayValues.add(values.getAsString("coord_long"));

            return  arrayValues;
        }

    public ArrayList createWeatherValues (Long id){

        ArrayList arrayValues = new ArrayList<>();

        ContentValues values = TestUtilities.createWeatherValues(id);

        arrayValues.add(values.getAsString("location_id"));
        arrayValues.add(values.getAsString("date"));
        arrayValues.add(values.getAsString("weather_id"));
        arrayValues.add(values.getAsString("short_desc"));
        arrayValues.add(values.getAsString("min"));
        arrayValues.add(values.getAsString("max"));
        arrayValues.add(values.getAsString("humidity"));
        arrayValues.add(values.getAsString("pressure"));
        arrayValues.add(values.getAsString("wind"));
        arrayValues.add(values.getAsString("degrees"));


        return  arrayValues;
    }

    public ContentValues createWeatherContentValues (Long id){

        ArrayList arrayValues = new ArrayList<>();

        ContentValues values = TestUtilities.createWeatherValues(id);


        return  values;
    }
        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)

        // Insert ContentValues into database and get a row ID back

        // Query the database and receive a Cursor back

        // Move the cursor to a valid database row

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database


    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {


        SQLiteDatabase db = createDb();
        Assert.assertEquals(true, db.isOpen());

        Long returnedID = insertLocationDataInLocationTable(db);
        Assert.assertTrue(returnedID != -1);

        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();

       // ArrayList result = getRowFromTable(cursor);
        Long locationID = Long.parseLong(getIDFromTable(cursor));
        Long weatherReturnedID = insertWeatherDataInWeatherTable(db,locationID);
        Assert.assertTrue(weatherReturnedID != -1);

        ArrayList weatherArrayValues = createWeatherValues(locationID);


        Cursor cursor2 = db.query(WeatherContract.WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor2.moveToFirst();
        ContentValues values = createWeatherContentValues(locationID);

        ArrayList result = getRowFromWeatherTable(cursor2);


        Collections.sort(result);
        Collections.sort(weatherArrayValues);

        Assert.assertEquals("are not equals", result, weatherArrayValues);

        cursor.close();
        cursor2.close();
        db.close();
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        // First step: Get reference to writable database

        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)

        // Insert ContentValues into database and get a row ID back

        // Query the database and receive a Cursor back

        // Move the cursor to a valid database row

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public SQLiteDatabase  createDb (){

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();

        return db;
    }

    public long insertLocationDataInLocationTable(SQLiteDatabase db) {

        // First step: Get reference to writable database

        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        Long id = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,values);


        return id;
    }

    public long insertWeatherDataInWeatherTable(SQLiteDatabase db, Long locationId) {

        // First step: Get reference to writable database

        ContentValues values = TestUtilities.createWeatherValues(locationId);

        Long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,values);


        return id;
    }

    @Override
    public int compareTo(TestDb testDb) {
        return 0;
    }
}
