package naveropenapi.example.com.aduinoproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    final static String TAG = "MainActivityTag";

    private Button btn_Login;
    private Button btn_Register;
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar pb;
    private Button googleSingBtn;
    private LinearLayout anime_layout;

    public static FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // 직접 회원 가입을 위한 객체
    private String stEmail = null;
    private String stPassword = null;

    // fireBase 로그인 식별 값
    private static final int RC_SIGN_IN = 9001;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //로그인 지속 리스너
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // mAuthStateListener 값이 null이 아닌 경우 재 로그인 행위
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void updateUI(FirebaseUser user) {
        mUser = user;
    }


    void setView() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        anime_layout = (LinearLayout) findViewById(R.id.login_loading_anime);
        googleSingBtn = (Button) findViewById(R.id.googleSignBtn);
        btn_Login = (Button) findViewById(R.id.btn_login);
        btn_Register = (Button) findViewById(R.id.btn_register);
        pb = (ProgressBar) findViewById(R.id.progress_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        setView();
        defult_login();

        google_Login();
        mAuth = FirebaseAuth.getInstance();

        googleSingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });
        authStateL();
    }


    //기본 로그인 회원 가입 리스너 처리
    private void defult_login() {
        //로그인 버튼 리스너

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdCheck check = new IdCheck();
                stEmail = etEmail.getText().toString();
                stPassword = etPassword.getText().toString();
                if (check.idCheck(stEmail, stPassword) == false) {
                    Snackbar.make(getWindow().getDecorView().getRootView(),
                            "아이디와 패스워드에 공백을 사용할 수 없습니다.", Snackbar.LENGTH_SHORT).show();
                } else {
                    loginUser(stEmail, stPassword);
                }
            }
        });

        //회원 가입 버튼 리스너

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdCheck check = new IdCheck();
                stEmail = etEmail.getText().toString();
                stPassword = etPassword.getText().toString();
                if (check.idCheck(stEmail, stPassword) == false) {
                    Snackbar.make(v,
                            "아이디와 패스워드에 공백을 사용할 수 없습니다.", Snackbar.LENGTH_SHORT).show();
                } else {
                    registerUser(stEmail, stPassword);
                }
            }
        });
    }


    //회원 가입 메소드
    private void registerUser(String email, String password) {
        anime_layout.setVisibility(View.VISIBLE);
        pb.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Snackbar.make(getCurrentFocus(), "가입이 완료 되었습니다.",
                                    Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(getCurrentFocus(), "입력값을 확인해 주세요.",
                                    Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                            anime_layout.setVisibility(View.GONE);
                            pb.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    //로그인 메소드
    private void loginUser(String email, String password) {
        pb.setVisibility(View.VISIBLE);
        anime_layout.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Snackbar.make(getCurrentFocus(), "로그인 되었습니다.",
                                    Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            pb.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            pb.setVisibility(View.INVISIBLE);
                            anime_layout.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar.make(getCurrentFocus(), "입력값을 다시 확인하세요",
                                    Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    // 구글 로그인 연동 시작****************
    //FirebaseAuth.AuthStateListener 구현
    private void authStateL() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
//        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_WebApiKey))
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


    //구글 Sign 인텐트 호출
    private void signInGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
    //Result값 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("GoogleLogin", "personName=" + requestCode + " " + resultCode);

//        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("GoogleLogin", "personName=" + result);
            if (result.isSuccess()) {
                // 로그인 성공 했을때
                GoogleSignInAccount acct = result.getSignInAccount();

                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                String tokenKey = acct.getIdToken();

//                mGoogleApiClient.disconnect();

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
        } else {
        }
    }
}


