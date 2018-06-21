package naveropenapi.example.com.aduinoproject.Login;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import naveropenapi.example.com.aduinoproject.MainActivity;
import naveropenapi.example.com.aduinoproject.R;
import naveropenapi.example.com.aduinoproject.Ui.IdCheck;

public class LoginActivity extends AppCompatActivity {
    final static String TAG = "MainActivityTag";

//    private Button btn_Login;
//    private Button btn_Register;
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar pb;
    private LinearLayout anime_layout;

    private Button googleSingBtn;
    private Button faceBookSingBtn;
    private LoginButton faceBookSingBtn_main;

    //firebaseDatabase
    public static FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //firebaseAuth
    public static FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private CallbackManager callbackManager;


    // 직접 회원 가입을 위한 객체
    private String stEmail = null;
    private String stPassword = null;

    // fireBase 로그인 식별 값
    private static final int GOO_SIGN_IN = 9001;
    private static final int FACE_SIGN_IN = 9002;


    void setView() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        anime_layout = (LinearLayout) findViewById(R.id.login_loading_anime);
//        btn_Login = (Button) findViewById(R.id.btn_login);
//        btn_Register = (Button) findViewById(R.id.btn_register);
        pb = (ProgressBar) findViewById(R.id.progress_main);

        googleSingBtn = (Button) findViewById(R.id.googleSignBtn);
        faceBookSingBtn = (Button) findViewById(R.id.facebookSignBtn);
        faceBookSingBtn_main = (LoginButton) findViewById(R.id.facebooksignBtn_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        Log.e(TAG,"on_create");
        mAuth = FirebaseAuth.getInstance();
        setView();
//        defult_login();
        google_Login();
        authStateL();


        googleSingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOO_SIGN_IN);
            }
        });

        callbackManager = CallbackManager.Factory.create();


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

        faceBookSingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceBookSingBtn_main.performClick();
            }
        });

    }


//    //기본 로그인 회원 가입 리스너 처리
//    private void defult_login() {
//        //로그인 버튼 리스너
//
//        btn_Login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IdCheck check = new IdCheck();
//                stEmail = etEmail.getText().toString();
//                stPassword = etPassword.getText().toString();
//                if (check.idCheck(stEmail, stPassword) == false) {
//                    Snackbar.make(getWindow().getDecorView().getRootView(),
//                            "아이디와 패스워드에 공백을 사용할 수 없습니다.", Snackbar.LENGTH_SHORT).show();
//                } else {
//                    loginUser(stEmail, stPassword);
//                }
//            }
//        });
//
//        //회원 가입 버튼 리스너
//
//        btn_Register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IdCheck check = new IdCheck();
//                stEmail = etEmail.getText().toString();
//                stPassword = etPassword.getText().toString();
//                if (check.idCheck(stEmail, stPassword) == false) {
//                    Snackbar.make(v,
//                            "아이디와 패스워드에 공백을 사용할 수 없습니다.", Snackbar.LENGTH_SHORT).show();
//                } else {
//                    registerUser(stEmail, stPassword);
//                }
//            }
//        });
//    }
//
//
//    //회원 가입 메소드
//    private void registerUser(String email, String password) {
//        anime_layout.setVisibility(View.VISIBLE);
//        pb.setVisibility(View.VISIBLE);
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "createUserWithEmail:success");
//                            Snackbar.make(getCurrentFocus(), "가입이 완료 되었습니다.",
//                                    Snackbar.LENGTH_SHORT).show();
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Snackbar.make(getCurrentFocus(), "입력값을 확인해 주세요.",
//                                    Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                            anime_layout.setVisibility(View.GONE);
//                            pb.setVisibility(View.INVISIBLE);
//                        }
//                    }
//                });
//    }
//
//    //로그인 메소드
//    private void loginUser(String email, String password) {
//        pb.setVisibility(View.VISIBLE);
//        anime_layout.setVisibility(View.VISIBLE);
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            Snackbar.make(getCurrentFocus(), "로그인 되었습니다.",
//                                    Snackbar.LENGTH_SHORT).show();
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//
//                            pb.setVisibility(View.INVISIBLE);
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//
//                        } else {
//                            pb.setVisibility(View.INVISIBLE);
//                            anime_layout.setVisibility(View.GONE);
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Snackbar.make(getCurrentFocus(), "입력값을 다시 확인하세요",
//                                    Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }
//

    //FirebaseAuth.AuthStateListener 구현
    private void authStateL() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e("리스너테스트","작동중");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + "sign in");

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
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
                finish();
            } else {
                Log.e("GoogleLogin", "login fail cause=" + result.getStatus().getStatusMessage());
                // 로그인 실패 했을때
            }
        } else if (requestCode == FACE_SIGN_IN) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                String uid = user.getUid();
                Uri photoUrl = user.getPhotoUrl();
                boolean emailVerified = user.isEmailVerified();
            }
        }

        //페이스북 콜백
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e(TAG,"on_resume");
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //로그인 지속 리스너
        //로그인이 활성화 중일때 로그인창 패스
        Log.e(TAG,"on_start");
        mAuth.removeAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"on_stop");
        // mAuthStateListener 값이 null이 아닌 경우 재 로그인 행위
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void updateUI(FirebaseUser user) {
        mUser = user;
    }

}


