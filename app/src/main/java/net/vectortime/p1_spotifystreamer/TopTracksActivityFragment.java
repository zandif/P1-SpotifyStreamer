package net.vectortime.p1_spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.vectortime.p1_spotifystreamer.dataClasses.TrackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {
    private TopTracksArrayAdapter mTracksAdapter;
    private String mArtistName;
    private String mArtistImageURL;
    private String mArtistId;
    private ArrayList<TrackInfo> mTracksList;

    private final String PARCEL_KEY = "tracks";

    public TopTracksActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if (mTracksList != null) {
            outState.putParcelableArrayList(PARCEL_KEY, mTracksList);
//            Log.i(TopTracksActivityFragment.class.getSimpleName(), "Saving " + mTracksList.size() +
//                    " entries to parcel.");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCEL_KEY)) {
            mTracksList = new ArrayList<>(10);
        } else {
            mTracksList = savedInstanceState.getParcelableArrayList(PARCEL_KEY);
//            Log.i(TopTracksActivityFragment.class.getSimpleName(), "Got " + mTracksList.size() + " " +
//                    "entries from parcel.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_toptracks, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistName = intent.getStringExtra(Intent.EXTRA_TEXT);
//            TextView myTextView = (TextView) rootView.findViewById(R.id.text_detail);
//            myTextView.setText(mForecastStr);

            if (intent.hasExtra(Intent.EXTRA_SHORTCUT_ICON)) {
                mArtistImageURL = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ICON);
            }

            if (intent.hasExtra(Intent.EXTRA_UID)) {
                mArtistId = intent.getStringExtra(Intent.EXTRA_UID);
            }
        }

//        mTracksList.add(new TrackInfo("0", "A Sky Full of Stars", "0", null, "Ghost " +
//                "Stories"));
//        mTracksList.add(new TrackInfo("1", "Fix You", "1", null, "X&Y"));
//        mTracksList.add(new TrackInfo("2", "The Scientist", "2", null, "A Rush of Blood to " +
//                "the..."));

        mTracksAdapter = new TopTracksArrayAdapter(getActivity(),R.layout.list_item_toptracks,
                mTracksList);

        ListView myListView = (ListView) rootView.findViewById(R.id.listview_toptracks);
        myListView.setAdapter(mTracksAdapter);

        if (mArtistId != null && mTracksList.size() < 1) {
            SearchSpotifyTopTracksTask searchTask = new SearchSpotifyTopTracksTask();
            searchTask.execute(mArtistId);
        }

        return rootView;
    }

    // Network check - as suggested by reviewer
    // Pulled from http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class SearchSpotifyTopTracksTask extends AsyncTask<String, Void, TrackInfo[]> {
        private final String LOG_TAG = SearchSpotifyTopTracksTask.class.getSimpleName();

        @Override
        protected TrackInfo[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }
            String artistQueryId = params[0];

//            Log.i(LOG_TAG, "artistID: " + artistQueryId);
            SpotifyApi api = new SpotifyApi();
            Map<String, Object> map = new HashMap<>();
            map.put("country", Locale.getDefault().getCountry());

            List<TrackInfo> info = null;

            if (isNetworkAvailable()) {
                try {
                    Tracks tracks = api.getService().getArtistTopTrack(artistQueryId, map);

                    info = new ArrayList<>();

                    for (int i = 0; i < tracks.tracks.size(); i++) {
                        Track track = tracks.tracks.get(i);
//                        Log.i(LOG_TAG, i + " " + track.name);
                        info.add(new TrackInfo(track.id, track.name, track.album.id, track.album.images,
                                track.album.name));
                    }
                } catch (RetrofitError error) {
                    return null;
                }
            } else {
                return null;
            }
            return info.toArray(new TrackInfo[info.size()]);
        }

        @Override
        protected void onPostExecute(TrackInfo[] inTrackInfo) {
            super.onPostExecute(inTrackInfo);
            if (inTrackInfo == null) {
                showToast("Error with network connection. Please check your network settings");
            } else if (inTrackInfo != null && inTrackInfo.length > 0) {
                mTracksAdapter.clear();
                int max = 10;
                if (inTrackInfo.length < 10)
                    max = inTrackInfo.length;
                for (int i = 0; i < max; i++){
                    mTracksAdapter.addAll(inTrackInfo[i]);
                }
            } else {
                // Display a toast
                showToast("No track results found for the artist " + mArtistName);
            }
        }

        public void showToast (CharSequence inText) {
            Context context = getActivity();
            CharSequence text = inText;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private class TopTracksArrayAdapter extends ArrayAdapter<TrackInfo> {
        private final String LOG_TAG = TopTracksArrayAdapter.class.getSimpleName();

        public TopTracksArrayAdapter(Context context, int resource, List<TrackInfo> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TrackInfo trackInfo = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout
                        .list_item_toptracks, parent, false);
            }
            if (trackInfo == null)
                return convertView;

            ImageView iconView = (ImageView) convertView.findViewById(R.id
                    .list_item_toptracks_imageview);
            String thumbnailURL = trackInfo.getSmallestImage();
            if (thumbnailURL != null && thumbnailURL.length() > 0)
//                Picasso.with(getContext()).load("http://i.imgur.com/DvpvklR.png").into(iconView);
                Picasso.with(getContext()).load(trackInfo.getSmallestImage()).into(iconView);
            else
                iconView.setImageResource(R.drawable.streamer_logo);

            TextView songTitle = (TextView) convertView.findViewById(R.id
                    .list_item_toptracks_songtitle_textview);
            songTitle.setText(trackInfo.songTitle);

            TextView ablumName = (TextView) convertView.findViewById(R.id
                    .list_item_toptracks_albumtitle_textview);
            ablumName.setText(trackInfo.albumTitle);

            return convertView;
        }
    }
}
