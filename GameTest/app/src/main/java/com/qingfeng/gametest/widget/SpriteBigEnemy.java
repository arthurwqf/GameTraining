package com.qingfeng.gametest.widget;

import android.graphics.Bitmap;

/**
 * Created by WangQF on 2017/5/27 0027.
 */

public class SpriteBigEnemy extends SpriteEnemy {

    public SpriteBigEnemy(Bitmap bitmap) {
        super(bitmap);
        setPower(10);//大敌机抗抵抗能力为10，即需要10颗子弹才能销毁大敌机
        setValue(30000);//销毁一个大敌机可以得30000分
    }
}
