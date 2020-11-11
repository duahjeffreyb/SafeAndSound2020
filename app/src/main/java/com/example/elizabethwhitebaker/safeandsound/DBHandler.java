package com.example.elizabethwhitebaker.safeandsound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "safeAndSoundDB.db";
    private static final String[] TABLE_NAMES = {"Initiators", "Groups", "Members", "GroupLeaders", "GroupMembers", "Events", "EventGroups"};
    private static final String[] TABLE1_COLUMNS = {"InitiatorID", "FirstName", "LastName", "Username", "PicturePath", "PhoneNumber", "Password"};
    private static final String[] TABLE2_COLUMNS = {"GroupID", "GroupName"};
    private static final String[] TABLE3_COLUMNS = {"MemberID", "FirstName", "LastName", "PhoneNumber", "ReplyStatus", "Response"};
    private static final String[] TABLE4_COLUMNS = {"GroupLeaderID", "InitiatorID", "GroupID"};
    private static final String[] TABLE5_COLUMNS = {"GroupMemberID", "GroupID", "MemberID"};
    private static final String[] TABLE6_COLUMNS = {"EventID", "EventName", "EventDescription"};
    private static final String[] TABLE7_COLUMNS = {"EventGroupID", "EventID", "GroupID"};

    DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Initiators( " +
                "InitiatorID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "FirstName TEXT, " +
                "LastName TEXT, " +
                "Username TEXT, " +
                "PicturePath TEXT, " +
                "PhoneNumber TEXT, " +
                "Password TEXT);");
        db.execSQL("CREATE TABLE Groups( " +
                "GroupID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "GroupName TEXT);");
        db.execSQL("CREATE TABLE Members( " +
                "MemberID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "FirstName TEXT, " +
                "LastName TEXT, " +
                "PhoneNumber TEXT, " +
                "ReplyStatus TEXT DEFAULT 'UNK', " +
                "Response TEXT DEFAULT 'No response to date.');");
        db.execSQL("CREATE TABLE Events( " +
                "EventID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "EventName TEXT, " +
                "EventDescription TEXT);");
        db.execSQL("CREATE TABLE GroupLeaders( " +
                "GroupLeaderID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "InitiatorID INTEGER REFERENCES Initiator(InitiatorID) ON UPDATE CASCADE, " +
                "GroupID INTEGER REFERENCES Groups(GroupID) ON UPDATE CASCADE);");
        db.execSQL("CREATE TABLE GroupMembers( " +
                "GroupMemberID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "GroupID INTEGER REFERENCES Groups(GroupID) ON UPDATE CASCADE, " +
                "MemberID INTEGER REFERENCES Members(MemberID) ON UPDATE CASCADE);");
        db.execSQL("CREATE TABLE EventGroups( " +
                "EventGroupID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "EventID INTEGER REFERENCES Events(EventID) ON UPDATE CASCADE, " +
                "GroupID INTEGER REFERENCES Groups(GroupID) ON UPDATE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if(newV != oldV) {
            db.execSQL("DROP TABLE IF EXISTS Initiators");
            db.execSQL("DROP TABLE IF EXISTS Groups");
            db.execSQL("DROP TABLE IF EXISTS Members");
            db.execSQL("DROP TABLE IF EXISTS GroupLeaders");
            db.execSQL("DROP TABLE IF EXISTS GroupMembers");
            db.execSQL("DROP TABLE IF EXISTS Events");
            db.execSQL("DROP TABLE IF EXISTS EventGroups");
            onCreate(db);
        }
    }

    void addHandler(Initiator initiator) {
        ContentValues values = new ContentValues();
        values.put(TABLE1_COLUMNS[1], initiator.getFirstName());
        values.put(TABLE1_COLUMNS[2], initiator.getLastName());
        values.put(TABLE1_COLUMNS[3], initiator.getUsername());
        values.put(TABLE1_COLUMNS[4], initiator.getPicturePath());
        values.put(TABLE1_COLUMNS[5], initiator.getPhoneNumber());
        values.put(TABLE1_COLUMNS[6], initiator.getPassword());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAMES[0], null, values);
        db.close();
    }

    void addHandler(Group group) {
        ContentValues values = new ContentValues();
        values.put(TABLE2_COLUMNS[1], group.getGroupName());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAMES[1], null, values);
        db.close();
    }

    void addHandler(Member member) {
        ContentValues values = new ContentValues();
        values.put(TABLE3_COLUMNS[1], member.getFirstName());
        values.put(TABLE3_COLUMNS[2], member.getLastName());
        values.put(TABLE3_COLUMNS[3], member.getPhoneNumber());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAMES[2], null, values);
        db.close();
    }

    void addHandler(GroupLeader groupLeader) {
        ContentValues values = new ContentValues();
        values.put(TABLE4_COLUMNS[1], groupLeader.getInitiatorID());
        values.put(TABLE4_COLUMNS[2], groupLeader.getGroupID());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAMES[3], null, values);
        db.close();
    }

    void addHandler(GroupMember groupMember) {
        ContentValues values = new ContentValues();
        values.put(TABLE5_COLUMNS[1], groupMember.getGroupID());
        values.put(TABLE5_COLUMNS[2], groupMember.getMemberID());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAMES[4], null, values);
        db.close();
    }

    void addHandler(Event event) {
        ContentValues values = new ContentValues();
        values.put(TABLE6_COLUMNS[1], event.getEventName());
        values.put(TABLE6_COLUMNS[2], event.getEventDescription());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAMES[5], null, values);
        db.close();
    }

    void addHandler(EventGroup eventGroup) {
        ContentValues values = new ContentValues();
        values.put(TABLE7_COLUMNS[1], eventGroup.getEventID());
        values.put(TABLE7_COLUMNS[2], eventGroup.getGroupID());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAMES[6], null, values);
        db.close();
    }

    Initiator findHandler(String user, String pass) {
        String query = "SELECT * FROM Initiators WHERE Username = '" + user +
                "' AND Password = '" + pass + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        Initiator i = new Initiator();
        if(c.moveToFirst()) {
            c.moveToFirst();
            i.setInitiatorID(Integer.parseInt(c.getString(0)));
            i.setFirstName(c.getString(1));
            i.setLastName(c.getString(2));
            i.setUsername(c.getString(3));
            i.setPicturePath(c.getString(4));
            i.setPhoneNumber(c.getString(5));
            i.setPassword(c.getString(6));
            c.close();
        } else {
            c.close();
            i = null;
        }
        db.close();
        return i;
    }

    ArrayList<Initiator> getAllInitiators() {
        String query = "SELECT * FROM Initiators;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        ArrayList<Initiator> inits = new ArrayList<>();
        int x = 0;
        while(c.moveToNext()) {
            Initiator i = new Initiator();
            if(x == 0)
                c.moveToFirst();
            i.setInitiatorID(Integer.parseInt(c.getString(0)));
            i.setFirstName(c.getString(1));
            i.setLastName(c.getString(2));
            i.setUsername(c.getString(3));
            i.setPicturePath(c.getString(4));
            i.setPhoneNumber(c.getString(5));
            i.setPassword(c.getString(6));
            inits.add(i);
            x++;
        }
        c.close();
        db.close();
        return inits;
    }

    Group findHandlerGroup(int groupID) {
        String query = "SELECT * FROM Groups WHERE GroupID = " + groupID + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        Group g = new Group();
        if(c.moveToFirst()) {
            c.moveToFirst();
            g.setGroupID(Integer.parseInt(c.getString(0)));
            g.setGroupName(c.getString(1));
            c.close();
        } else {
            g = null;
        }
        db.close();
        return g;
    }

    Group findHandlerGroup(String groupName) {
        String query = "SELECT * FROM Groups WHERE GroupName = '" + groupName + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        Group g = new Group();
        if(c.moveToFirst()) {
            c.moveToFirst();
            g.setGroupID(Integer.parseInt(c.getString(0)));
            g.setGroupName(c.getString(1));
            c.close();
        } else {
            g = null;
        }
        db.close();
        return g;
    }

    ArrayList<Group> getAllGroups() {
        String query = "SELECT * FROM Groups;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        ArrayList<Group> groups = new ArrayList<>();
        int x = 0;
        while(c.moveToNext()) {
            Group g = new Group();
            if (x == 0)
                c.moveToFirst();
            g.setGroupID(Integer.parseInt(c.getString(0)));
            g.setGroupName(c.getString(1));
            groups.add(g);
            x++;
        }
        c.close();
        db.close();
        return groups;
    }

    ArrayList<GroupMember> findHandlerGroupMembers(int groupID) {
        String query = "SELECT * FROM GroupMembers WHERE groupID = " + groupID + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        ArrayList<GroupMember> mems = new ArrayList<>();
        int x = 0;
        while(c.moveToNext()) {
            GroupMember gM = new GroupMember();
            if (x == 0)
                c.moveToFirst();
            gM.setGroupMemberID(Integer.parseInt(c.getString(0)));
            gM.setGroupID(Integer.parseInt(c.getString(1)));
            gM.setMemberID(Integer.parseInt(c.getString(2)));
            mems.add(gM);
            x++;
        }
        c.close();
        db.close();
        return mems;
    }

    GroupMember findHandlerGroupMember(int groupID, int memID) {
        String query = "SELECT * FROM GroupMembers WHERE GroupID = " + groupID
                + " AND " + "MemberID = " + memID + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        GroupMember gM = new GroupMember();
        if(c.moveToFirst()) {
            c.moveToFirst();
            gM.setGroupMemberID(Integer.parseInt(c.getString(0)));
            gM.setGroupID(Integer.parseInt(c.getString(1)));
            gM.setMemberID(Integer.parseInt(c.getString(2)));
            c.close();
        } else {
            gM = null;
        }
        db.close();
        return gM;
    }

    Member findHandlerMember(int memberID) {
        String query = "SELECT * FROM Members WHERE MemberID = " + memberID + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        Member m = new Member();
        if(c.moveToFirst()) {
            c.moveToFirst();
            m.setMemberID(Integer.parseInt(c.getString(0)));
            m.setFirstName(c.getString(1));
            m.setLastName(c.getString(2));
            m.setPhoneNumber(c.getString(3));
            m.setReplyStatus(c.getString(4));
            m.setResponse(c.getString(5));
            c.close();
        } else {
            m = null;
        }
        db.close();
        return m;
    }

    Member findHandlerMember(String first, String last) {
        String query = "SELECT * FROM Members WHERE FirstName = '" + first + "'" +
                " AND LastName = '" + last + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        Member m = new Member();
        if(c.moveToFirst()) {
            c.moveToFirst();
            m.setMemberID(Integer.parseInt(c.getString(0)));
            m.setFirstName(c.getString(1));
            m.setLastName(c.getString(2));
            m.setPhoneNumber(c.getString(3));
            m.setReplyStatus(c.getString(4));
            m.setResponse(c.getString(5));
            c.close();
        } else {
            m = null;
        }
        db.close();
        return m;
    }

    ArrayList<Member> getAllMembers() {
        String query = "SELECT * FROM Members;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        ArrayList<Member> mems = new ArrayList<>();
        int x = 0;
        while(c.moveToNext()) {
            Member m = new Member();
            if(x == 0)
                c.moveToFirst();
            m.setMemberID(Integer.parseInt(c.getString(0)));
            m.setFirstName(c.getString(1));
            m.setLastName(c.getString(2));
            m.setPhoneNumber(c.getString(3));
            m.setReplyStatus(c.getString(4));
            m.setResponse(c.getString(5));
            mems.add(m);
            x++;
        }
        c.close();
        db.close();
        return mems;
    }

    ArrayList<GroupMember> getAllGroupMembers(){
        String query = "SELECT * FROM GroupMembers;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        ArrayList<GroupMember> groupMembers = new ArrayList<>();
        int x = 0;
        while(c.moveToNext()){
            GroupMember gm = new GroupMember();
            if(x == 0) {
                c.moveToFirst();
            }
                gm.setGroupMemberID(Integer.parseInt(c.getString(0)));
                gm.setGroupID(Integer.parseInt(c.getString(1)));
                gm.setMemberID(Integer.parseInt(c.getString(2)));
                groupMembers.add(gm);
                x++;
            }
        c.close();
        db.close();
        return groupMembers;
    }

    public Event findHandlerEvent(int eventID) {
        String query = "SELECT * FROM Events WHERE EventID = " + eventID + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        Event e = new Event();
        if(c.moveToFirst()) {
            c.moveToFirst();
            e.setEventID(Integer.parseInt(c.getString(0)));
            e.setEventName(c.getString(1));
            e.setEventDescription(c.getString(2));
            c.close();
        } else {
            e = null;
        }
        db.close();
        return e;
    }

    Event findHandlerEvent(String eventName) {
        String query = "SELECT * FROM Events WHERE EventName = '" + eventName + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        Event e = new Event();
        if(c.moveToFirst()) {
            c.moveToFirst();
            e.setEventID(Integer.parseInt(c.getString(0)));
            e.setEventName(c.getString(1));
            e.setEventDescription(c.getString(2));
            c.close();
        } else {
            e = null;
        }
        db.close();
        return e;
    }

    ArrayList<Event> getAllEvents() {
        String query = "SELECT * FROM Events;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        ArrayList<Event> events = new ArrayList<>();
        int x = 0;
        while(c.moveToNext()) {
            Event e = new Event();
            if(x == 0)
                c.moveToFirst();
            e.setEventID(Integer.parseInt(c.getString(0)));
            e.setEventName(c.getString(1));
            e.setEventDescription(c.getString(2));
            events.add(e);
            x++;
        }
        c.close();
        db.close();
        return events;
    }

    void deleteHandler(int id, String table) {
        String tableID = table.substring(0, table.length() - 1);
        for(String name : TABLE_NAMES) {
            if (name.equals(table)) {
                String query = "DELETE FROM " + table + " WHERE " + tableID + "ID = " + id + ";";
                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL(query);
            }
        }
    }

    public boolean updateHandler(int id, String firstName, String lastName, String username, byte[] picture, String phoneNumber, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(TABLE1_COLUMNS[0], id);
        args.put(TABLE1_COLUMNS[1], firstName);
        args.put(TABLE1_COLUMNS[2], lastName);
        args.put(TABLE1_COLUMNS[3], username);
        args.put(TABLE1_COLUMNS[4], picture);
        args.put(TABLE1_COLUMNS[5], phoneNumber);
        args.put(TABLE1_COLUMNS[6], password);
        return db.update(TABLE_NAMES[0], args, TABLE1_COLUMNS[0] + "=" + String.valueOf(id), null) > 0;
    }

    public boolean updateHandler(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(TABLE2_COLUMNS[0], id);
        args.put(TABLE2_COLUMNS[1], name);
        return db.update(TABLE_NAMES[1], args, TABLE2_COLUMNS[0] + "=" + String.valueOf(id), null) > 0;
    }

    public boolean updateHandler(int id, String firstName, String lastName, String phoneNumber, boolean reply, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(TABLE3_COLUMNS[0], id);
        args.put(TABLE3_COLUMNS[1], firstName);
        args.put(TABLE3_COLUMNS[2], lastName);
        args.put(TABLE3_COLUMNS[3], phoneNumber);
        args.put(TABLE3_COLUMNS[4], reply);
        args.put(TABLE3_COLUMNS[5], comments);
        return db.update(TABLE_NAMES[2], args, TABLE3_COLUMNS[0] + "=" + String.valueOf(id), null) > 0;
    }

    public boolean updateHandlerEvent(int eventID, String eventName, String eventDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(TABLE6_COLUMNS[0], eventID);
        args.put(TABLE6_COLUMNS[1], eventName);
        args.put(TABLE6_COLUMNS[2], eventDescription);
        return db.update(TABLE_NAMES[5], args, TABLE6_COLUMNS[0] + "=" + String.valueOf(eventID), null) > 0;
    }
}
