package net.vectortime.p1_spotifystreamer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArtistArrayAdapter mArtistsAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<ArtistInfo> artistsTestData = new ArrayList<>(10);
        artistsTestData.add(new ArtistInfo("Barenaked Ladies",R.drawable.ic_launcher));
        artistsTestData.add(new ArtistInfo("Cowboy Mouth",R.drawable.ic_launcher));
        artistsTestData.add(new ArtistInfo("Stroke 9",R.drawable.streamer_logo));
        artistsTestData.add(new ArtistInfo("Stroke 8",R.drawable.streamer_logo));
        artistsTestData.add(new ArtistInfo("Stroke 7",R.drawable.streamer_logo));
        artistsTestData.add(new ArtistInfo("Stroke 6",R.drawable.streamer_logo));
        artistsTestData.add(new ArtistInfo("Stroke 5",R.drawable.streamer_logo));
        artistsTestData.add(new ArtistInfo("Stroke 4",R.drawable.streamer_logo));
        artistsTestData.add(new ArtistInfo("Stroke 3",R.drawable.streamer_logo));
        artistsTestData.add(new ArtistInfo("Stroke 2",R.drawable.streamer_logo));

//        ArrayList<String> artistsTestData = new ArrayList<>(10);
//        artistsTestData.add("Barenaked Ladies");
//        artistsTestData.add("Cowboy Mouth");
//        artistsTestData.add("Stroke 9");
//        artistsTestData.add("Stroke 8");
//        artistsTestData.add("Stroke 7");
//        artistsTestData.add("Stroke 6");
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

        return rootView;
    }

    private class ArtistInfo {
       String artistName;
        int image;

        public ArtistInfo (String inName, int inImage){
            this.artistName = inName;
            this.image = inImage;
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

            ImageView iconView = (ImageView) convertView.findViewById(R.id
                    .list_item_artist_imageview);
            iconView.setImageResource(artistInfo.image);
            TextView artistName = (TextView) convertView.findViewById(R.id
                    .list_item_artist_textview);
            artistName.setText(artistInfo.artistName);

            return convertView;

//            return super.getView(position, convertView, parent);
        }
    }
}
