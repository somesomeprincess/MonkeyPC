package hippo.com.xuhangtest;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilDemo {
//
//    public void writeFile(String fileName,String writestr) {
//        int REQUEST_EXTERNAL_STORAGE =1;
//        String[] PERMISSIONS_STORAGE={
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        };
//        int permission= ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        String TAG="File--";
//        File file = null;
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            File sdcardDir = Environment.getExternalStorageDirectory();
//            file = new File(sdcardDir,fileName);
//            Log.v(TAG,file.getAbsolutePath());
//            if(!file.exists()) {
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        else{
//            Log.v(TAG,"no SD Card dir");
//            return;
//        }
//
//        try {
//            //FileOutputStream fout = openFileOutput("fi",MODE_APPEND);
//            FileOutputStream fout = new FileOutputStream(file,true);
//            byte [] bytes = writestr.getBytes();
//            fout.write(bytes);
//            fout.close();
//            Log.v(TAG,"Already write!!!!!!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.v(TAG,e.toString());
//        }
//
//    }

    public void helloworld(){
        Log.v("helloworld","helloworld");
    }
}
