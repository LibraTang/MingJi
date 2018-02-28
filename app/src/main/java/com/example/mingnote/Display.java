package com.example.mingnote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Display extends AppCompatActivity{
    EditText display;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        display = (EditText) findViewById(R.id.display);
        String imagePath = getIntent().getBundleExtra("neirong").getString(
                "nei");
        SpannableString ss = new SpannableString(imagePath);
        Pattern p=Pattern.compile("/mnt/sdcard/.+?\\.\\w{3}");
        Matcher m=p.matcher(imagePath);
        while(m.find()){
            Bitmap bm = BitmapFactory.decodeFile(m.group());
            Bitmap rbm = resizeImage(bm, 100, 100);
            ImageSpan span = new ImageSpan(this, rbm);
            ss.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        display.setText(ss);
    }

    private Bitmap resizeImage(Bitmap bitmap, int wid, int hei) {
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
}
