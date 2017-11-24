package com.qingfeng.gametest.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 精灵类  所有其他用于绘制类的基类
 * Created by WangQF on 2017/5/15 0015.
 */

public class Sprite {
    private Bitmap bitmap = null;
    private boolean visible = true;
    private float x = 0;
    private float y = 0;
    private float collideOffset = 0; // 碰撞偏移量
    private int frame = 0;//绘制的次数
    private boolean destroyed = false;

    public Sprite(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    public boolean getVisibility() {
        return this.visible;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        if (bitmap != null) {
            return bitmap.getWidth();
        }
        return 0;
    }

    public float getHeight() {
        if (bitmap != null) {
            return bitmap.getHeight();
        }
        return 0;
    }

    public void move(float offsetX, float offsetY) {
        this.x += offsetX;
        this.y += offsetY;
    }

    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void centerTo(float centerX, float centerY) {
        float w = getWidth();
        float h = getHeight();
        x = centerX - w / 2;
        y = centerY - h / 2;
    }

    public RectF getRectF() {
        float left = x;
        float top = y;
        float right = left + getWidth();
        float bottom = top + getHeight();
        return new RectF(left, top, right, bottom);
    }

    public Rect getBitmapSrcRect() {
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = (int) getWidth();
        rect.bottom = (int) getHeight();
        return rect;
    }

    /**
     * 获取碰撞的矩形   collideOffset 为碰撞深度 接触collideOffset就算碰到了
     *
     * @return
     */
    public RectF getCollideRectF() {
        RectF rectF = new RectF();
        rectF.left -= collideOffset;
        rectF.top -= collideOffset;
        rectF.right += collideOffset;
        rectF.bottom += collideOffset;
        return rectF;
    }

    /**
     * 与其他sprite 的碰撞点
     *
     * @param s
     * @return
     */
    public Point getCollidePointWithOther(Sprite s) {
        Point point = null;
        RectF rectF1 = getCollideRectF();
        RectF rectF2 = s.getCollideRectF();
        RectF rectF = new RectF();
        boolean isIntersect = rectF.setIntersect(rectF1, rectF2);
        if (isIntersect) {
            point = new Point(Math.round(rectF.centerX()), Math.round(rectF.centerY()));
        }
        return point;
    }

    public final void draw(Canvas canvas, Paint paint, GameView gameView) {
        frame++;
        beforeDraw(canvas, paint, gameView);
        onDraw(canvas, paint, gameView);
        afterDraw(canvas, paint, gameView);
    }

    public void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
    }

    public void onDraw(Canvas canvas, Paint paint, GameView gameView) {
        if (!destroyed && this.bitmap != null && getVisibility()) {
            //将sprite 绘制到canvas上
            Rect srcRect = getBitmapSrcRect();
            RectF dstRectf = getRectF();
            canvas.drawBitmap(bitmap, srcRect, dstRectf, paint);
        }
    }

    public void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
    }

    public void destroy() {
        bitmap = null;
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getFrame() {
        return frame;
    }

}
