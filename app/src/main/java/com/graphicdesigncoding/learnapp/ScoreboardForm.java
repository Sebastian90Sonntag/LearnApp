package com.graphicdesigncoding.learnapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.graphicdesigncoding.learnapp.databinding.ScoreboardFormBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardForm extends Fragment {
    private ScoreboardFormBinding binding;
    private CustomArrayAdapter userArrayAdapter;
    private int resultListCount;
    private final MainActivity activity = ((MainActivity)getActivity());
    private final List<User> resultList = new ArrayList<>();
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ScoreboardFormBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.listView_scoreboard);
        userArrayAdapter = new CustomArrayAdapter(getContext(), R.layout.listview_row_layout);
        listView.setAdapter(userArrayAdapter);
        readData(view,getContext());

    }



    public void readData(View view,Context context){
        SharedPreferences sharedPref;
        if (context != null) {
            sharedPref = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            new CallAPI().Post(
                "https://api.graphic-design-coding.de/scoreboard",
                "{\"t\":\"" + sharedPref.getString("UToken", null) + "\"}",
                new Callback() {
                    @Override
                    public void finished(Object responseMsg) {
                        JSONObject obj;

                        System.out.println(responseMsg.toString());
                        try {

                            obj = new JSONObject(responseMsg.toString());
                            String token;
                            String str_md5Data = new Crypt().md5("data");
                            String str_md5Token = new Crypt().md5("token");

                            try {

                                if (obj.has(str_md5Token) && obj.has(str_md5Data)) {


                                    token = obj.getString(str_md5Token);

                                    if (token.equals(sharedPref.getString("UToken", null))) {

                                        JSONArray data_j_array = obj.getJSONArray(str_md5Data);
                                        resultListCount = obj.getJSONArray(str_md5Data).length();

                                        for (int i = 0; i < obj.getJSONArray(str_md5Data).length(); i++) {

                                            UserItem user = new UserItem();
                                            JSONObject j_object = data_j_array.getJSONObject(i);
                                            String u_img = j_object.getString("image_link");
                                            user.name = j_object.getString("username");
                                            user.score = j_object.getString("score");
                                            if (activity != null){
                                                if (activity.isBitmapInMemoryCache(u_img)) {
                                                    user.image = activity.getBitmapFromMemCache(u_img);
                                                    resultList.add(new User(user.image, user.name, user.score));
                                                    OrderList();

                                                } else if (!u_img.isEmpty()) {

                                                    new CallAPI().GetImage(
                                                            u_img,
                                                            new Callback() {

                                                                @Override
                                                                public void finished(Object responseMsg) {

                                                                    activity.addBitmapToMemoryCache(u_img, (Bitmap) responseMsg);
                                                                    user.image = activity.getBitmapFromMemCache(u_img);
                                                                    resultList.add(new User(user.image, user.name, user.score));
                                                                    OrderList();
                                                                }

                                                                @Override
                                                                public void canceled() {
                                                                    user.image = activity.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_account_avatar);
                                                                    resultList.add(new User(user.image, user.name, user.score));
                                                                    OrderList();
                                                                }
                                                            }
                                                    );

                                                } else {

                                                    user.image = activity.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_account_avatar);
                                                    resultList.add(new User(user.image, user.name, user.score));
                                                    OrderList();

                                                }
                                            }
                                        }

                                    } else {

                                        System.out.println("token wrong inner control");

                                    }

                                } else if (obj.has(new Crypt().md5("error"))) {

                                    String error;


                                    error = obj.getString(new Crypt().md5("error"));
                                    System.out.println("idk1");
                                    System.out.println(error);
                                    Toast.makeText(view.getContext(), "Wrong login data", Toast.LENGTH_LONG).show();

                                } else {

                                    //User not exist :D
                                    System.out.println("idk3");
                                    Toast.makeText(view.getContext(), "Wrong login data", Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException e) {

                                Toast.makeText(view.getContext(), "Service error", Toast.LENGTH_LONG).show();
                            }
                        } catch (Throwable e) {

                            Toast.makeText(view.getContext(), "Device Service error", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void canceled() {
                        // No connection to server || No internet
                        Toast.makeText(view.getContext(), "No Connection to Server", Toast.LENGTH_LONG).show();
                    }

                });
        }

    }
    @Override
    public void onResume() {
        super.onResume();
                if(activity!=null){
                    activity.showExtendedBar(true,"Score Board", true);
                }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void OrderList(){
        if (resultList.size() == resultListCount){
            resultList.sort(Collections.reverseOrder());
            for (User item:resultList) {
                userArrayAdapter.add(item);
            }
        }
    }
}
