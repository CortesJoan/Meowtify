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
import com.example.meowtify.models.Song;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SongService {
    //
    public Song lastSearchedSong;
    String token = "ids=7ouMYWpwJ422jRcDASZB7P%2C4VqPOruhp5EdPBeR92t6lQ%2C2takcwOaAZWiXQijPHIx7B";
    String token2 = "BQBmx65FSMiYCKgQh-_0LHs-_GrOAiHlfcKc1x8oaoDxhUDP7FsLghn9N0MSJaSt8wLofLAePIO0zcZTL8z_pUIuieiWp47T8YYlQ8dRKAB7zFCNnhoDa86pyYLbBhEFXCd5QHeDH9GYm771YZe15TeAwQuPMUKrM2Ej2bRKYjyWvQ0vHnzt9vziMU8nB4cPjKDFpD3CnoRyHbTZVDIUz4fzif4Ul3a6XIVgDxXXCLBoy2dYtn5tmCXl-tTpntdmR-WNVHzdmYMeY-ujttt_XXOuh8TPiiSI71zuVKExgwyl";
    private ArrayList<Song> songs = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;

    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void getASongByRef(final VolleyCallBack callBack, String songId) {
        String endpoint = "https://api.spotify.com/v1/tracks/" + songId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    System.out.println("JSON: " + response.toString());
                    Song p = gson.fromJson(response.toString(), Song.class);
                    System.out.println("Searched song" + p.toString());
                    lastSearchedSong = p;
                    callBack.onSuccess();
                }, error -> {
                    System.out.println("Error " + error.getMessage());

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

    public Song getLastSearchedSong() {
        return lastSearchedSong;
    }

    public ArrayList<Song> getRecentlyPlayedTracks(final VolleyCallBack callBack) {
        String endpoint = "https://api.spotify.com/v1/me/player/recently-played";
        //+"-H \"Accept: application/json\" -H \"Content-Type: application/json\" -H \"Authorization: Bearer " + MainActivity.TOKEN;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");

                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            object = object.optJSONObject("track");
                            Song song = gson.fromJson(object.toString(), Song.class);
                            System.out.println(song.toString());
                            songs.add(song);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        return songs;
    }


    public void addSongToLibrary(Song song) {
        JSONObject payload = preparePutPayload(song);
        JsonObjectRequest jsonObjectRequest = prepareSongLibraryRequest(payload);
        queue.add(jsonObjectRequest);
    }
    public void removeSongOfLibrary(Song song) {
        JSONObject payload = preparePutPayload(song);
        JsonObjectRequest jsonObjectRequest = prepareUnFavoriteATrackRequest(payload,song.getId());
        queue.add(jsonObjectRequest);
    }
    private JsonObjectRequest prepareUnFavoriteATrackRequest(JSONObject payload, String id) {
        return new JsonObjectRequest(Request.Method.DELETE, "https://api.spotify.com/v1/me/tracks?ids=" + id , payload, response -> {
        }, error -> {
            System.out.println(error.getMessage());
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

    private JsonObjectRequest prepareSongLibraryRequest(JSONObject payload) {
        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/tracks", payload, response -> {
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

    private JSONObject preparePutPayload(Song song) {
        JSONArray idarray = new JSONArray();
        idarray.put(song.getId());
        JSONObject ids = new JSONObject();
        try {
            ids.put("ids", idarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ids;
    }
    boolean lastCheck;

    public boolean isLastCheck() {
        return lastCheck;
    }

    public AtomicBoolean checkIfTheUserHasASongInFavorites(final VolleyCallBack callBack, String songId) {
        AtomicBoolean user = new AtomicBoolean(false);
        String endpoint = "https://api.spotify.com/v1/me/tracks/contains?ids=" + songId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, endpoint, null, response -> {
                    try {
                        for (int n = 0; n < response.length(); n++) {
                            user.set(response.getBoolean(n));
                            System.out.println("User has saved this song " + user);
                            lastCheck = user.get();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callBack.onSuccess();

                }, error -> {
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
}