package geekomaniacs.smartfs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;

import java.io.File;
import java.util.ArrayList;

import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.utility.Utility;


public class MainActivity extends Activity {

    private static final String TAG = "SmartFS";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        mRecyclerView.addOnItemTouchListener(
//                new RecyclerView.OnItemTouchListener() {
//                    @Override
//                    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
//                        Log.v(TAG, "onInterceptTouchEvent");
//                        showPopUp(recyclerView);
//                        onTouchEvent(recyclerView, motionEvent);
//                        return true;
//                    }
//
//                    @Override
//                    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
//                        Log.v(TAG, "onTouchEvent");
//                        showPopUp(recyclerView);
//
//                    }
//                }
//        );

        // specify an adapter (see also next example)
        ArrayList<SmartFSFile> mDataset = Utility.getFileList();
//        ArrayList<SmartFSFile> mDataset = new ArrayList<SmartFSFile>();
//        mDataset.add(new SmartFSFile(new File("ABC")));
//        mDataset.add(new SmartFSFile(new File("BCD")));

        mAdapter = new MyAdapter(mDataset, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showPopUp(View view){
        PopupMenu popUp = new PopupMenu(this, view);
        popUp.inflate(R.menu.pop_up_menu);
        popUp.show();
    }
}
