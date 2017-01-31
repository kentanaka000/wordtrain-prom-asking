package com.example.kentaro.wordtrain;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public String nextWord (String current, List<String> prev) {
        List<String> list = new ArrayList<String>();
        String word;
        int tempFreq;
        //find the word and get its distance
        Cursor finder = database.rawQuery("SELECT dist FROM wordlist WHERE word = '"+current+"'", null);
        finder.moveToFirst();
        int distance = finder.getInt(0)-1;
        finder.close();

        //break string to 4 chars
        char[] each = new char[4];
        for (int i = 0; i < 4; i++) {
            each[i] = current.charAt(i);
        }

        int maxFreq = 0;
        while (distance < 10) {
            String strDist = Integer.toString(distance);

            //find similar words with distance - 1
            Cursor change1 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '_" +
                    each[1] + each[2] + each[3] + "' AND dist = " + strDist + " ORDER BY freq DESC", null);
            change1.moveToFirst();
            while (!change1.isAfterLast() && find(change1.getString(0),prev)) change1.moveToNext();
            if (!change1.isAfterLast()) {
                //get the all words of max freq into a list;

                maxFreq = change1.getInt(1);
                do {
                    if (maxFreq != change1.getInt(1)) break;
                    if (!find(change1.getString(0),prev)) list.add(change1.getString(0));
                }
                while (change1.moveToNext());
            }
            change1.close();
            //repeat x4


            Cursor change2 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '" + each[0] + "_"
                    + each[2] + each[3] + "' AND dist = " + strDist + " ORDER BY freq DESC", null);
            change2.moveToFirst();
            while (!change2.isAfterLast() && find(change2.getString(0),prev)) change2.moveToNext();

            if (!change2.isAfterLast()) {
                //check if freq is higher here
                tempFreq = change2.getInt(1);
                if (tempFreq >= maxFreq) {
                    if (tempFreq > maxFreq) {
                        list.clear();
                        maxFreq = tempFreq;
                    }
                    do {
                        if (maxFreq != change2.getInt(1)) break;
                        if (!find(change2.getString(0),prev))
                            list.add(change2.getString(0));
                    }
                    while (change2.moveToNext());

                }
            }

            change2.close();

            Cursor change3 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '" + each[0]
                    + each[1] + "_" + each[3] + "' AND dist = " + strDist + " ORDER BY freq DESC", null);

            change3.moveToFirst();
            while (!change3.isAfterLast() && find(change3.getString(0),prev)) change3.moveToNext();

            if (!change3.isAfterLast()) {
                //check if freq is higher here
                tempFreq = change3.getInt(1);
                if (tempFreq >= maxFreq) {
                    if (tempFreq > maxFreq) {
                        list.clear();
                        maxFreq = tempFreq;
                    }
                    do {
                        if (maxFreq != change3.getInt(1)) break;
                        if (!find(change3.getString(0),prev))
                            list.add(change3.getString(0));
                    }
                    while (change3.moveToNext());

                }
            }
            change3.close();

            Cursor change4 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '" + each[0]
                    + each[1] + each[2] + "_" + "' AND dist = " + strDist + " ORDER BY freq DESC", null);


            change4.moveToFirst();
            while (!change4.isAfterLast() && find(change4.getString(0),prev)) change4.moveToNext();

            if (!change4.isAfterLast()) {
                tempFreq = change4.getInt(1);
                //check if freq is higher here
                if (tempFreq >= maxFreq) {
                    if (tempFreq > maxFreq) {
                        list.clear();
                        maxFreq = tempFreq;
                    }
                    do {
                        if (maxFreq != change4.getInt(1)) break;
                        if (!find(change4.getString(0),prev))
                            list.add(change4.getString(0));
                    }
                    while (change4.moveToNext());

                }
            }
            change4.close();

            ((ArrayList<String>) list).trimToSize();
            if (!list.isEmpty()) {
                word = list.get((int) (Math.random() * list.size()));
                return word;
            }
            distance++;
        }
        return "fuck";
    }


    public int nextDist (String current, List<String> prev) {
        List<String> list = new ArrayList<String>();
        int tempFreq;
        //find the word and get its distance
        Cursor finder = database.rawQuery("SELECT dist FROM wordlist WHERE word = '"+current+"'", null);
        finder.moveToFirst();
        int distance = finder.getInt(0)-1;
        finder.close();

        //break string to 4 chars
        char[] each = new char[4];
        for (int i = 0; i < 4; i++) {
            each[i] = current.charAt(i);
        }

        int maxFreq = 0;
        while (distance < 10) {
            String strDist = Integer.toString(distance);

            //find similar words with distance - 1
            Cursor change1 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '_" +
                    each[1] + each[2] + each[3] + "' AND dist = " + strDist + " ORDER BY freq DESC", null);
            change1.moveToFirst();
            while (!change1.isAfterLast() && find(change1.getString(0),prev)) change1.moveToNext();
            if (!change1.isAfterLast()) {
                //get the all words of max freq into a list;

                maxFreq = change1.getInt(1);
                do {
                    if (maxFreq != change1.getInt(1)) break;
                    if (!find(change1.getString(0),prev)) list.add(change1.getString(0));
                }
                while (change1.moveToNext());
            }
            change1.close();
            //repeat x4


            Cursor change2 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '" + each[0] + "_"
                    + each[2] + each[3] + "' AND dist = " + strDist + " ORDER BY freq DESC", null);
            change2.moveToFirst();
            while (!change2.isAfterLast() && find(change2.getString(0),prev)) change2.moveToNext();

            if (!change2.isAfterLast()) {
                //check if freq is higher here
                tempFreq = change2.getInt(1);
                if (tempFreq >= maxFreq) {
                    if (tempFreq > maxFreq) {
                        list.clear();
                        maxFreq = tempFreq;
                    }
                    do {
                        if (maxFreq != change2.getInt(1)) break;
                        if (!find(change2.getString(0),prev))
                            list.add(change2.getString(0));
                    }
                    while (change2.moveToNext());

                }
            }

            change2.close();

            Cursor change3 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '" + each[0]
                    + each[1] + "_" + each[3] + "' AND dist = " + strDist + " ORDER BY freq DESC", null);

            change3.moveToFirst();
            while (!change3.isAfterLast() && find(change3.getString(0),prev)) change3.moveToNext();

            if (!change3.isAfterLast()) {
                //check if freq is higher here
                tempFreq = change3.getInt(1);
                if (tempFreq >= maxFreq) {
                    if (tempFreq > maxFreq) {
                        list.clear();
                        maxFreq = tempFreq;
                    }
                    do {
                        if (maxFreq != change3.getInt(1)) break;
                        if (!find(change3.getString(0),prev))
                            list.add(change3.getString(0));
                    }
                    while (change3.moveToNext());

                }
            }
            change3.close();

            Cursor change4 = database.rawQuery("SELECT word,freq FROM wordlist WHERE word LIKE '" + each[0]
                    + each[1] + each[2] + "_" + "' AND dist = " + strDist + " ORDER BY freq DESC", null);


            change4.moveToFirst();
            while (!change4.isAfterLast() && find(change4.getString(0),prev)) change4.moveToNext();

            if (!change4.isAfterLast()) {
                tempFreq = change4.getInt(1);
                //check if freq is higher here
                if (tempFreq >= maxFreq) {
                    if (tempFreq > maxFreq) {
                        list.clear();
                        maxFreq = tempFreq;
                    }
                    do {
                        if (maxFreq != change4.getInt(1)) break;
                        if (!find(change4.getString(0),prev))
                            list.add(change4.getString(0));
                    }
                    while (change4.moveToNext());

                }
            }
            change4.close();

            ((ArrayList<String>) list).trimToSize();
            if (!list.isEmpty()) {
                break;
            }
            distance++;
        }
        return distance;
    }

    public String getRandomWord() {
        String word;
        Cursor cursor = database.rawQuery("SELECT word FROM wordlist WHERE dist = 5 AND freq > 1 ORDER BY RANDOM()", null);
        cursor.moveToFirst();
        word = cursor.getString(0);
        cursor.close();
        return word;
    }

    public boolean check(String current, String next, List<String> list) {
        Cursor cursor = database.rawQuery("SELECT dist,common FROM wordlist WHERE word = '"+next+"'", null);
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            cursor.close();
            return false;
        }
        if (cursor.getInt(1)==1) {
            cursor.close();
            return true;
        }
        int dist = cursor.getInt(0);
        cursor.close();

        if (nextDist(current,list) + 1<dist) {
            list.add(next);
            return false;
        }
        else {
            return true;
        }
    }

    public List<String> getQuotes() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM quotes", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    boolean find(String search, List<String> list) {
        for(String str: list) {
            if(str.trim().contains(search))
                return true;
        }
        return false;
    }
}