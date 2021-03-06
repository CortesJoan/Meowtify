package com.example.meowtify.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.meowtify.VolleyCallBack;
import com.example.meowtify.models.Album;
import com.example.meowtify.models.Artist;
import com.example.meowtify.models.GeneralItem;
import com.example.meowtify.models.Playlist;
import com.example.meowtify.models.Song;
import com.example.meowtify.models.Type;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlaylistService {
    public List<GeneralItem> lastSearched;
    String token = "ids=7ouMYWpwJ422jRcDASZB7P%2C4VqPOruhp5EdPBeR92t6lQ%2C2takcwOaAZWiXQijPHIx7B";
    String token2 = "BQBmx65FSMiYCKgQh-_0LHs-_GrOAiHlfcKc1x8oaoDxhUDP7FsLghn9N0MSJaSt8wLofLAePIO0zcZTL8z_pUIuieiWp47T8YYlQ8dRKAB7zFCNnhoDa86pyYLbBhEFXCd5QHeDH9GYm771YZe15TeAwQuPMUKrM2Ej2bRKYjyWvQ0vHnzt9vziMU8nB4cPjKDFpD3CnoRyHbTZVDIUz4fzif4Ul3a6XIVgDxXXCLBoy2dYtn5tmCXl-tTpntdmR-WNVHzdmYMeY-ujttt_XXOuh8TPiiSI71zuVKExgwyl";
    //
    boolean lastCheck;
    Playlist lastSearchedPlaylist;
    List<Album> newReleases = new ArrayList<>();
    List<Playlist> developersPlaylist = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<Playlist> featuredPlaylists = new ArrayList<>();

    public Playlist getLastSearchedPlaylist() {
        return lastSearchedPlaylist;
    }

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

    public List<Album> getNewReleases() {
        return newReleases;
    }

    public List<Playlist> getDevelopersPlaylist() {
        System.out.println("Developers playlist is " + developersPlaylist.toString());
        return developersPlaylist;
    }

    public List<Album> getNewReleases(final VolleyCallBack callBack, String country, int limit, int offset) {
        String endpoint = "https://api.spotify.com/v1/browse/new-releases?country=" + country + "&limit=" + limit + "&offset=" + offset;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    System.out.println("Response :  " + response.toString());
                    Gson gson = new Gson();
                    JSONObject jsonObject = response.optJSONObject("albums");
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
        String endpoint = "https://api.spotify.com/v1/playlists/" + playlistId;
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
                            p.AddSong(s);

                        }
                        developersPlaylist.add(p);
                        lastSearchedPlaylist=p;

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

    //todo give the bool and methos a better name
    public boolean isLastCheck() {
        return lastCheck;
    }

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

    public AtomicBoolean checkIfTheUserFollowsAPlaylist(final VolleyCallBack callBack, String playlistId) {
        AtomicBoolean user = new AtomicBoolean(false);
        String endpoint = "https://api.spotify.com/v1/playlists/" + playlistId + "/followers/contains?ids=" + sharedPreferences.getString("userid", null);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        for (int n = 0; n < response.length(); n++) {
                            user.set(response.getBoolean(n));
                            System.out.println("User is in the playlist " + user);
                            lastCheck = user.get();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();

                }, error -> {
                    // TODO: Handle error
                    System.out.println("error on error " + error.toString() + error.getMessage() + error.getLocalizedMessage());

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
        queue.add(jsonArrayRequest);
        return user;
    }

    public void followAPlaylist(Playlist playlist) {
        JSONObject payload = preparePutPayload(playlist);
        System.out.println("ID to follow" + " = " + playlist.getId());
        JsonObjectRequest jsonObjectRequest = prepareFollowPlaylistRequest(payload, playlist.getId());
        queue.add(jsonObjectRequest);
    }
    public void createAPlaylist(String name,String description, boolean isPublic) {
        JSONObject payload = preparePutPayloadUserPlaylist(name,description,isPublic);
         JsonObjectRequest jsonObjectRequest = prepareCreatePlaylistRequest(payload);
        queue.add(jsonObjectRequest);
    }

    private JsonObjectRequest prepareFollowPlaylistRequest(JSONObject payload, String id) {
        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/playlists/" + id + "/followers", payload, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }
    private JsonObjectRequest prepareCreatePlaylistRequest(JSONObject payload) {
        return new JsonObjectRequest(Request.Method.POST, "https://api.spotify.com/v1/users/"+sharedPreferences.getString("userid", null) + "/playlists", payload, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }

    private JsonObjectRequest prepareUnfollowPlaylistRequest(JSONObject payload, String id) {
        return new JsonObjectRequest(Request.Method.DELETE, "https://api.spotify.com/v1/playlists/" + id + "/followers", payload, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }

    private JSONObject preparePutPayload(Playlist playlist) {

        JSONObject ids = new JSONObject();
        try {
            ids.put("public", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ids;
    }
    private JSONObject preparePutPayloadUserPlaylist(String name, String description, boolean isPublic) {
        JSONObject ids = new JSONObject();
        try {
            ids.put("name", name);
            ids.put("description", description);
            ids.put("public", isPublic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public List<GeneralItem> search(final VolleyCallBack callBack, String wordsToSearch, List<Type> types, String market, int limit, int offset) {
        String endpoint = "https://api.spotify.com/v1/search?q=" + wordsToSearch + "&type=" + convertTypeListToQueryString(types) + "&market=" + market + "&limit=" + limit + "&offset" + offset;
        List<GeneralItem> result = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        JSONArray jsonArray = null;
                        Gson gson = new Gson();
                        for (Type t : types) {
                            jsonArray = response.getJSONObject(t.toString() + "s").getJSONArray("items");
                            List<JSONObject> jsonObjects = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObjects.add(jsonArray.getJSONObject(i));
                            }
                            System.out.println("tHE PRE ERROR LENGTH IS " + jsonArray.length());
                            if (jsonArray==null ||jsonArray.length()!=0){
                            switch (t) {
                                case album:
                                    Album[] a = gson.fromJson(Arrays.toString(jsonObjects.toArray()), Album[].class);
                                    for (Album album : a) {
                                        result.add(album.toGeneralItem());
                                    }
                                    break;
                                case track:
                                    Song[] s = gson.fromJson(Arrays.toString(jsonObjects.toArray()), Song[].class);
                                    for (Song song : s) {
                                        result.add(song.toGeneralItem());
                                    }
                                    break;
                                case playlist:
                                    Playlist[] p = gson.fromJson(Arrays.toString(jsonObjects.toArray()), Playlist[].class);
                                    for (Playlist playlist : p) {
                                        result.add(playlist.toGeneralItem());
                                    }
                                    break;
                                case artist:
                                    Artist[] artists = gson.fromJson(Arrays.toString(jsonObjects.toArray()), Artist[].class);
                                    for (Artist artist : artists) {
                                        result.add(artist.toGeneralItem());
                                    }
                                    break;
                            }}
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                        searchResults=result;
                    callBack.onSuccess();

                }, error -> {
                    // TODO: Handle error
                    System.out.println("error on error " + error.toString() + error.getMessage() + error.getLocalizedMessage());

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
        return result;
    }
List<GeneralItem> searchResults= new ArrayList<>();

    public List<GeneralItem> getSearchResults() {
        return searchResults;
    }

    private String convertTypeListToQueryString(List<Type> types) {
        String result = "";
        for (int i = 0; i < types.size(); i++) {
            if (types.size() - 1 != i) {
                result += types.get(i).toString() + "%2C";
            } else {
                result += types.get(i).toString();
            }
        }
        return result;
    }


    public void unfollowAPlaylist(Playlist playlist) {
        JSONObject payload = new JSONObject();
        System.out.println("ID to follow" + " = " + playlist.getId());
        JsonObjectRequest jsonObjectRequest = prepareUnfollowPlaylistRequest(payload, playlist.getId());
        queue.add(jsonObjectRequest);
    }


}
