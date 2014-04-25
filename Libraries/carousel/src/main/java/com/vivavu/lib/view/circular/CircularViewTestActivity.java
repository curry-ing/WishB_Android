package com.vivavu.lib.view.circular;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

import com.vivavu.lib.R;

import java.util.ArrayList;
import java.util.List;

public class CircularViewTestActivity extends Activity {

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_view_test);

        mContext = getApplicationContext();

        SemiCircularList listview = (SemiCircularList) findViewById(R.id.layout_card);
        //CircularList listview = (CircularList) findViewById(R.id.layout_card);
        ArrayList<DummyData> itemList = new ArrayList<DummyData>();
        for(int i=0; i<8; i++) {
            DummyData temp = new DummyData("#" + Integer.toString(i),
                    "Card description. Test card number #" + Integer.toString(i),
                    Color.rgb(128 + i * 2, 64 + i * 2, 64 + i * 2) );
            itemList.add(temp);
        }

        CircularAdapter circularAdapter = new CircleAdapter(mContext, itemList);
        listview.setAdapter(circularAdapter);

    }

    public static List getDummyData(){
        ArrayList<DummyData> itemList = new ArrayList<DummyData>();
        for(int i=0; i<8; i++) {
            DummyData temp = new DummyData("#" + Integer.toString(i), "Card description. Test card number #" + Integer.toString(i),
                    Color.rgb(128 + i * 2, 64 + i * 2, 64 + i * 2) );
            itemList.add(temp);
        }
        return itemList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public static class DummyData {
        public String mName = null;
        public String mDesc = null;
        public int mBGColor = 0;

        public DummyData(String name, String desc, int bgcolor) {
            this.mName = name;
            this.mDesc = desc;
            this.mBGColor = bgcolor;
        }
    }
}
