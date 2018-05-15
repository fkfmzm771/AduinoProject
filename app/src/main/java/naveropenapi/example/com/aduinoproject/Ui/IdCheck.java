package naveropenapi.example.com.aduinoproject.Ui;


public class IdCheck {

    public boolean idCheck(String id, String pass) {
        if (id.trim().length() == 0 && pass.trim().length() == 0) {
            return false;
        } else
            return true;
    }



}
