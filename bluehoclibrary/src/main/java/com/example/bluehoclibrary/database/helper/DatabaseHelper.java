package com.example.bluehoclibrary.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bluehoclibrary.database.model.DBMember;
import com.example.bluehoclibrary.database.model.DBMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 2/25/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BlueHoc";

    private static final String CREATE_TABLE_MEMBER = "CREATE TABLE 'Member' (\n" +
            "\tmember_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\tbluetooth_address VARCHAR(20) UNIQUE,\n" +
            "\tbluetooth_name VARCHAR(45)\n" +
            ");";

    private static final String CREATE_TABLE_MESSAGE = "CREATE TABLE 'Message' (\n" +
            "\tmessage_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\tfrom_id INTEGER,\n" +
            "\tto_id INTEGER,\n" +
            "\tcontent VARCHAR(255),\n" +
            "\tFOREIGN KEY (from_id) REFERENCES 'Member'(member_id),\n" +
            "\tFOREIGN KEY (to_id) REFERENCES 'Member'(member_id)\n" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEMBER);
        db.execSQL(CREATE_TABLE_MESSAGE);

        Log.d(TAG, "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'Member'");
        db.execSQL("DROP TABLE IF EXISTS 'Message'");

        this.onCreate(db);
    }

    // CRUD

    public long createMember(DBMember member) {
        SQLiteDatabase db = getWritableDatabase();

        DBMember dbMember = getMemberByAddress(member.getAddress());

        if(dbMember == null) {
            ContentValues values = new ContentValues();
            values.put("bluetooth_address", member.getAddress());
            values.put("bluetooth_name", member.getName());

            long member_id = db.insert("Member", null, values);

            return member_id;
        } else {
            return -1;
        }

    }

    public long createMessage(DBMessage message) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("from_id", getMemberIdByAddress(message.getFrom().getAddress()));
        values.put("to_id", getMemberIdByAddress(message.getTo().getAddress()));
        values.put("content", message.getContent());

        Log.v(TAG, "From Address: " + message.getFrom().getAddress());

        long member_id = db.insert("Message", null, values);

        return member_id;
    }

    public DBMember getMemberByAddress(String address) {
        List<DBMember> members = getMembers();

        for(DBMember member : members) {
            if(member.getAddress().equals(address)) {
                return member;
            }
        }

        return null;
    }

    public long getMemberIdByAddress(String address) {
        List<DBMember> members = getMembers();

        for(DBMember member : members) {
            Log.v(TAG, member.getAddress() + " VS " + address);
            if(member.getAddress().equals(address)) {
                return member.getMemberId();
            }
        }

        return -1;
    }

    public DBMember getMemberById(long id) {
        List<DBMember> members = getMembers();

        for(DBMember member : members) {
            if(member.getMemberId() == id) {
                return member;
            }
        }

        return null;
    }

    public List<DBMember> getMembers() {
        ArrayList<DBMember> members = new ArrayList<>();

        String query = "SELECT * FROM Member";

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst())
        {
            do {
                DBMember member = new DBMember();

                member.setMemberId(cursor.getInt(cursor.getColumnIndex("member_id")));
                member.setAddress(cursor.getString(cursor.getColumnIndex("bluetooth_address")));
                member.setName(cursor.getString(cursor.getColumnIndex("bluetooth_name")));

                members.add(member);
            } while(cursor.moveToNext());
        }

        return members;
    }

    public List<DBMessage> getMessages() {
        ArrayList<DBMessage> messages = new ArrayList<>();

        String query = "SELECT * FROM Message";

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst())
        {
            do {
                DBMessage message = new DBMessage();

                message.setMessageId(cursor.getLong(cursor.getColumnIndex("message_id")));
                message.setFrom(getMemberById(cursor.getLong(cursor.getColumnIndex("from_id"))));
                message.setTo(getMemberById(cursor.getLong(cursor.getColumnIndex("to_id"))));
                message.setContent(cursor.getString(cursor.getColumnIndex("content")));

                Log.v(TAG, Long.toString(cursor.getLong(cursor.getColumnIndex("from_id"))));

                messages.add(message);
            } while(cursor.moveToNext());
        }

        return messages;
    }

    public List<DBMessage> getMessagesByAddress(String address) {
        List<DBMessage> messages = getMessages();
        List<DBMessage> selectedMessages = new ArrayList<>();

        for(DBMessage message : messages) {
            if(message.getFrom().getAddress().equals(address) || message.getTo().getAddress().equals(address)) {
                selectedMessages.add(message);
            }
        }


        return selectedMessages;
    }
}
