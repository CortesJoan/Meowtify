package com.example.meowtify;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.meowtify.models.Album;
import com.example.meowtify.models.Playlist;
import com.example.meowtify.models.Song;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistService {
    String token = "ids=7ouMYWpwJ422jRcDASZB7P%2C4VqPOruhp5EdPBeR92t6lQ%2C2takcwOaAZWiXQijPHIx7B";
    String token2 = "BQBmx65FSMiYCKgQh-_0LHs-_GrOAiHlfcKc1x8oaoDxhUDP7FsLghn9N0MSJaSt8wLofLAePIO0zcZTL8z_pUIuieiWp47T8YYlQ8dRKAB7zFCNnhoDa86pyYLbBhEFXCd5QHeDH9GYm771YZe15TeAwQuPMUKrM2Ej2bRKYjyWvQ0vHnzt9vziMU8nB4cPjKDFpD3CnoRyHbTZVDIUz4fzif4Ul3a6XIVgDxXXCLBoy2dYtn5tmCXl-tTpntdmR-WNVHzdmYMeY-ujttt_XXOuh8TPiiSI71zuVKExgwyl";
     private SharedPreferences sharedPreferences;
    //
    private RequestQueue queue;
    private ArrayList<Playlist> playlists= new ArrayList<>();
    private ArrayList<Playlist> featuredPlaylists= new ArrayList<>();

    public PlaylistService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }


    public ArrayList<Playlist> getPlaylists() {
        System.out.println(playlists.toString() + "Vendetta");
        return playlists;
    }
    public ArrayList<Playlist> getFeaturedPlaylistsPlaylists() {
        System.out.println(playlists.toString() + "Vendetta");
        return featuredPlaylists;
    }



    public List<Playlist> getUserPlayLists(final VolleyCallBack callBack, int max, int offset) {
        List<Playlist> playlists = new ArrayList<>();
        String endpoint = "https://api.spotify.com/v1/me/playlists?limit=" + max + "&offset=" + offset;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    System.out.println("Response :  " + response.toString());
                    Gson gson = new Gson();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = response.getJSONArray("items");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {

                        assert jsonArray != null;
                        System.out.println(jsonArray.toString());
                        for (int n = 0; n < jsonArray.length(); n++) {

                            JSONObject object1 = jsonArray.getJSONObject(n);
                            System.out.println("print playlist user" + object1.toString());
                            //     object = object.optJSONObject("tracks");
                            Playlist p = gson.fromJson(object1.toString(), Playlist.class);
                            System.out.println(p.toString());
                            playlists.add(p);
                            this.playlists.add(p);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    callBack.onSuccess();
                }, error -> {
                    System.out.println("Error");
                    // TODO: Handle error

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

        return playlists;
    }
    List<Album> newReleases= new ArrayList<>();
    List<Playlist> developersPlaylist= new ArrayList<>();

    public List<Album> getNewReleases() {
        return newReleases;
    }
    public List<Playlist> getDevelopersPlaylist(){
        System.out.println("Developers playlist is " + developersPlaylist.toString()) ;
     return developersPlaylist;
    }

    public List<Album> getNewReleases(final  VolleyCallBack callBack, String country, int limit, int offset){
 String endpoint=    "https://api.spotify.com/v1/browse/new-releases?country=" + country  + "&limit=" + limit + "&offset="+ offset;
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, endpoint, null, response -> {
                System.out.println("Response :  " + response.toString());
                Gson gson = new Gson();
                JSONObject jsonObject=   response.optJSONObject("albums");
                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    assert jsonArray != null;
                    System.out.println(jsonArray.toString());
                    for (int n = 0; n < jsonArray.length(); n++) {

                        JSONObject object1 = jsonArray.getJSONObject(n);
                        System.out.println("print new releases album" + object1.toString());
                        //     object = object.optJSONObject("tracks");
                        Album a = gson.fromJson(object1.toString(), Album.class);
                        System.out.println(a.toString());
                        newReleases.add(a);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callBack.onSuccess();
            }, error -> {
                System.out.println("Error");
                // TODO: Handle error

            }) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            String token = sharedPreferences.getString("token", "");
            String auth = "Bearer " + token;
            headers.put("Authorization", auth);
            return headers;
        }
    };
    queue.add(jsonObjectRequest);

    return newReleases;
}

    public void getAPlayListByRef(final VolleyCallBack callBack, String playlistId) {
        String endpoint = "https://api.spotify.com/v1/playlists/"+playlistId;
         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        Gson gson = new Gson();
                        System.out.println("JSON: " + response.toString());
                        Playlist p = gson.fromJson(response.toString(), Playlist.class);
                        System.out.println("Playlist p " + p.toString());
                        JSONArray jsonArray =
                                response.getJSONObject("tracks").getJSONArray("items");
                        System.out.println(jsonArray.toString());
                        for (int n = 0; n < jsonArray.length(); n++) {

                            JSONObject object1 = jsonArray.getJSONObject(n).getJSONObject("track");
                            System.out.println("last print" + object1.toString());

                            //     object = object.optJSONObject("tracks");
                            Song s = gson.fromJson(object1.toString(), Song.class);
                            System.out.println("Song " + n + ": " + s.toString());

                        }                             developersPlaylist.add(p);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

    }

    //todo move this to Playlist Service
    public List<Playlist> getFeaturedPlayList(final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/browse/featured-playlists";
        List<Playlist> playlists = new ArrayList<>();
        //+"-H \"Accept: application/json\" -H \"Content-Type: application/json\" -H \"Authorization: Bearer " + MainActivity.TOKEN;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        Gson gson = new Gson();
                        String message = response.getString("message");
                        System.out.println(message);
                        JSONObject object = response.getJSONObject("playlists");
                        System.out.println(object.toString());
                        JSONArray jsonArray = object.getJSONArray("items");
                        System.out.println(jsonArray.toString());
                        for (int n = 0; n < jsonArray.length(); n++) {

                            JSONObject object1 = jsonArray.getJSONObject(n);
                            System.out.println("last print" + object1.toString());
                            //     object = object.optJSONObject("tracks");
                            Playlist p = gson.fromJson(object1.toString(), Playlist.class);
                            System.out.println(p.toString());
                            featuredPlaylists.add(p);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return playlists;
    }


}
