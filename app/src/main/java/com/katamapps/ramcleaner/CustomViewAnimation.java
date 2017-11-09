package com.katamapps.ramcleaner;

import android.view.animation.Animation;
import android.view.animation.Transformation;


/**
 * Created by Sridhar on 5/23/2017.
 */

public class CustomViewAnimation extends Animation
{
    private CustomViewClass customViewClass;
    private float oldAngle;
    private float newAngle;

    public CustomViewAnimation(CustomViewClass customViewClass0, int newAngle)
    {
        customViewClass=customViewClass0;
        this.oldAngle = customViewClass.getAngle();
        this.newAngle = newAngle;

    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        super.applyTransformation(interpolatedTime, t);
        float angle =  (oldAngle + ((newAngle - oldAngle) * interpolatedTime));
        customViewClass.setAngle(angle);
        customViewClass.requestLayout();
    }
}
