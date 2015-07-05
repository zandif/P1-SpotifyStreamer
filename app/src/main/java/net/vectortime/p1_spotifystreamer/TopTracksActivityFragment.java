package net.vectortime.p1_spotifystreamer;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {
    private TopTracksArrayAdapter mTracksAdapter;
    private String mArtistName;
    private String mArtistImageURL;
    private String mArtistId;

    public TopTracksActivityFragment() {
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

        ArrayList<TrackInfo> topTracksTestData = new ArrayList<>(10);
//        topTracksTestData.add(new TrackInfo("0", "A Sky Full of Stars", "0", null, "Ghost " +
//                "Stories"));
//        topTracksTestData.add(new TrackInfo("1", "Fix You", "1", null, "X&Y"));
//        topTracksTestData.add(new TrackInfo("2", "The Scientist", "2", null, "A Rush of Blood to " +
//                "the..."));

        ActionBar ab = getActivity().getActionBar();
        if (ab != null)
            ab.setSubtitle("Hello fragment");

        mTracksAdapter = new TopTracksArrayAdapter(getActivity(),R.layout.list_item_toptracks,
                topTracksTestData);

        ListView myListView = (ListView) rootView.findViewById(R.id.listview_toptracks);
        myListView.setAdapter(mTracksAdapter);

        if (mArtistId != null) {
            SearchSpotifyTopTracksTask searchTask = new SearchSpotifyTopTracksTask();
            searchTask.execute(mArtistId);
        }

        return rootView;
    }

    private class SearchSpotifyTopTracksTask extends AsyncTask<String, Void, TrackInfo[]> {
        private final String LOG_TAG = SearchSpotifyTopTracksTask.class.getSimpleName();

        @Override
        protected TrackInfo[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }
            String artistQueryId = params[0];

            Log.i(LOG_TAG, "artistID: " + artistQueryId);
            SpotifyApi api = new SpotifyApi();
            Map<String, Object> map = new HashMap<>();
            map.put("country", Locale.getDefault().getCountry());
            Tracks tracks = api.getService().getArtistTopTrack(artistQueryId, map);

            List<TrackInfo> info = new ArrayList<>();
            for (int i = 0; i < tracks.tracks.size(); i++){
                Track track = tracks.tracks.get(i);
                Log.i(LOG_TAG, i + " " + track.name);
                info.add(new TrackInfo(track.id, track.name, track.album.id, track.album.images,
                        track.album.name));
            }
            return info.toArray(new TrackInfo[info.size()]);
        }

        @Override
        protected void onPostExecute(TrackInfo[] inTrackInfo) {
            super.onPostExecute(inTrackInfo);
            if (inTrackInfo != null && inTrackInfo.length > 0) {
                mTracksAdapter.clear();
                int max = 10;
                if (inTrackInfo.length < 10)
                    max = inTrackInfo.length;
                for (int i = 0; i < max; i++){
                    mTracksAdapter.addAll(inTrackInfo[i]);
                }
            } else {
                // Display a toast
                Context context = getActivity();
                CharSequence text = "No track results found for the artist " + mArtistName;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

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
            if (thumbnailURL != null)
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

    private class TrackInfo {

        String songId;
        String songTitle;
        String ablumId;
        List<Image> images;
        String albumTitle;

        public TrackInfo(String inId, String inName, String inAlbumId, List<Image> inAlbumImages,
                         String inAlbumName) {
            this.songId = inId;
            this.songTitle = inName;
            this.ablumId = inAlbumId;
            this.images = inAlbumImages;
            this.albumTitle = inAlbumName;
        }

        public String getSmallestImage() {
            String returnString = null;
            int smallest_size = 0;
            if (images != null && images.size() > 0)

                for (int i = 0; i < images.size(); i++) {
                    if (smallest_size == 0) {
                        smallest_size = images.get(i).width;
                        returnString = images.get(i).url;
                    }

                    if (images.get(i).width < smallest_size) {
                        returnString = images.get(i).url;
                        smallest_size = images.get(i).width;
                    }
                }
            return returnString;
        }
    }
}
