package com.qingfeng.gametest.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 垂直移动的 sprite
 * Created by WangQF on 2017/5/15 0015.
 */

public class SpriteVerticalMove extends Sprite {
    private float speed = 2;

    public SpriteVerticalMove(Bitmap bitmap) {
        super(bitmap);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if (!isDestroyed()) {
            //在y轴方向移动speed像素
            move(0, speed * gameView.getDensity());
        }
    }

    @Override
    public void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
        if (!isDestroyed()) {
            //检查sprite 是否超出了Canvas 的范围，如果超出，则销毁sprite
            RectF canvasRectF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            RectF spriteRectF = getRectF();
            if (!RectF.intersects(canvasRectF, spriteRectF)) {
                destroy();
            }
        }
    }
}
