package com.example.mingnote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.R.attr.bitmap;

public class NoteEdit extends AppCompatActivity {
    private EditText edit_content;
    private Database DBHelper;
    public int enter_state = 0;//用来区分是新建一个note还是更改原来的note
    public String last_content;//用来获取edittext内容

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        InitView();

    }

    private Bitmap resizeImage(Bitmap bitmap, int wid, int hei)
    //压缩位图
    {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = wid;
        int newHeight = hei;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizeBipmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

        return resizeBipmap;
    }

    private void InitView() {
        edit_content = (EditText) findViewById(R.id.edit_content);
        DBHelper = new Database(this);

        //获取此时时刻时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = sdf.format(date);

        //接收内容和id
        Bundle myBundle = this.getIntent().getExtras();
        last_content = myBundle.getString("info");
        enter_state = myBundle.getInt("enter_state");
        edit_content.setText(last_content);
    }


    @Override
    protected void onPause() {
        super.onPause();
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        String content = edit_content.getText().toString();
        //判断是新的内容还是修改内容
        if (enter_state == 0) {
            if (!content.equals("")) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String dateString = sdf.format(date);
                Bundle data = new Bundle();
                data.putString("nei",content);
                Intent intent = new Intent(NoteEdit.this,Display.class);
                intent.putExtra("neirong",data);
                ContentValues values = new ContentValues();
                values.put("content", content);
                values.put("date", dateString);
                db.insert("note", null, values);//插入数据
                finish();
            }
        } else {
            ContentValues values = new ContentValues();
            values.put("content", content);
            db.update("note", values, "content=?", new String[]{last_content});
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_paint:
                Intent getImage = new Intent(Intent.ACTION_PICK, null);//从文件管理器中取得数据
                getImage.addCategory(Intent.CATEGORY_OPENABLE);//增加一个可以解析的分类
                getImage.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");//取照片
                getImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(getImage, 1);
                break;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        ContentResolver resolver = getContentResolver();//解析
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri originalUri = intent.getData();
                try {
                    Bitmap originalBitmap = BitmapFactory.decodeStream(resolver
                            .openInputStream(originalUri));
                    Bitmap bitmap = resizeImage(originalBitmap, 100, 100);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                edit_content = (EditText) findViewById(R.id.edit_content);
                insertIntoEditText(getBitmapMime(bitmap, originalUri));
            }
        }
    }

    private SpannableString getBitmapMime(int pic, Uri uri)
    //获取bitmap的mime类型
    {
        String path = uri.getPath();
        SpannableString ss = new SpannableString(path);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//插入对象，起始位置，终止位置，标记
        return ss;
    }

    private void insertIntoEditText(SpannableString ss)
    //插入图片
    {
        Editable et = edit_content.getText();// 先获取Edittext中的内容
        int start = edit_content.getSelectionStart();
        et.insert(start, ss);// 设置ss要添加的位置
        edit_content.setText(et);// 把et添加到Edittext中
        edit_content.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
