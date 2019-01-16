package com.example.amin.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.amin.criminalintent.models.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    
    public Crime getCrime() {
        UUID uuid = UUID.fromString(getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID)));
        String title = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        Date date = new Date(getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE)));
        boolean isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED)) != 0;
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(uuid);
        crime.setTitle(title);
        crime.setDate(date);
        crime.setSolved(isSolved);
        crime.setSuspect(suspect);
        
        return crime;
    }
}
