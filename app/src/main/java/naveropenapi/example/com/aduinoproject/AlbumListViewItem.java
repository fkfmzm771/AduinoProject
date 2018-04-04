package naveropenapi.example.com.aduinoproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by MIT-18 on 2016-09-12.
 */
public class AlbumListViewItem {

    private Bitmap iconBitmap;
    private String titleStr;
    private String descStr;


    public void setIcon(String icon){
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 4;
//        iconBitmap = BitmapFactory.decodeFile(icon,options);
        iconBitmap = BitmapFactory.decodeFile(icon);
    }
    public Bitmap getIcon(){
        return this.iconBitmap;
    }

    public void setTitle(String title){

        titleStr=title;
    }
    public String getTitle(){

        return this.titleStr;
    }


    public void setDesc(String desc){

        descStr=desc;
    }
    public String getDesc(){
        return this.descStr;

    }
}
