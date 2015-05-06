package geekomaniacs.smartfs.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import geekomaniacs.smartfs.FileOperationsActivity;
import geekomaniacs.smartfs.MainActivity;
import geekomaniacs.smartfs.R;
import geekomaniacs.smartfs.beans.SmartFSFile;
import geekomaniacs.smartfs.utility.Utility;

/**
 * Created by aniket on 4/13/15.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    ArrayList<SmartFSFile> mDataset;
    static Context context;
    ContextMenu.ContextMenuInfo info;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<SmartFSFile> myDataset, Context context) {
        mDataset = myDataset;
        MyAdapter.context = context;
    }

    public MyAdapter(){

    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_entry, parent, false);

        ViewHolder vh = new ViewHolder(layout);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getFile().getName());
        holder.mSmallTextView.setText((String.valueOf(mDataset.get(position).getDownloadSize())) + Utility.SPACE + Utility.PERCENT_COMPLETED);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{
        // each data item is just a string in this case
        public TextView mTextView;
        public TextView mSmallTextView;
        public ViewHolder(LinearLayout v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.mainFileName);
            mSmallTextView = (TextView)v.findViewById(R.id.fileSize);
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,FileOperationsActivity.class);
            File file = mDataset.get(getPosition()).getFile();
            intent.putExtra(Utility.FILE_NAME, file.getName());
            intent.putExtra(Utility.FILE_SIZE, file.length());
            intent.putExtra(Utility.DATE_MODIFIED, Utility.sdf.format(file.lastModified()));
            context.startActivity(intent);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            new MyAdapter().info = menuInfo;
            menu.setHeaderTitle(Utility.SELECT_ACTION);
            menu.add(0, R.id.share, 0, Utility.SHARE);
            menu.add(0, R.id.delete, 0, Utility.DELETE);
            menu.add(0, R.id.download, 0, Utility.DOWNLOAD);
            Log.v(MainActivity.TAG, String.valueOf(getPosition()));
            Utility.position = getPosition();
        }
    }

    public void setPercent (String name, Integer done) {
        for (SmartFSFile sfs : mDataset) {
            if (sfs.getFile().getName().equals(name)) {
                sfs.setDownloadSize(done);
            }
        }
    }
}