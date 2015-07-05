package net.vectortime.p1_spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArtistArrayAdapter mArtistsAdapter;
    private boolean searchInProgress = false;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        EditText mEdit = (EditText)rootView.findViewById(R.id.search_textfield);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1)
                    return;
                if (!searchInProgress) {
                    SearchSpotifyArtistsTask searchTask = new SearchSpotifyArtistsTask();
                    searchTask.execute(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ArrayList<ArtistInfo> artistsTestData = new ArrayList<>(10);

//        ArrayList<String> artistsTestData = new ArrayList<>(10);
//        artistsTestData.add("Barenaked Ladies");
//        artistsTestData.add("Cowboy Mouth");
//        artistsTestData.add(new ArtistInfo("Stroke 9"));
//        artistsTestData.add(new ArtistInfo("Stroke 8"));
//        artistsTestData.add(new ArtistInfo("Stroke 7"));
//        artistsTestData.add(new ArtistInfo("Stroke 6"));
//        artistsTestData.add("Stroke 5");
//        artistsTestData.add("Stroke 4");
//        artistsTestData.add("Stroke 3");
//        artistsTestData.add("Stroke 2");

        List<ArtistInfo> artistsList = artistsTestData;

//        mArtistsAdapter = new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.list_item_artist,
//                R.id.list_item_artist_textview,
//                artistsList);

        mArtistsAdapter = new ArtistArrayAdapter(getActivity(),R.layout.list_item_artist,
                artistsList);

        ListView myListView = (ListView) rootView.findViewById(R.id.listview_artists);
        myListView.setAdapter(mArtistsAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArtistInfo info = (ArtistInfo) mArtistsAdapter.getItem(i);

//                // Display a toast
//                Context context = getActivity();
//                CharSequence text = info.artistName + " " + info.getLargestImage();
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

                // Explicit intent to launch the detail activity
                Intent topTracksIntent = new Intent(getActivity(), TopTracksActivity.class);
                topTracksIntent.putExtra(Intent.EXTRA_TEXT, info.artistName);
                topTracksIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, info.getLargestImage());
                topTracksIntent.putExtra(Intent.EXTRA_UID, info.artistId);
                startActivity(topTracksIntent);
            }
        });

        return rootView;
    }

    private class ArtistInfo {
        String artistName;
        List<Image> images;
        String artistId;

        public ArtistInfo (String inName, List<Image> inImages, String inId){
            this.artistName = inName;
            this.images = inImages;
            this.artistId = inId;
        }

        public ArtistInfo (String inName, List<Image> inImages){
            this.artistName = inName;
            this.images = inImages;
            this.artistId = null;
        }

        public ArtistInfo (String inName){
            this.artistName = inName;
            this.images = null;
            this.artistId = null;
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

        public String getLargestImage() {
            String returnString = null;
            int lagest_size = 0;
            if (images != null && images.size() > 0)

                for (int i = 0; i < images.size(); i++) {
                    if (images.get(i).width > lagest_size) {
                        returnString = images.get(i).url;
                        lagest_size = images.get(i).width;
                    }
                }
            return returnString;
        }
    }

    private class ArtistArrayAdapter extends ArrayAdapter<ArtistInfo> {
        private final String LOG_TAG = ArtistArrayAdapter.class.getSimpleName();


        public ArtistArrayAdapter(Context context, int resource, List<ArtistInfo> objects) {


            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ArtistInfo artistInfo = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout
                        .list_item_artist, parent, false);
            }
            if (artistInfo == null)
                return convertView;

            ImageView iconView = (ImageView) convertView.findViewById(R.id
                    .list_item_artist_imageview);
            String thumbnailURL = artistInfo.getSmallestImage();
            if (thumbnailURL != null)
//                Picasso.with(getContext()).load("http://i.imgur.com/DvpvklR.png").into(iconView);
                Picasso.with(getContext()).load(artistInfo.getSmallestImage()).into(iconView);
            else
                iconView.setImageResource(R.drawable.streamer_logo);

            TextView artistName = (TextView) convertView.findViewById(R.id
                    .list_item_artist_textview);
            artistName.setText(artistInfo.artistName);

            return convertView;
        }
    }

    private class SearchSpotifyArtistsTask extends AsyncTask<String, Void, ArtistInfo[]> {
        private final String LOG_TAG = SearchSpotifyArtistsTask.class.getSimpleName();

        @Override
        protected ArtistInfo[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }
            String artistQueryName = params[0];

            SpotifyApi api = new SpotifyApi();

            ArtistsPager results = api.getService().searchArtists(artistQueryName);
            List<Artist> artists = results.artists.items;
            List<ArtistInfo> info = new ArrayList<>();
            for (int i = 0; i < artists.size(); i++){
                Artist artist = artists.get(i);
                Log.i(LOG_TAG, i + " " + artist.name);
                info.add(new ArtistInfo(artist.name, artist.images, artist.id));
                for (int j = 0; j < artist.images.size(); j++) {
//                    kaaes.spotify.webapi.android.models.Image huh = artist.images.get(j);
                    Log.i(LOG_TAG, j + " " + artist.images.get(j).url + " " + artist.images.get
                            (j).width.toString() + "x" + artist.images.get(j).height.toString());
                }
            }
            return info.toArray(new ArtistInfo[info.size()]);
        }

        @Override
        protected void onPostExecute(ArtistInfo[] inArtistInfo) {
            super.onPostExecute(inArtistInfo);
            if (inArtistInfo != null && inArtistInfo.length > 0) {
                mArtistsAdapter.clear();
                int max = 10;
                if (inArtistInfo.length < 10)
                    max = inArtistInfo.length;
                for (int i = 0; i < max; i++){
                    mArtistsAdapter.addAll(inArtistInfo[i]);
                }
            } else {
                // Display a toast
                Context context = getActivity();
                CharSequence text = "No results found for that artist";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            searchInProgress = false;
        }
    }
}
