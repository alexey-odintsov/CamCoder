package com.alekso.camcoder.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alekso.camcoder.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexey.odintsov on 20.06.2016.
 */
public class DirectoryChooserDialog extends DialogFragment {
    private static final String TAG = "DIR";
    private File mRoot;
    private ArrayList<File> mItems = new ArrayList<>();
    private DirectoryAdapter adapter;
    private TextView tvParent;
    private OnDirectorySelectListener listener;

    public interface OnDirectorySelectListener {
        void onDirectorySelect(String path);
    }

    public static DirectoryChooserDialog newInstance(File root, OnDirectorySelectListener listener) {
        DirectoryChooserDialog dialog = new DirectoryChooserDialog();

        Bundle args = new Bundle();
        args.putString("root", root.getAbsolutePath());
        dialog.setArguments(args);

        dialog.listener = listener;

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String rootString = getArguments().getString("root");
        mRoot = new File(rootString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Select category");

        View v = inflater.inflate(R.layout.directory_chooser, container, false);
        ListView lv = (ListView) v.findViewById(R.id.listView);

        tvParent = (TextView) v.findViewById(R.id.tvParent);

        adapter = new DirectoryAdapter(getContext(), R.layout.directory_item, mItems);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mRoot = mItems.get(i);
                Log.d(TAG, "selected: " + mRoot);
                updateDirs(mRoot);
                adapter.notifyDataSetChanged();
            }
        });


        Button btnSelect = (Button) v.findViewById(R.id.btnChoose);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getContext(), mRoot.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onDirectorySelect(mRoot.getAbsolutePath());
                }
                dismiss();
            }
        });


        Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        updateDirs(mRoot);
        return v;
    }

    /**
     *
     */
    class DirectoryAdapter extends ArrayAdapter<File> {
        private int mResourceId;

        public DirectoryAdapter(Context context, int resource, List<File> objects) {
            super(context, resource, objects);
            mResourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                view = View.inflate(getContext(), mResourceId, null);
            }

            File file = this.getItem(position);
            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            if (position == 0) {
                tvName.setText("..");
            } else {
                if (file.canWrite()) {
                    tvName.setTextColor(Color.GREEN);
                } else {
                    tvName.setTextColor(Color.BLACK);
                }
                tvName.setText("/" + file.getName());
            }

            return view;
        }
    }

    /**
     * Update listview items
     *
     * @param parent
     */
    private void updateDirs(File parent) {
        mItems.clear();
        File[] files = parent.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory(); // && file.canWrite();
            }
        });

        if (parent.getParent() != null) {
            File p = parent.getParentFile();
            mItems.add(p);
        }


        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                mItems.add(files[i]);
            }
        }

        // set name of the current directory
        tvParent.setText(mRoot != null ? mRoot.getAbsolutePath() : "/");

    }
}
