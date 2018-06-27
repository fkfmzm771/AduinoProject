package naveropenapi.example.com.aduinoproject.FireBase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;

public class FireBaseDB {
    private FirebaseDatabase database = null;
    private DatabaseReference myRef = null;
    private FirebaseUser user = null;
    private FirebaseAuth mAuth = null;

    private Hashtable<String, String> inputData;


    public FireBaseDB(){

        this.database = FirebaseDatabase.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.mAuth = FirebaseAuth.getInstance();

        inputData = new Hashtable<>();
    }

    //레퍼런스 값 추가 메서드
    public void input_Ref(DatabaseReference ref)  {
        this.myRef = ref;
    }

    public void set_Refkey(String key, String data){
        //데이터베이스에 넣을 헤쉬테이블값을 생성
        inputData.put(key, data);
    }

    public void set_ref_hash(){
        myRef.setValue(inputData);
        inputData.clear();
    }



    public FirebaseDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setAuth(FirebaseAuth auth) {
        mAuth = auth;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
}
