package com.qingfeng.gametest.widget;

import android.graphics.Bitmap;

/**
 * Created by WangQF on 2017/5/15 0015.
 */

public class Bullet extends SpriteVerticalMove {
    public Bullet(Bitmap bitmap) {
        super(bitmap);
        setSpeed(-10); //负数表示子弹向上飞
    }
}
