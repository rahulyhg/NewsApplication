package org.peenyaindustries.piaconnect.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.peenyaindustries.piaconnect.models.Category;
import org.peenyaindustries.piaconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";

    //Database Version
    private static final int DATABASE_VERSION = 3;

    //Database Name
    private static final String DATABSE_NAME = "piaconnect";

    //Table Names
    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_POST = "posts";
    private static final String TABLE_CATEGORY_POST = "category_post";

    //Categories Table
    private static final String KEY_CAT_ID = "category_id";
    private static final String KEY_CAT_TITLE = "category_title";
    private static final String KEY_CAT_DESC = "category_description";
    private static final String KEY_CAT_POST_COUNT = "category_post_count";

    //Posts Table
    private static final String KEY_POST_ID = "post_id";
    private static final String KEY_POST_TYPE = "post_type";
    private static final String KEY_POST_STATUS = "post_status";
    private static final String KEY_POST_URL = "post_url";
    private static final String KEY_POST_TITLE = "post_title";
    private static final String KEY_POST_CONTENT = "post_content";
    private static final String KEY_POST_EXCERPT = "post_excerpt";
    private static final String KEY_POST_DATE = "post_date";
    private static final String KEY_POST_MODIFIED = "post_modified";
    private static final String KEY_POST_THUMBNAIL = "post_thumbnail";
    private static final String KEY_POST_FULLIMAGE = "post_full_image";

    //Category_post Table
    private static final String KEY_CP_ID = "cp_id";
    private static final String KEY_CP_POST_ID = "post_id";
    private static final String KEY_CP_CAT_ID = "category_id";

    //Table Create Statements
    //Categories Table Create
    private static final String CREATE_CATEGORY_TABLE = "CREATE TABLE " +
            TABLE_CATEGORY + "(" + KEY_CAT_ID + " INTEGER PRIMARY KEY," +
            KEY_CAT_TITLE + " TEXT," + KEY_CAT_DESC + " TEXT," + KEY_CAT_POST_COUNT + " INTEGER" + ")";

    //Post Table Create
    private static final String CREATE_POST_TABLE = "CREATE TABLE " +
            TABLE_POST + "(" + KEY_POST_ID + " INTEGER PRIMARY KEY," +
            KEY_POST_TYPE + " TEXT," + KEY_POST_STATUS + " TEXT," + KEY_POST_URL + " TEXT," +
            KEY_POST_TITLE + " TEXT," + KEY_POST_CONTENT + " LONGTEXT," +
            KEY_POST_EXCERPT + " TEXT," + KEY_POST_DATE + " TEXT," +
            KEY_POST_MODIFIED + " TEXT," + KEY_POST_THUMBNAIL + " TEXT," + KEY_POST_FULLIMAGE + " TEXT" + ")";

    //Category_post Table Create
    private static final String CREATE_CATEGORY_POST_TABLE = "CREATE TABLE " +
            TABLE_CATEGORY_POST + "(" + KEY_CP_ID + " INTEGER PRIMARY KEY," +
            KEY_CP_CAT_ID + " INTEGER," + KEY_CP_POST_ID + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating required tables
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_POST_TABLE);
        db.execSQL(CREATE_CATEGORY_POST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_POST);

        // create new tables
        onCreate(db);
    }

    /*
     * Inserting a Post
     */
    public long insertPost(Post post, List<String> postCategories) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_POST_ID, post.getId());
        values.put(KEY_POST_TYPE, post.getType());
        values.put(KEY_POST_STATUS, post.getStatus());
        values.put(KEY_POST_URL, post.getUrl());
        values.put(KEY_POST_TITLE, post.getTitle());
        values.put(KEY_POST_CONTENT, post.getContent());
        values.put(KEY_POST_EXCERPT, post.getExcerpt());
        values.put(KEY_POST_DATE, post.getDate());
        values.put(KEY_POST_MODIFIED, post.getModified());
        values.put(KEY_POST_THUMBNAIL, post.getThumbnail());
        values.put(KEY_POST_FULLIMAGE, post.getFullImage());

        //insert row
        db.insert(TABLE_POST, null, values);

        //assigning categories to posts
        for (String postCategory : postCategories) {
            insertCategoryPost(Long.parseLong(postCategory), post.getId());
        }

        return post.getId();
    }

    /*
    * get single post
    */
    public Post getPost(long post_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_POST + " WHERE "
                + KEY_POST_ID + " = " + post_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Post post = new Post();
        if (c != null) {
            post.setId(c.getInt(c.getColumnIndex(KEY_POST_ID)));
            post.setType(c.getString(c.getColumnIndex(KEY_POST_TYPE)));
            post.setUrl(c.getString(c.getColumnIndex(KEY_POST_URL)));
            post.setStatus(c.getString(c.getColumnIndex(KEY_POST_STATUS)));
            post.setTitle(c.getString(c.getColumnIndex(KEY_POST_TITLE)));
            post.setContent(c.getString(c.getColumnIndex(KEY_POST_CONTENT)));
            post.setExcerpt(c.getString(c.getColumnIndex(KEY_POST_EXCERPT)));
            post.setDate(c.getString(c.getColumnIndex(KEY_POST_DATE)));
            post.setModified(c.getString(c.getColumnIndex(KEY_POST_MODIFIED)));
            post.setThumbnail(c.getString(c.getColumnIndex(KEY_POST_THUMBNAIL)));
            post.setFullImage(c.getString(c.getColumnIndex(KEY_POST_FULLIMAGE)));
        }

        return post;
    }

    /*
     * Fetching All Posts
     */
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<Post>();
        String selectQuery = "SELECT  * FROM " + TABLE_POST + " ORDER BY " + KEY_POST_ID + " DESC";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Post post = new Post();
                post.setId(c.getInt(c.getColumnIndex(KEY_POST_ID)));
                post.setType(c.getString(c.getColumnIndex(KEY_POST_TYPE)));
                post.setUrl(c.getString(c.getColumnIndex(KEY_POST_URL)));
                post.setStatus(c.getString(c.getColumnIndex(KEY_POST_STATUS)));
                post.setTitle(c.getString(c.getColumnIndex(KEY_POST_TITLE)));
                post.setContent(c.getString(c.getColumnIndex(KEY_POST_CONTENT)));
                post.setExcerpt(c.getString(c.getColumnIndex(KEY_POST_EXCERPT)));
                post.setDate(c.getString(c.getColumnIndex(KEY_POST_DATE)));
                post.setModified(c.getString(c.getColumnIndex(KEY_POST_MODIFIED)));
                post.setThumbnail(c.getString(c.getColumnIndex(KEY_POST_THUMBNAIL)));
                post.setFullImage(c.getString(c.getColumnIndex(KEY_POST_FULLIMAGE)));

                // adding to posts list
                posts.add(post);
            } while (c.moveToNext());
        }

        return posts;
    }

    /*
 * getting all posts under single category
 * */
    public List<Post> getAllPostsByCategory(String cat_name) {

        List<Post> posts = new ArrayList<Post>();

        String selectQuery = "SELECT  * FROM " + TABLE_POST + " p, "
                + TABLE_CATEGORY + " c, " + TABLE_CATEGORY_POST + " cp WHERE c."
                + KEY_CAT_TITLE + " = '" + cat_name + "'" + " AND c." + KEY_CAT_ID
                + " = " + "cp." + KEY_CP_CAT_ID + " AND p." + KEY_POST_ID + " = "
                + "cp." + KEY_CP_POST_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Post post = new Post();
                post.setId(c.getInt(c.getColumnIndex(KEY_POST_ID)));
                post.setType(c.getString(c.getColumnIndex(KEY_POST_TYPE)));
                post.setUrl(c.getString(c.getColumnIndex(KEY_POST_URL)));
                post.setStatus(c.getString(c.getColumnIndex(KEY_POST_STATUS)));
                post.setTitle(c.getString(c.getColumnIndex(KEY_POST_TITLE)));
                post.setContent(c.getString(c.getColumnIndex(KEY_POST_CONTENT)));
                post.setExcerpt(c.getString(c.getColumnIndex(KEY_POST_EXCERPT)));
                post.setDate(c.getString(c.getColumnIndex(KEY_POST_DATE)));
                post.setModified(c.getString(c.getColumnIndex(KEY_POST_MODIFIED)));
                post.setThumbnail(c.getString(c.getColumnIndex(KEY_POST_THUMBNAIL)));
                post.setFullImage(c.getString(c.getColumnIndex(KEY_POST_FULLIMAGE)));

                // adding to post list
                posts.add(post);
            } while (c.moveToNext());
        }

        return posts;
    }

    public long getPostCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_POST);
    }

    public long getCategoryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_CATEGORY);
    }

    /*
    * Updating a post
    */
    public int updatePost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_POST_ID, post.getId());
        values.put(KEY_POST_TYPE, post.getType());
        values.put(KEY_POST_STATUS, post.getStatus());
        values.put(KEY_POST_URL, post.getUrl());
        values.put(KEY_POST_TITLE, post.getTitle());
        values.put(KEY_POST_CONTENT, post.getContent());
        values.put(KEY_POST_EXCERPT, post.getExcerpt());
        values.put(KEY_POST_DATE, post.getDate());
        values.put(KEY_POST_MODIFIED, post.getModified());
        values.put(KEY_POST_THUMBNAIL, post.getThumbnail());
        values.put(KEY_POST_FULLIMAGE, post.getFullImage());

        // updating row
        return db.update(TABLE_POST, values, KEY_POST_ID + " = ?",
                new String[]{String.valueOf(post.getId())});
    }

    /*
    * Deleting a post
    */
    public void deletePost(long post_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POST, KEY_POST_ID + " = ?",
                new String[]{String.valueOf(post_id)});
    }

    /*
    * Insert Category
    */
    public long insertCategory(Category cat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CAT_ID, cat.getId());
        values.put(KEY_CAT_TITLE, cat.getTitle());
        values.put(KEY_CAT_DESC, cat.getDescription());
        values.put(KEY_CAT_POST_COUNT, cat.getPostCount());

        // insert row
        db.insert(TABLE_CATEGORY, null, values);

        return cat.getId();
    }

    /**
     * getting all categories
     */
    public List<Category> getAllCategories() {
        List<Category> cats = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(c.getInt((c.getColumnIndex(KEY_CAT_ID))));
                cat.setTitle(c.getString(c.getColumnIndex(KEY_CAT_TITLE)));
                cat.setDescription(c.getString(c.getColumnIndex(KEY_CAT_DESC)));
                cat.setPostCount(c.getInt(c.getColumnIndex(KEY_CAT_POST_COUNT)));

                // adding to category list
                cats.add(cat);
            } while (c.moveToNext());
        }
        return cats;
    }

    /*
    * Updating a category
    */
    public int updateCategory(Category cat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CAT_ID, cat.getId());
        values.put(KEY_CAT_TITLE, cat.getTitle());
        values.put(KEY_CAT_DESC, cat.getDescription());
        values.put(KEY_CAT_POST_COUNT, cat.getPostCount());

        // updating row
        return db.update(TABLE_CATEGORY, values, KEY_CAT_ID + " = ?",
                new String[]{String.valueOf(cat.getId())});
    }

    /*
     * Insert category post
     */
    public long insertCategoryPost(long cat_id, long post_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CP_CAT_ID, cat_id);
        values.put(KEY_CP_POST_ID, post_id);

        return db.insert(TABLE_CATEGORY_POST, null, values);
    }

    /*
    * Deleting a category
    */
    public void deleteCategory(Category cat, boolean should_delete_all_category_post) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting category
        // check if posts under this category should also be deleted
        if (should_delete_all_category_post) {
            // get all posts under this category
            List<Post> allCategoryPosts = getAllPostsByCategory(cat.getTitle());

            // delete all posts
            for (Post post : allCategoryPosts) {
                // delete post
                deletePost(post.getId());
            }
        }

        // now delete the category
        db.delete(TABLE_CATEGORY, KEY_CAT_ID + " = ?",
                new String[]{String.valueOf(cat.getId())});
    }

    public void deleteAllContent() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_POST, null, null);
        db.delete(TABLE_CATEGORY, null, null);
        db.delete(TABLE_CATEGORY_POST, null, null);

    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
