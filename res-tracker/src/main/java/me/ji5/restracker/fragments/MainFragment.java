package me.ji5.restracker.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import me.ji5.restracker.R;
import me.ji5.restracker.datatypes.BatteryInfo;
import me.ji5.restracker.datatypes.ResourceSnapshot;
import me.ji5.restracker.datatypes.TrafficUsage;
import me.ji5.restracker.utils.Log;

public class MainFragment extends ListFragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private Realm realm;
    private ArrayList<ResourceSnapshot> mResSnapshotList = new ArrayList<ResourceSnapshot>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CommonFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        try {
            realm = new Realm(getActivity().getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ResSnapshotAdapter(getActivity(), mResSnapshotList));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addResourceSnapshot();
                break;
            case R.id.btn_refresh:
                onRefresh();
                break;
        }
    }

    protected void addResourceSnapshot() {
        try {
            realm.beginWrite();

            BatteryInfo batteryInfo = new BatteryInfo(getActivity());
            TrafficUsage trafficUsage = new TrafficUsage(getActivity());

            ResourceSnapshot resourceSnapshot = realm.create(ResourceSnapshot.class);
            resourceSnapshot.setLevel(batteryInfo.getScaledLevel());
            resourceSnapshot.setRxUsage(trafficUsage.getRxUsage());
            resourceSnapshot.setTxUsage(trafficUsage.getTxUsage());

            realm.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    protected void onRefresh() {
        int sdk_int = Build.VERSION.SDK_INT;

        RealmResults<ResourceSnapshot> result = realm.where(ResourceSnapshot.class).findAll();
        ((ResSnapshotAdapter) getListAdapter()).clear();
        if (sdk_int < 11) {
            for (ResourceSnapshot rs : result) {
                ((ResSnapshotAdapter) getListAdapter()).add(rs);
            }
        } else {
            ((ResSnapshotAdapter) getListAdapter()).addAll(result);
        }
    }

    protected class ResSnapshotAdapter extends ArrayAdapter<ResourceSnapshot> {
        protected LayoutInflater mInflater;

        public ResSnapshotAdapter(Context context, ArrayList<ResourceSnapshot> objs) {
            super(context, R.layout.fragment_main_list_row, objs);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fragment_main_list_row, null);
                viewHolder = createViewHolder(convertView, position);
                if (convertView != null) {
                    convertView.setTag(viewHolder);
                } else {
                    Log.e("convertView is NULL while creating view holder!");
                }
            } else {
                if (convertView.getTag() == null) {
                    viewHolder = createViewHolder(convertView, position);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
            }

            ResourceSnapshot item = getItem(position);

            viewHolder.tv_title.setText("" + item.getLevel() + "%%");
            viewHolder.tv_subtitle.setText("Rx:" + Formatter.formatShortFileSize(getContext(), item.getRxUsage())
                    + ", Tx:" + Formatter.formatShortFileSize(getContext(), item.getTxUsage()));
            viewHolder.tv_timestamp.setText(getDateTimeString(item.getTimestamp()));
            viewHolder.iv_downloaded.setVisibility(View.INVISIBLE);
            viewHolder.tv_changelog.setVisibility(View.GONE);
            viewHolder.iv_stable.setVisibility(View.INVISIBLE);

            return convertView;
        }

        protected Context getBaseContext() {
            return getContext();
        }

        protected SimpleDateFormat getSimpleDateFormat() {
            return new SimpleDateFormat("M/d H:mm:ss");
        }

        protected String getDateTimeString(long time_milis) {
            SimpleDateFormat sdf = getSimpleDateFormat();
            String datetime = sdf.format(new Date(time_milis));

            String duration_str = "";
            long duration = System.currentTimeMillis() - time_milis;
            if (duration > 0) {
                if (duration < DateUtils.MINUTE_IN_MILLIS) {
                    duration_str = String.format(getBaseContext().getString(R.string.datetime_in_seconds), (int) (duration / DateUtils.SECOND_IN_MILLIS));
                } else if (duration < DateUtils.HOUR_IN_MILLIS) {
                    duration_str = String.format(getBaseContext().getString(R.string.datetime_in_minutes), (int) (duration / DateUtils.MINUTE_IN_MILLIS));
                } else if (duration < DateUtils.HOUR_IN_MILLIS * 24) {
                    duration_str = String.format(getBaseContext().getString(R.string.datetime_in_hours), (int) (duration / DateUtils.HOUR_IN_MILLIS));
                }
            }

            if (duration_str.length() > 0) {
                datetime = duration_str + ", " + datetime;
            }
            return datetime;
        }

        protected ViewHolder createViewHolder(View convertView, int position) {
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_changelog = (TextView) convertView.findViewById(R.id.tv_changelog);
            viewHolder.tv_subtitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
            viewHolder.tv_timestamp = (TextView) convertView.findViewById(R.id.tv_timestamp);
            viewHolder.iv_stable = (ImageView) convertView.findViewById(R.id.iv_stable);
            viewHolder.iv_downloaded = (ImageView) convertView.findViewById(R.id.iv_downloaded);

            return viewHolder;
        }

        protected class ViewHolder {
            public TextView tv_title;
            public TextView tv_changelog;
            public TextView tv_subtitle;
            public TextView tv_timestamp;
            public ImageView iv_stable;
            public ImageView iv_downloaded;
        }
    }
}
