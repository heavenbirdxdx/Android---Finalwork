package com.xdx.yyh.bit.finalwork.MiniDouyin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.Person;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class OperatorHelper extends AppCompatActivity {

    private PersonDbHelper mPersonDbHelper;
//    public LoginPerson loginPerson ;

    public OperatorHelper(Context context){
        mPersonDbHelper = new PersonDbHelper(context);
//        loginPerson = (LoginPerson)getApplication();
    }

//    判断数据库中是否存在此人
    public boolean isExist(String number){
//        loginPerson = (LoginPerson)getApplication();
        SQLiteDatabase db = mPersonDbHelper.getReadableDatabase();
        String[] projection = {
                PersonDb.PersonInfo.COLUMN_ID_TITLE
        };
        String selection = PersonDb.PersonInfo.COLUMN_NUM_TITLE + " =" + number;

        if(db == null){
            return false;
        }
        Cursor cursor = null;

        try{
            cursor = db.query(PersonDb.PersonInfo.TABLE_NAME, projection,
                    selection, null,null,null, null);
        }finally {
            if(cursor.getCount() > 0){
                cursor.close();
                return true;
            }
            else
                cursor.close();
                return false;

        }
    }

//    更改头像
    public boolean updatePersonPortraitFromDatabase(String imgPath, String number){
        SQLiteDatabase db = mPersonDbHelper.getWritableDatabase();
//        loginPerson = (LoginPerson)getApplication();
        ContentValues values = new ContentValues();

        values.put(PersonDb.PersonInfo.COLUMN_PORTRAIT_TITLE,
                imgPath);

        String[] numArray = new String[1];
        numArray[0] = number;

        int count = db.update(PersonDb.PersonInfo.TABLE_NAME, values,
                PersonDb.PersonInfo.COLUMN_NUM_TITLE + "=?", numArray);

        db.close();
        if(count > 0){
//            loginPerson.getLoginPerson().setPortrait(imgPath);
            return true;
        }
        else
            return false;
    }

//    更改个人信息_学号不动
    public boolean updatePersonFromDatabase(String number, String name,String birth){
        SQLiteDatabase db = mPersonDbHelper.getWritableDatabase();
//        loginPerson = (LoginPerson)getApplication();
        ContentValues values = new ContentValues();

        values.put(PersonDb.PersonInfo.COLUMN_NAME_TITLE,
                name);
        values.put(PersonDb.PersonInfo.COLUMN_BIRTHDAY_TITLE,
                birth);

        String[] numArray = new String[1];
        numArray[0] = number;
        int count = db.update(PersonDb.PersonInfo.TABLE_NAME, values,
                PersonDb.PersonInfo.COLUMN_NUM_TITLE + "=?", numArray);

        db.close();
        if (count > 0) {
//            loginPerson.getLoginPerson().setName(name);
//            loginPerson.getLoginPerson().setBirth(birth);
            return true;
        } else {
            return false;
        }
    }

//    更新数据库中的data
public boolean updatePersonDateFromDatabase(String date, String number){
    SQLiteDatabase db = mPersonDbHelper.getWritableDatabase();
//        loginPerson = (LoginPerson)getApplication();
    ContentValues values = new ContentValues();

    values.put(PersonDb.PersonInfo.COLUMN_DATE_TITLE,
            date);

    String[] numArray = new String[1];
    numArray[0] = number;
    int count = db.update(PersonDb.PersonInfo.TABLE_NAME, values,
            PersonDb.PersonInfo.COLUMN_NUM_TITLE + "=?", numArray);

    db.close();
    if (count > 0) {
        return true;
    } else {
        return false;
    }
}


//    获取数据库中的个人全部信息
    public List<Person> getPersonInfoFromDatabase(String number){
        SQLiteDatabase db = mPersonDbHelper.getReadableDatabase();
//        loginPerson = (LoginPerson)getApplication();
        String[] projection = {
                PersonDb.PersonInfo.COLUMN_ID_TITLE,
                PersonDb.PersonInfo.COLUMN_NUM_TITLE,
                PersonDb.PersonInfo.COLUMN_NAME_TITLE,
                PersonDb.PersonInfo.COLUMN_BIRTHDAY_TITLE,
                PersonDb.PersonInfo.COLUMN_DATE_TITLE ,
                PersonDb.PersonInfo.COLUMN_PORTRAIT_TITLE
        };
        String selection = PersonDb.PersonInfo.COLUMN_NUM_TITLE + " =" + number;

        String sortOrder =
                PersonDb.PersonInfo.COLUMN_ID_TITLE + " ASC";
        if(db == null){
            return Collections.emptyList();
        }
        List<Person> result = new LinkedList<>();
        Cursor cursor = null;

        try{
            cursor = db.query(PersonDb.PersonInfo.TABLE_NAME, projection,
                    selection, null,null,null, sortOrder);
            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(PersonDb.PersonInfo.COLUMN_ID_TITLE));
                String num = cursor.getString(cursor.getColumnIndex(PersonDb.PersonInfo.COLUMN_NUM_TITLE));
                String name = cursor.getString(cursor.getColumnIndex(PersonDb.PersonInfo.COLUMN_NAME_TITLE));
                String birth = cursor.getString(cursor.getColumnIndex(PersonDb.PersonInfo.COLUMN_BIRTHDAY_TITLE));
                String portraitPath = cursor.getString(cursor.getColumnIndex(PersonDb.PersonInfo.COLUMN_PORTRAIT_TITLE));
                String dateMs = cursor.getString(cursor.getColumnIndex(PersonDb.PersonInfo.COLUMN_DATE_TITLE));
                Person person = new Person(id);
                person.setNum(num);
                person.setName(name);
                person.setBirth(birth);
                person.setDate(dateMs);
                person.setPortrait(portraitPath);

                result.add(person);
            }

            Person person = result.get(0);
//            loginPerson.setLoginPerson(person);

        }finally {
            cursor.close();
            if(result.size()!=0){
                return result;
            }
            else
                return Collections.emptyList();
        }
    }


//    加入一个人
    public Person insertPersonFromDatabase(String imgPath, String number,String name,
                                            String birth,String date,String oriNumber){
        try{
            SQLiteDatabase db = mPersonDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(PersonDb.PersonInfo.COLUMN_NUM_TITLE,number);
            values.put(PersonDb.PersonInfo.COLUMN_NAME_TITLE, name);
            values.put(PersonDb.PersonInfo.COLUMN_BIRTHDAY_TITLE,birth);
            values.put(PersonDb.PersonInfo.COLUMN_DATE_TITLE, date);
            values.put(PersonDb.PersonInfo.COLUMN_PORTRAIT_TITLE,imgPath);

            long newRowId = db.insert(PersonDb.PersonInfo.TABLE_NAME,null,values);
            List<Person> people = getPersonInfoFromDatabase(number);

            String[] numberArray = new String[1];
            numberArray[0] = oriNumber;
//            int count = db.update(PersonDb.PersonInfo.TABLE_NAME, values,
//                PersonDb.PersonInfo.COLUMN_NUM_TITLE + "=?", numArray);
            long oldRowId = db.delete(PersonDb.PersonInfo.TABLE_NAME,
                    PersonDb.PersonInfo.COLUMN_NUM_TITLE + "=?",
                    numberArray);

            return people.get(0);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            
        }
    }

}
