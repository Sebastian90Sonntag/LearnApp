package com.graphicdesigncoding.learnapp.forms;

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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.graphicdesigncoding.learnapp.user.CustomArrayAdapter;
import com.graphicdesigncoding.learnapp.MainActivity;
import com.graphicdesigncoding.learnapp.R;
import com.graphicdesigncoding.learnapp.user.User;
import com.graphicdesigncoding.learnapp.user.UserItem;
import com.graphicdesigncoding.learnapp.api.CallAPI;
import com.graphicdesigncoding.learnapp.api.Callback;
import com.graphicdesigncoding.learnapp.api.ContentType;
import com.graphicdesigncoding.learnapp.api.Crypt;
import com.graphicdesigncoding.learnapp.api.SimpleJson;
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.databinding.ScoreboardFormBinding;

/////////////////////////////////////
//COPYRIGHT BY GraphicDesignCoding///
/////////////////////////////////////

public class ScoreboardForm extends Fragment {
    private ScoreboardFormBinding binding;
    private CustomArrayAdapter userArrayAdapter;
    private int resultListCount;
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
            new CallAPI(
                "https://api.graphic-design-coding.de/scoreboard",
                "{\"t\":\"" + sharedPref.getString("UToken", null) + "\"}",
                ContentType.APPLICATION_JSON,
                TransferMethod.POST,
                new Callback() {
                    @Override
                    public void finished(Object responseMsg) {
                        MainActivity mA = (MainActivity) requireActivity();
                        JSONObject obj;
                        SimpleJson simpleJson = new SimpleJson();
                        mA.Debug("ScoreboardForm",responseMsg.toString());
                        obj = simpleJson.Decode(responseMsg.toString());
                        String token;
                        String str_md5Data = new Crypt().md5("data");
                        String str_md5Token = new Crypt().md5("token");

                        if (obj.has(str_md5Token) && obj.has(str_md5Data)) {

                            token = simpleJson.Get(obj,str_md5Token).toString();

                            if (token.equals(sharedPref.getString("UToken", null))) {

                                JSONArray data_j_array = simpleJson.GetArray(obj,str_md5Data);
                                resultListCount = data_j_array.length();
                                for (int i = 0; i < resultListCount; i++) {

                                    UserItem user = new UserItem();
                                    JSONObject j_object = simpleJson.GetObjectInJArray(data_j_array, i);
                                    String u_img = simpleJson.Get(j_object, "image_link").toString();
                                    user.name = simpleJson.Get(j_object, "username").toString();
                                    user.score = simpleJson.Get(j_object, "score").toString();


                                    if (mA.isBitmapInMemoryCache(u_img)) {

                                        user.image = mA.getBitmapFromMemCache(u_img);
                                        resultList.add(new User(user.image, user.name, user.score));
                                        OrderList();

                                    } else if (!u_img.isEmpty()) {

                                        new CallAPI(
                                                u_img,
                                                null,
                                                ContentType.TEXT_PLAIN,
                                                TransferMethod.POST,
                                                new Callback() {

                                                    @Override
                                                    public void finished(Object responseMsg) {
                                                        MainActivity mA = (MainActivity) requireActivity();
                                                        mA.addBitmapToMemoryCache(u_img, (Bitmap) responseMsg);
                                                        user.image = mA.getBitmapFromMemCache(u_img);
                                                        resultList.add(new User(user.image, user.name, user.score));
                                                        OrderList();
                                                    }

                                                    @Override
                                                    public void canceled(Object responseMsg) {
                                                        MainActivity mA = (MainActivity) requireActivity();
                                                        mA.Debug("ScoreboardForm",responseMsg.toString());
                                                        user.image = mA.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_account_avatar);
                                                        resultList.add(new User(user.image, user.name, user.score));
                                                        OrderList();
                                                    }
                                                }
                                        );

                                    } else {

                                        user.image = mA.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_account_avatar);
                                        resultList.add(new User(user.image, user.name, user.score));
                                        OrderList();

                                    }
                                }
                            } else {

                                Toast.makeText(view.getContext(), "Wrong login data", Toast.LENGTH_LONG).show();

                            }
                        } else {

                            Toast.makeText(view.getContext(), "Service error", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void canceled(Object responseMsg) {

                        Toast.makeText(view.getContext(), "No Connection to Server", Toast.LENGTH_LONG).show();
                    }
                });
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        MainActivity mA = ((MainActivity)requireActivity());
        mA.showExtendedBar(true,"Score Board", true);
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
