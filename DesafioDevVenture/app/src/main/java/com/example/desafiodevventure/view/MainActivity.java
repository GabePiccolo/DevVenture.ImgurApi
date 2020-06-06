package com.example.desafiodevventure.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.desafiodevventure.R;
import com.example.desafiodevventure.model.Cats;
import com.example.desafiodevventure.recyclerview.CatsVH;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCatPics();
    }

    //Request API
    public void getCatPics() {
        httpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/gallery/search/?q=cats")
                .header("Authorization", "Client-ID 1ceddedc03a5d71")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                try {
                    JSONObject data = new JSONObject(response.body().string());
                    JSONArray items = data.getJSONArray("data");

                    final List<Cats> photos = new ArrayList<Cats>();

                    for(int i=0; i<items.length(); i++){
                        JSONObject item =items.getJSONObject(i);
                        Cats photo = new Cats();

                        if(item.getBoolean("is_album")){
                            photo.id = item.getString("cover");
                        } else {
                            photo.id = item.getString("id");
                        }

                        photos.add(photo);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            render(photos);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //Set data to View
    private void render(final List<Cats> photos) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        RecyclerView.Adapter<CatsVH> adapter = new RecyclerView.Adapter<CatsVH>() {
            @NonNull
            @Override
            public CatsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                CatsVH vh = new CatsVH(getLayoutInflater().inflate(R.layout.pics_layout, null));
                vh.imageView = (ImageView) vh.itemView.findViewById(R.id.photo);

                return vh;
            }

            @Override
            public void onBindViewHolder(@NonNull CatsVH holder, int position) {
                Picasso.with(MainActivity.this).load("https://i.imgur.com/" +
                        photos.get(position).id + ".jpg").into(holder.imageView);

            }

            @Override
            public int getItemCount() {
                return photos.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }
}