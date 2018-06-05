package naveropenapi.example.com.aduinoproject.DB;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import ai.api.AIListener;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import naveropenapi.example.com.aduinoproject.R;

/**
 * Created by hyunungLim on 2018-05-14.
 */

public class DialogFlow implements AIListener {

    public AIService aiService;
    private Context mContext;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    View view;


    public DialogFlow(Context context) {
        mContext = context;

        view = View.inflate(mContext, R.layout.voice_loading, null);

        final AIConfiguration config = new AIConfiguration("253a2d36e8d2494cb0f3975029fa9e09",
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(context, config);
        aiService.setListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

    }

    public void button_Clicked() {
        aiService.startListening();

    }



    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        Toast.makeText(mContext,"리스닝 시작",Toast.LENGTH_SHORT).show();

        ImageView rabbit = view.findViewById(R.id.loadGif);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(rabbit);
        Glide.with(mContext).load(R.drawable.load).into(gifImage);

        LinearLayout lay = view.findViewById(R.id.loadingPage);
        lay.setVisibility(View.VISIBLE);

    }

    @Override
    public void onListeningCanceled() {

        Toast.makeText(mContext,"리스닝 캔슬",Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.loadingPage).setVisibility(View.GONE);
    }

    @Override
    public void onListeningFinished() {

        Toast.makeText(mContext,"리스닝 피니쉬",Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.loadingPage).setVisibility(View.GONE);
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
//
//        Toast.makeText(mContext,"Query:" + result.getResolvedQuery() +
//                " Action: " + result.getAction() +
//                " Parameters: " + parameterString,Toast.LENGTH_LONG).show();


        //캘랜더에서 날짜 정보를 가져옴
        Calendar c = Calendar.getInstance();
        //데이터 포멧을 설정
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sd2 = new SimpleDateFormat("HH:mm");
        //데이터 포멧을 설정한 현재 시간을 가져옴
        String formatDate1 = sd1.format(c.getTime());
        String formatDate2 = sd2.format(c.getTime());

        //데이터 베이스 레퍼런스를 설정
        //chat란 레퍼런스 공간에 자식 값으로 '현재 시간'을 지정
        myRef = database.getReference("MemberToken").child(user.getUid()).child("ChatBot").child(formatDate1);

        //데이터베이스에 넣을 헤쉬테이블값을 생성
        Hashtable<String, String> chat = new Hashtable<>();

        //테이블에 데이터를 추가
        //email은 사용자 정보에서 가져온 값이다.
        chat.put("id", "MISAKI");
        chat.put("email", "MISAKI");
        //에디트 텍스트에 입력한 값
        chat.put("comment", result.getFulfillment().getSpeech());
        chat.put("time", formatDate2);

        //데이터베이스 레퍼런스에 값을 set
        myRef.setValue(chat);


    }

}


