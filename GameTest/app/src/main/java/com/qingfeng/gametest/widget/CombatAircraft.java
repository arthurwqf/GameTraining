package com.qingfeng.gametest.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.List;

/**
 * 战斗机类  可通过交互改变位置
 * Created by WangQF on 2017/5/27 0027.
 */

public class CombatAircraft extends Sprite {
    private boolean collide = false;//识别战斗机是否被击中
    private int bombAwardCount = 0;//可使用的炸弹数

    //双发子弹相关
    private boolean signle = true;//标识是否发的是单一的子弹
    private int doubleTime = 0; //当前已经使用双子弹绘制的次数
    private int maxDoubleTime = 140;//使用双子弹最多的绘制次数

    //被撞后闪烁相关
    private int beginFlushFrame = 0;//要在第beginFlushFrame帧开始闪烁战斗机
    private int flushTime = 0;//已经闪烁的次数
    private int flushFrequency = 16;//在闪烁的时候，每隔16帧改变战斗机的可见性
    private int maxFlushTime = 10; //最大的闪烁次数


    public CombatAircraft(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if (!isDestroyed()) {
            validatePosition(canvas);
        }
    }

    //确保战斗机类完全位于canvas范围内
    private void validatePosition(Canvas canvas) {
        if (getX() < 0) {
            setX(0);
        }
        if (getY() < 0) {
            setY(0);
        }
        RectF rectF = getRectF();
        int canvasWidth = canvas.getWidth();
        if (rectF.right > canvasWidth) {
            setX(canvasWidth - getWidth());
        }
        int canvasHeight = canvas.getHeight();
        if (rectF.bottom > canvasHeight) {
            setY(canvasHeight - getHeight());
        }
    }

    //发射子弹
    public void fight(GameView gameView) {
        if (collide || isDestroyed()) {
            //如果战斗机被撞或销毁了
            return;
        }

        float x = getX() + getWidth() / 2;
        float y = getY() - 5;
        if (signle) {
            //单发模式
            Bitmap yellowBulletBitmap = gameView.getYellowBulletBitmap();
            Bullet bullet = new Bullet(yellowBulletBitmap);
            bullet.moveTo(x, y);
            gameView.addSprite(bullet);
        } else {
            //双发模式
            float offset = getWidth() / 4;
            float leftX = x - offset;
            float rightX = x + offset;
            Bitmap blueBulletBitmap = gameView.getBlueBulletBitmap();

            Bullet leftBlueBullet = new Bullet(blueBulletBitmap);
            leftBlueBullet.moveTo(leftX, y);
            gameView.addSprite(leftBlueBullet);

            Bullet rightBlueBullet = new Bullet(blueBulletBitmap);
            rightBlueBullet.moveTo(rightX, y);
            gameView.addSprite(rightBlueBullet);

            doubleTime++;
            if (doubleTime >= maxDoubleTime) {
                signle = true;
                doubleTime = 0;
            }
        }
    }

    /**
     * @param canvas
     * @param paint
     * @param gameView
     */
    @Override
    public void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
        if (isDestroyed()) {
            return;
        }

        //在飞机当前还没有被击中时，要判断是否将要被敌机击中
        if (!collide) {
            List<SpriteEnemy> enemies = gameView.getAliveEnemyPlanes();
            for (SpriteEnemy enemy : enemies) {
                Point p = getCollidePointWithOther(enemy);
                if (p != null) {
                    explode(gameView);
                    break;
                }
            }
        }

        //beginFlushFrame 初始值为0，表示没有进入闪烁模式
        //如果beginFlushFrame 大于0，表示要在beginFlushFrame帧进入闪烁模式
        if (beginFlushFrame > 0) {
            long frame = getFrame();
            //如果当前帧数大于等于beginFlushFrame,才表示战斗机进入销毁前的闪烁状态
            if (frame >= beginFlushFrame) {
                if ((frame - beginFlushFrame) % flushFrequency == 0) {
                    boolean visible = getVisibility();
                    setVisibility(!visible);
                    flushTime++;
                    if (flushTime >= maxFlushTime) {
                        destroy();
                    }
                }
            }
        }

        //在没有被击中的情况下检查是否获得了道具
        if (!collide) {
            //检查是否获得炸弹道具
            List<BombAward> bombAwards = gameView.getAliveBombAwards();
            for (BombAward bombAward : bombAwards) {
                Point p = getCollidePointWithOther(bombAward);
                if (p != null) {
                    bombAwardCount++;
                    bombAward.destroy(); //销毁移动的奖励
                }
            }

            //检查是否获得子弹道具
            List<BulletAward> bulletAwards = gameView.getAliveBulletAwards();
            for (BulletAward bulletAward : bulletAwards) {
                Point p = getCollidePointWithOther(bulletAward);
                if (p != null) {
                    bulletAward.destroy();
                    signle = false;
                    doubleTime = 0;
                }
            }
        }
    }

    /**
     * 战斗机爆炸
     *
     * @param gameView
     */
    private void explode(GameView gameView) {
        if (!collide) {
            collide = true;
            setVisibility(false);
            float centerX = getX() + getWidth() / 2;
            float centerY = getY() + getHeight() / 2;
            Explosion explosion = new Explosion(gameView.getExplosionBitmap());
            explosion.centerTo(centerX, centerY);
            gameView.addSprite(explosion);
            beginFlushFrame = getFrame() + explosion.getExplodeDurationFrame();
        }
    }
}
