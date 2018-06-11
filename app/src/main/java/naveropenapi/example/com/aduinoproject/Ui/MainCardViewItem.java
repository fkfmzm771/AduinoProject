package naveropenapi.example.com.aduinoproject.Ui;

public class MainCardViewItem {

    private int image;
    private String titleStr;


    public MainCardViewItem(int image_id, String title){
        this.image = image_id;
        this.titleStr = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitleStr() {
        return titleStr;
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }


}
