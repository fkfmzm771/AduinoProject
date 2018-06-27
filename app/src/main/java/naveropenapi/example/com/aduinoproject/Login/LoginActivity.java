package naveropenapi.example.com.aduinoproject.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import naveropenapi.example.com.aduinoproject.DeveloperKey;
import naveropenapi.example.com.aduinoproject.FireBase.FireBaseDB;
import naveropenapi.example.com.aduinoproject.MainActivity;
import naveropenapi.example.com.aduinoproject.R;

public class LoginActivity extends AppCompatActivity {
    final static String TAG = "MainActivityTag";

    //    private Button btn_Login;
//    private Button btn_Register;
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar pb;
    private LinearLayout anime_layout;

    private Button googleSignBtn;
    private Button faceBookSignBtn;
    private Button naverSignBtn;
    private LoginButton faceBookSingBtn_main;

    //firebaseDatabase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //firebaseAuth
    public static FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private CallbackManager callbackManager;

    //네이버 로그인
    private NaverLogin mNaverLogin;


    // fireBase 로그인 식별 값
    private static final int GOO_SIGN_IN = 9001;
    private static final int FACE_SIGN_IN = 9002;

    private FireBaseDB mFireBaseDB;


    void setView() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        anime_layout = (LinearLayout) findViewById(R.id.login_loading_anime);
        pb = (ProgressBar) findViewById(R.id.progress_main);

        googleSignBtn = (Button) findViewById(R.id.googleSignBtn);
        faceBookSignBtn = (Button) findViewById(R.id.facebookSignBtn);
        naverSignBtn = (Button) findViewById(R.id.naverSignBtn);
        faceBookSingBtn_main = (LoginButton) findViewById(R.id.facebooksignBtn_main);
        mFireBaseDB = new FireBaseDB();
        mNaverLogin = new NaverLogin(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        Log.e(TAG, "on_create");
        mAuth = FirebaseAuth.getInstance();
        setView();
        google_Login();
        authStateL();

        //페이스북 콜백 매니저
        callbackManager = CallbackManager.Factory.create();


        googleSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOO_SIGN_IN);
            }
        });

        faceBookSingBtn_main.setReadPermissions("public_profile", "email");
        faceBookSingBtn_main.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });

        faceBookSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceBookSingBtn_main.performClick();
            }
        });

        naverSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NaverLogin.mOAuthLoginModule.startOauthLoginActivity(
                        LoginActivity.this, mNaverLogin.mOAuthLoginHandler);
//                Log.e("네이버 로그인", NaverLogin.mOAuthLoginModule.getAccessToken(getApplicationContext()));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginCheck.NaverCheck = true;
                finish();
            }
        });

    }


    //FirebaseAuth.AuthStateListener 구현
    private void authStateL() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e("리스너테스트", "작동중");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + "sign in");

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginCheck.FaceCheck = true;
                    finish();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + "sign out");
                }
            }
        };
    }

    //구글 연동 메서드
    private void google_Login() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(DeveloperKey.GOOGLE_DEVELOPER_KEY)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            }
                        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 엑세스 성공
                            Log.d(TAG, "signInWithCredential: 로그인 성공");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // 엑세스 실패
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    // 구글 로그인 연동 종료****************


    //페이스북 연동
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //페이스북 Sign 인텐트 호출
    private void signInFaceBook() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, FACE_SIGN_IN);
    }


    //Result값 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        Log.e("GoogleLogin", "personName=" + requestCode + " " + resultCode);

        if (requestCode == GOO_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("GoogleLogin", "personName=" + result);
            if (result.isSuccess()) {
                // 로그인 성공 했을때
                GoogleSignInAccount acct = result.getSignInAccount();

                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                String tokenKey = acct.getIdToken();

                Log.e("GoogleLogin", "personName=" + personName);
                Log.e("GoogleLogin", "personEmail=" + personEmail);
                Log.e("GoogleLogin", "personId=" + personId);
                Log.e("GoogleLogin", "tokenKey=" + tokenKey);

                firebaseAuthWithGoogle(acct);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginCheck.GoogleCheck = true;
                finish();
            } else {
                Log.e("GoogleLogin", "login fail cause=" + result.getStatus().getStatusMessage());
                // 로그인 실패 했을때
            }
        }
        if (requestCode == FACE_SIGN_IN) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                String uid = user.getUid();
                Uri photoUrl = user.getPhotoUrl();
                boolean emailVerified = user.isEmailVerified();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginCheck.FaceCheck = true;
                finish();

            } else {
                Log.e("FaceLogin", "Face login fail cause");
                // 로그인 실패 했을때
            }
        }

        //페이스북 콜백
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e(TAG, "on_resume");
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //로그인 지속 리스너
        //로그인이 활성화 중일때 로그인창 패스
        Log.e(TAG, "on_start");
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "on_stop");
        // mAuthStateListener 값이 null이 아닌 경우 재 로그인 행위
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void updateUI(FirebaseUser user) {
        mUser = user;
    }
}


