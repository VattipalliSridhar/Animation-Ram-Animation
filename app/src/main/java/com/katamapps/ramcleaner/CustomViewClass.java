package com.katamapps.ramcleaner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by Sridhar on 5/23/2017.
 */

public class CustomViewClass extends View
{
    private int canvas_width,canvas_height;
    private Paint arc1_paint,arc2_paint,txt_paint;
    private RectF oval1 = new RectF();
    private RectF oval2 = new RectF();
    private float sweepAngle;
    private int size;
    private String txt_color;
    private Bitmap view_img;



    public CustomViewClass(Context context, String s, Bitmap bitmap)
    {
        super(context);
        arc1_paint=new Paint();
        arc1_paint.setColor(Color.parseColor("#d0d0d0"));
        arc1_paint.setStyle(Paint.Style.STROKE);
        arc1_paint.setStrokeCap(Paint.Cap.ROUND);
        arc1_paint.setDither(true);
        arc1_paint.setAntiAlias(true);

        arc2_paint=new Paint();
        arc2_paint.setColor(Color.parseColor(s));
        arc2_paint.setAntiAlias(true);
        arc1_paint.setDither(true);
        arc2_paint.setStrokeCap(Paint.Cap.ROUND);
        arc2_paint.setStyle(Paint.Style.STROKE);

        view_img=bitmap;


        txt_color=s;

        size=20;

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        canvas_width=w;
        canvas_height=h;

        view_img=Bitmap.createScaledBitmap(view_img,canvas_width/6,canvas_width/6,true);

        arc1_paint.setStrokeWidth(canvas_width/32);
        arc2_paint.setStrokeWidth(canvas_width/32);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        oval1.set(canvas_width/size, canvas_width/size, canvas_height-canvas_width/size, canvas_width-canvas_width/size);
        canvas.drawArc(oval1, 90,360, false, arc1_paint);

        oval2.set(canvas_width/size, canvas_width/size, canvas_height-canvas_width/size, canvas_width-canvas_width/size);
        canvas.drawArc(oval2, 90,sweepAngle, false, arc2_paint);

        txt_paint=new Paint();
        txt_paint.setColor(Color.parseColor(txt_color));
        txt_paint.setAntiAlias(true);
        txt_paint.setDither(true);
        txt_paint.setTextSize(canvas_width/6);
        txt_paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawBitmap(view_img,canvas_width/2-canvas_width/12, canvas_height/2+canvas_height/10,null);


        canvas.drawText(""+((int)((sweepAngle/360)*100))+"%", canvas_width/2+canvas_width/40, canvas_height/2+canvas_height/30, txt_paint);

    }

    public float getAngle()
    {
        return sweepAngle;
    }

    public void setAngle(float angle)
    {
        this.sweepAngle =  angle;
    }
}
