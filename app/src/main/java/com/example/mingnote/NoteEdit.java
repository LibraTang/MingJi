package com.example.mingnote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.R.attr.bitmap;
import static android.R.attr.switchMinWidth;

public class NoteEdit extends AppCompatActivity {
    private EditText edit_content;
    private Database DBHelper;
    public int enter_state = 0;//用来区分是新建一个note还是更改原来的note
    public String last_content;//用来获取edittext内容
    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        picture = (ImageView) findViewById(R.id.picture);
        setSupportActionBar(toolbar);
        InitView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
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
                data.putString("nei", content);
                Intent intent = new Intent(NoteEdit.this, Display.class);
                intent.putExtra("neirong", data);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(NoteEdit.this, "com.example.cameraalbumtest.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
        }
    }
}
