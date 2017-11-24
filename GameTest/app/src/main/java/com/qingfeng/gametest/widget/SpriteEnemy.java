package com.qingfeng.gametest.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.List;

/**
 * Created by WangQF on 2017/5/15 0015.
 */

public class SpriteEnemy extends SpriteVerticalMove {
    private int power = 1; //敌人的抗打能力
    private int value = 0; //打一个敌人获得分数

    public SpriteEnemy(Bitmap bitmap) {
        super(bitmap);
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
        super.afterDraw(canvas, paint, gameView);

        //绘制完后检查自身是否被子弹击中
        if (!isDestroyed()) {
            //敌机在绘制完成后要判断是否被子弹打中
            List<Bullet> bullets = gameView.getAliveBullets();
            for (Bullet bullet : bullets) {
                //判断敌机是否与子弹相交
                Point p = getCollidePointWithOther(bullet);
                if (p != null) {
                    //如果有交点说明子弹打到了飞机上
                    bullet.destroy();
                    power--;
                    if (power <= 0) {
                        //敌机已没有能量 销毁敌机
                        explode(gameView);
                        return;
                    }
                }
            }
        }
    }

    /**
     * 创建爆炸效果后销毁敌机
     */
    private void explode(GameView gameView) {
        //创建爆炸效果
        float centerX = getX() + getWidth() / 2;
        float centetY = getY() + getHeight() / 2;
        Bitmap bitmap = gameView.getExplosionBitmap();
        Explosion explosion = new Explosion(bitmap);
        explosion.centerTo(centerX, centetY);
        gameView.addSprite(explosion);

        //创建爆炸效果完成后，向GameView中添加得分并销毁敌机
        gameView.addScore(value);
        destroy();
    }

}
