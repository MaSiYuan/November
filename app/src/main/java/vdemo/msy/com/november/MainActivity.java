package vdemo.msy.com.november;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msy.vlib.flip.BasePageFactory;
import com.msy.vlib.flip.FlipWidget;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private FlipWidget flipWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flipWidget = findViewById(R.id.flip_widget);
        flipWidget.postDelayed(new Runnable() {
            @Override
            public void run() {
                showData();
            }
        }, 50);

    }

    private void showData() {
        ArrayList<String> numList = new ArrayList<>();
        numList.add("http://pic7.nipic.com/20100518/3409334_031036048098_2.jpg");
        numList.add("http://pic1.16pic.com/00/07/85/16pic_785034_b.jpg");
        numList.add("http://www.taopic.com/uploads/allimg/110910/2518-11091022301758.jpg");
        numList.add("http://pic40.nipic.com/20140424/10558908_213423765000_2.jpg");
        numList.add("http://pic13.nipic.com/20110307/2222821_094740634000_2.jpg");
        numList.add("http://pic.58pic.com/58pic/12/46/15/57y58PICrwD.jpg");
        numList.add("http://pic14.nipic.com/20110427/5006708_200927797000_2.jpg");
        numList.add("http://img2.imgtn.bdimg.com/it/u=210464971,3093822652&fm=214&gp=0.jpg");
        numList.add("http://pic26.nipic.com/20121231/4838084_110353658135_2.jpg");
        numList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510645585&di=df8e65096a294131d59ee912ad96d15f&imgtype=jpg&er=1&src=http%3A%2F%2Ffile31.mafengwo.net%2FM00%2F4B%2F46%2FwKgBs1cGzOmAfu59AA6GRdjRJjc14.groupinfo.w680.jpeg");

        BasePageFactory<String> factory = new BasePageFactory<String>(R.layout.item_flip_widget) {
            @Override
            protected void showData(View view, String item) {
                TextView tvContent = view.findViewById(R.id.tv_content);
                ImageView ivImg = view.findViewById(R.id.iv_img);
                tvContent.setText(item);
                Glide.with(MainActivity.this)
                        .load(item)
                        .thumbnail(0.2f)
                        .into(ivImg);
            }
        };
        factory.setData(numList);
        flipWidget.setPageFactory(factory);
    }
}
