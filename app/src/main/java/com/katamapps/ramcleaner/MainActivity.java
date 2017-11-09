package com.katamapps.ramcleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Sridhar on 5/23/2017.
 */

public class MainActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    private int screenWidth,screenHeight;

    private RelativeLayout storage_layout,ram_layout,internal_storage,
            external_storage,internal_view,external_view,ram_view;

    private CustomViewClass customViewClass0,customViewClass1,customViewClass2;
    private CustomViewAnimation customViewAnimation0,customViewAnimation1,customViewAnimation2;


    private TextView external_view_text,internal_view_text;
    private String internalStoragePath = null;
    private String externalStoragePath = null;
    private ArraySet<String> readPossibleExternalStoragePaths;
    private Context context;
    private static final String TAG = "MainActivity";
    private ImageView external_view_img;
    private TextView external_total_txt,external_use_txt,external_free_txt;
    private TextView internal_total_txt,internal_use_txt,internal_free_txt;
    private TextView ram_txt;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {

        }
        else
        {
            init();
        }


    }

    private void init()
    {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;






        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""+getString(R.string.app_name));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout full_layout = (RelativeLayout)findViewById(R.id.full_layout);
        full_layout.getLayoutParams().width = screenWidth;
        full_layout.getLayoutParams().height = screenHeight - ((int) px + getStatusBarHeight());

        Log.e("msg","hight: "+screenHeight);

        storage_layout=(RelativeLayout)findViewById(R.id.storage_layout);
        storage_layout.getLayoutParams().height=(((screenHeight - ((int) px + getStatusBarHeight()))*50)/100);

        ram_layout=(RelativeLayout)findViewById(R.id.ram_layout);
        ram_layout.getLayoutParams().height=(((screenHeight - ((int) px + getStatusBarHeight()))*50)/100);

        internal_storage=(RelativeLayout)findViewById(R.id.internal_storage);
        internal_storage.getLayoutParams().width=screenWidth/2;
        internal_storage.getLayoutParams().height=(((screenHeight - ((int) px + getStatusBarHeight()))*50)/100);

        external_storage=(RelativeLayout)findViewById(R.id.external_storage);
        external_storage.getLayoutParams().width=screenWidth/2;
        external_storage.getLayoutParams().height=(((screenHeight - ((int) px + getStatusBarHeight()))*50)/100);

        internal_view=(RelativeLayout)findViewById(R.id.internal_view);
        internal_view.getLayoutParams().height = screenHeight/4 - (screenHeight * 2)/100;
        internal_view.getLayoutParams().width = screenHeight/4 - (screenHeight * 2)/100;
        ViewGroup.MarginLayoutParams  layoutParams=(ViewGroup.MarginLayoutParams)internal_view.getLayoutParams();
        layoutParams.topMargin=(screenHeight*8)/100;


        external_view=(RelativeLayout)findViewById(R.id.external_view);
        external_view.getLayoutParams().height = screenHeight/4 - (screenHeight * 2)/100;
        external_view.getLayoutParams().width = screenHeight/4 - (screenHeight * 2)/100;
        ViewGroup.MarginLayoutParams  layoutParams1=(ViewGroup.MarginLayoutParams)external_view.getLayoutParams();
        layoutParams1.topMargin=(screenHeight*8)/100;





        ram_view=(RelativeLayout)findViewById(R.id.ram_view);
        ram_view.getLayoutParams().height = screenHeight/2 - (screenHeight * 20)/100;
        ram_view.getLayoutParams().width = screenHeight/2 - (screenHeight * 20)/100;


        external_total_txt=(TextView)findViewById(R.id.external_total_txt);
        external_free_txt=(TextView)findViewById(R.id.external_free_txt);
        external_use_txt=(TextView)findViewById(R.id.external_use_txt);

        internal_total_txt=(TextView)findViewById(R.id.internal_total_txt);
        internal_free_txt=(TextView)findViewById(R.id.internal_free_txt);
        internal_use_txt=(TextView)findViewById(R.id.internal_use_txt);









        external_view_img=(ImageView)findViewById(R.id.external_view_img);
        external_view_img.getLayoutParams().height=screenWidth/10;
        external_view_img.getLayoutParams().width=screenWidth/10;
        ViewGroup.MarginLayoutParams  external_view_img_layoutParams=(ViewGroup.MarginLayoutParams)external_view_img.getLayoutParams();
        external_view_img_layoutParams.bottomMargin=(screenHeight*4)/100;



        ram_txt=(TextView)findViewById(R.id.ram_txt);


        ViewGroup.MarginLayoutParams  ram_txt_layoutParams=(ViewGroup.MarginLayoutParams)ram_txt.getLayoutParams();
        ram_txt_layoutParams.bottomMargin=(screenHeight*2)/100;


        ram_status();
        external_status();
        internal_status();
        readPossibleExternalStoragePaths();

    }

    private void readPossibleExternalStoragePaths()
    {

        if(readPossibleExternalStoragePaths != null)
        {
            for (int i = 0; i < readPossibleExternalStoragePaths.size(); i++)
            {
                if (internalStoragePath.equals(readPossibleExternalStoragePaths.valueAt(i))) {

                } else {
                    externalStoragePath = readPossibleExternalStoragePaths.valueAt(i);
                }
            }
        }
    }


    private void external_status()
    {
        externalStoragePath = getExternalStoragePath();


        if(externalStoragePath != null)
        {
            File externalMemoryFile = new File(externalStoragePath);
            final long externalMemoryTotalSpace = externalMemoryFile.getTotalSpace();
            final long externalMemoryFreeSpace = externalMemoryFile.getFreeSpace();
            final long externalMemoryUsedSpace = (externalMemoryTotalSpace - externalMemoryFreeSpace);
            Utils.external_storage_value=(int)((externalMemoryUsedSpace * 100)/externalMemoryTotalSpace);

            bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_micro_sd_card);

            customViewClass1=new CustomViewClass(MainActivity.this,"#4B0082", bitmap);
            external_view.removeAllViews();
            external_view.addView(customViewClass1);


            customViewAnimation1=new CustomViewAnimation(customViewClass1, (int) ((Utils.external_storage_value/100)*360));
            customViewAnimation1.setDuration(1000);
            customViewClass1.startAnimation(customViewAnimation1);


            external_total_txt.setText("  TotalSpace "+   Formatter.formatFileSize(context,externalMemoryTotalSpace));
            external_free_txt.setText("   FreeSpace "+   Formatter.formatFileSize(context,externalMemoryFreeSpace));
            external_use_txt.setText("  UsedSpace "+   Formatter.formatFileSize(context,externalMemoryUsedSpace));
        }
        else
        {
            external_view_text=(TextView)findViewById(R.id.external_view_text);
            external_view_text.setVisibility(View.VISIBLE);
            external_view_text.setText("Sd card not exits");

            external_total_txt.setVisibility(View.GONE);
            external_free_txt.setVisibility(View.GONE);
            external_use_txt.setVisibility(View.GONE);
        }


    }

    private void internal_status()
    {
        internalStoragePath = getInternalStoragePath();


        if(internalStoragePath != null)
        {
            File internalMemoryFile = new File(internalStoragePath);
            final long internalMemoryTotalSpace = internalMemoryFile.getTotalSpace();
            final long internalMemoryFreeSpace  = internalMemoryFile.getFreeSpace();
            final long internalMemoryUsedSpace  = (internalMemoryTotalSpace - internalMemoryFreeSpace);
            Utils.internal_storage_value=(int)((internalMemoryUsedSpace * 100)/internalMemoryTotalSpace);
            Log.i(TAG,"internalMemoryUsedSpace =  "+   ((internalMemoryUsedSpace * 100)/internalMemoryTotalSpace));
            bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_internal_storage);

            customViewClass0=new CustomViewClass(MainActivity.this,"#9400D3",bitmap);
            internal_view.removeAllViews();
            internal_view.addView(customViewClass0);

            customViewAnimation0=new CustomViewAnimation(customViewClass0, (int) ((Utils.internal_storage_value/100)*360));
            customViewAnimation0.setDuration(1000);
            customViewClass0.startAnimation(customViewAnimation0);

            internal_total_txt.setText("  TotalSpace "+   Formatter.formatFileSize(context,internalMemoryTotalSpace));
            internal_free_txt.setText("   FreeSpace "+   Formatter.formatFileSize(context,internalMemoryFreeSpace));
            internal_use_txt.setText("  UsedSpace "+   Formatter.formatFileSize(context,internalMemoryUsedSpace));
        }
        else
        {
            internal_view_text=(TextView)findViewById(R.id.internal_view_text);
            internal_view_text.setVisibility(View.VISIBLE);
            internal_view_text.setText("internal not exits");

            internal_total_txt.setVisibility(View.GONE);
            internal_free_txt.setVisibility(View.GONE);
            internal_use_txt.setVisibility(View.GONE);
        }

    }


    public int getStatusBarHeight()
    {
        int result = 0;
        try
        {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0)
            {
                result = getResources().getDimensionPixelSize(resourceId);
            }
        }
        catch (Exception e)
        {

        }
        return result;
    }



    private String getExternalStoragePath()
    {
        String externalStoragePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            File[] files = context.getExternalFilesDirs(null);

            if(files != null && files.length >= 2)
            {
                if(files[1] != null)
                {
                    externalStoragePath = files[1].getAbsolutePath();
                    String[] split = externalStoragePath.split("/Android/data/"+getPackageName()+"/files");
                    for (String s:split)
                    {
                        externalStoragePath = s;
                    }
                }
            }
        }
        else
        {
            readPossibleExternalStoragePaths = readProcMountsFile();
        }
        return externalStoragePath;
    }


    private String getInternalStoragePath()
    {
        String internalStoragePath = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            File[] files = context.getExternalFilesDirs(null);

            if(files != null)
            {
                internalStoragePath = files[0].getAbsolutePath();
                String[] split = internalStoragePath.split("/Android/data/"+getPackageName()+"/files");
                for (String s:split)
                {
                    internalStoragePath = s;
                }
            }
        }
        else
        {
            internalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return internalStoragePath;
    }




    private android.support.v4.util.ArraySet<String> readProcMountsFile()
    {
        RandomAccessFile randomAccessFile = null;
        StringBuilder stringBuilder = new StringBuilder();
        android.support.v4.util.ArraySet<String> pathsArraySet = new android.support.v4.util.ArraySet<>();
        pathsArraySet.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        try
        {
            randomAccessFile = new RandomAccessFile("/proc/mounts","r");
            String line;

            while ((line = randomAccessFile.readLine()) != null)
            {
                if(line.contains("vfat") || line.contains("/mnt"))
                {
                    if(line.contains("/dev/block/vold/"))
                    {
                        if(!line.contains("/mnt/secure")
                                && !line.contains("/mnt/asec")
                                && !line.contains("/mnt/obb")
                                && !line.contains("/dev/mapper")
                                && !line.contains("tmpfs"))
                        {
                            stringBuilder.append(line).append("\n");
                            StringTokenizer stringTokenizer = new StringTokenizer(line," ");
                            stringTokenizer.nextToken();    // First Token
                            String secondToken = stringTokenizer.nextToken();
                            pathsArraySet.add(secondToken);
                        }
                    }
                }
            }

            return pathsArraySet;
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG,"FileNotFoundException \n"+e.getMessage());
        } catch (IOException e)
        {
            Log.e(TAG,"IOException \n"+e.getMessage());
        }
        finally
        {
            try
            {
                if (randomAccessFile != null)
                {
                    randomAccessFile.close();
                }
            }
            catch (IOException e)
            {
                Log.e(TAG,"IOException while closing RandomAccessFile \n"+e.getMessage());
            }
        }

        return null;
    }


    private void ram_status()
    {
        double memoryUsed = getDeviceRAMInformation();
        Utils.optimiz_ram_value = (int)memoryUsed;

        bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_ram);

        customViewClass2=new CustomViewClass(MainActivity.this,"#0000FF", bitmap);
        ram_view.removeAllViews();
        ram_view.addView(customViewClass2);

        customViewAnimation2=new CustomViewAnimation(customViewClass2,(int) ((Utils.optimiz_ram_value/100)*360));
        customViewAnimation2.setDuration(1000);
        customViewClass2.startAnimation(customViewAnimation2);

    }
    private double getDeviceRAMInformation()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        double  availableMemory          = memoryInfo.availMem/(Math.pow(1024,2)),
                memoryUsed               = 0.0d,
                memoryUsedInPercent      = 0.0d,
                totalRAMInKB             = 0.0d,
                totalRAMInMB             = 0.0d;


        DecimalFormat twoDigitsAfterDecimalPoint = new DecimalFormat("#.##");
        RandomAccessFile randomAccessFile = null;
        try
        {
            randomAccessFile = new RandomAccessFile("/proc/meminfo","r");
            String line = randomAccessFile.readLine();
            String value = getOnlyDigitsFromString(line);

            totalRAMInKB = Double.parseDouble(value);
            totalRAMInMB = totalRAMInKB/1024;
        }
        catch (FileNotFoundException e)
        {
        } catch (IOException e)
        {
        }
        finally
        {
            try
            {
                if (randomAccessFile != null)
                {
                    randomAccessFile.close();
                }
            }
            catch (IOException e)
            {

            }
        }




        memoryUsed                  = totalRAMInMB - availableMemory;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            long totalRam = memoryInfo.totalMem;
            long available_Ram = memoryInfo.availMem;
            ram_txt.setText("Ram Usage "+Formatter.formatFileSize(context, (totalRam - available_Ram))+"/"+Formatter.formatFileSize(context,  totalRam));
        }
        else
        {
            ram_txt.setText("Ram Usage "+Formatter.formatFileSize(context, (long) memoryUsed)+"/"+Formatter.formatFileSize(context, (long) totalRAMInMB));
        }

        memoryUsedInPercent         = (memoryUsed*100)/totalRAMInMB;

        return memoryUsedInPercent;
    }
    private String getOnlyDigitsFromString(String string)
    {
        Pattern pattern = Pattern.compile("(\\d+)");    // regularexpression to find only digits
        Matcher matcher = pattern.matcher(string);
        String value = null;

        while (matcher.find())
        {
            value = matcher.group(1);
        }

        return value;
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        ram_status();
        external_status();
        internal_status();
        readPossibleExternalStoragePaths();
    }
}
