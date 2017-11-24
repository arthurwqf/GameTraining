package com.qingfeng.gametest.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 奖品
 * Created by WangQF on 2017/5/15 0015.
 */

public class SpriteAward extends SpriteVerticalMove {
    private static int STATUS_DOWN1 = 1;
    private static int STATUS_UP2 = 2;
    private static int STATUS_DOWN3 = 3;
    private int status = STATUS_DOWN1;

    public SpriteAward(Bitmap bitmap) {
        super(bitmap);
        setSpeed(7);
    }

    @Override
    public void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
        if (!isDestroyed()) {
            //在绘制一定次数后，要改变方向和速度
            int canvasHeight = canvas.getHeight();
            if (status != STATUS_DOWN3) {
                float maxY = getY() + getHeight();
                //第一次向下
                if (status == STATUS_DOWN1) {
                    if (maxY >= canvasHeight * 0.25) {
                        //当第一次下降到临界值时，改变方向
                        setSpeed(-5);
                        status = STATUS_UP2;
                    }
                } else if (status == STATUS_UP2) {
                    //第二次向上
                    if (maxY + this.getSpeed() <= 0) {
                        //第二次上升到临界值时改变方向，向下
                        setSpeed(13);
                        status = STATUS_DOWN3;
                    }
                }
            } else {
                if (getY() >= canvasHeight) {
                    destroy();
                }
            }
        }
    }
}
