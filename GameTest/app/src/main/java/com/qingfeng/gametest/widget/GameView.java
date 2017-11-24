package com.qingfeng.gametest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.qingfeng.gametest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangQF on 2017/5/15 0015.
 */

public class GameView extends View {
    public static final int STATUS_GAME_STARTED = 1;//游戏开始
    public static final int STATUS_GAME_PAUSED = 2;//游戏暂停
    public static final int STATUS_GAME_OVER = 3;//游戏结束
    public static final int STATUS_GAME_DESTROYED = 4;//游戏销毁
    private int status = STATUS_GAME_DESTROYED;//初始为销毁状态

    private float density = getResources().getDisplayMetrics().density;//屏幕密度
    private List<Sprite> sprites = new ArrayList<>(); //所有的sprite
    private List<Sprite> spritesNeedAdded = new ArrayList<>(); //需要添加的sprite
    //0:combatAircraft
    //1:explosion
    //2:yellowBullet
    //3:blueBullet
    //4:smallEnemyPlane
    //5:middleEnemyPlane
    //6:bigEnemyPlane
    //7:bombAward
    //8:bulletAward
    //9:pause1
    //10:pause2
    //11:bomb
    private List<Bitmap> bitmaps = new ArrayList<>(); //所有的bitmap
    private long frame = 0;//总共绘制的帧数
    private long score = 0;//总得分
    private Paint mPaint, mTextPaint;
    private float fontSize = 12;//默认的字体大小，用于绘制左上角的文本
    private float fontSize2 = 20;//用于在Game Over的时候绘制Dialog中的文本
    private float borderSize = 2;//Game Over的Dialog的边框
    private CombatAircraft combatAircraft = null;
    private Rect continueRect = new Rect();//"继续"、"重新开始"按钮的Rect

    //触摸事件相关的变量
    private static final int TOUCH_MOVE = 1;//移动
    private static final int TOUCH_SINGLE_CLICK = 2;//单击
    private static final int TOUCH_DOUBLE_CLICK = 3;//双击
    //一次单击事件由DOWN和UP两个事件合成，假设从down到up间隔小于200毫秒，我们就认为发生了一次单击事件
    private static final int singleClickDurationTime = 200;
    //一次双击事件由两个点击事件合成，两个单击事件之间小于300毫秒，我们就认为发生了一次双击事件
    private static final int doubleClickDurationTime = 300;
    private long lastSingleClickTime = -1;//上次发生单击的时刻
    private long touchDownTime = -1;//触点按下的时刻
    private long touchUpTime = -1;//触点弹起的时刻
    private float touchX = -1;//触点的x坐标
    private float touchY = -1;//触点的y坐标

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GameView, defStyle, 0);
        a.recycle();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        mTextPaint.setColor(0xff000000);
        fontSize = mTextPaint.getTextSize();
        fontSize *= density;
        fontSize2 *= density;
        mTextPaint.setTextSize(fontSize);
        borderSize *= density;
    }

    public void start(int[] bitmapIds) {
        destroy();
        for (int bitmapId : bitmapIds) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapId);
            bitmaps.add(bitmap);
        }
        startWhenBitmapsReady();
    }

    private void startWhenBitmapsReady() {
        combatAircraft = new CombatAircraft(bitmaps.get(0));
        //将游戏设置为开始状态
        status = STATUS_GAME_STARTED;
        postInvalidate();
    }

    private void restart() {
        destroyNotRecycleBitmaps();
        startWhenBitmapsReady();
    }

    public void pause() {
        //将游戏设置为暂停状态
        status = STATUS_GAME_PAUSED;
    }

    private void resume() {
        //将游戏设置为运行状态
        status = STATUS_GAME_STARTED;
        postInvalidate();
    }

    private long getScore() {
        return score;
    }

    /*-------------------------draw------------------------------------*/

    @Override
    protected void onDraw(Canvas canvas) {
        //我们在每一帧都检测是否满足延迟触发单击事件的条件
        if (isSingleClick()) {
            onSingleClick(touchX, touchY);
        }

        if (status == STATUS_GAME_STARTED) {

        }
    }

    private void drawGameStarted(Canvas canvas) {

    }

    //绘制左上角的得分和左下角的炸弹数量
    private void drawScoreAndBombs(Canvas canvas) {
        //绘制右上角的暂停按钮
        Bitmap pauseBitmap = status == STATUS_GAME_STARTED ? bitmaps.get(9) : bitmaps.get(10);
        RectF pauseBitmapDstRestF = getPauseBitmapDstRecf();
        float pauseLeft = pauseBitmapDstRestF.left;
        float pauseTop = pauseBitmapDstRestF.top;
        canvas.drawBitmap(pauseBitmap, pauseLeft, pauseTop, mPaint);

        //绘制左上角的得分数
        float scoreLeft = pauseLeft + pauseBitmap.getWidth() + 20 * density;
        float scoreTop = fontSize + pauseTop + pauseBitmap.getHeight() / 2 - fontSize / 2;
        canvas.drawText(score + "", scoreLeft, scoreTop, mTextPaint);

        //绘制左下角
        if (combatAircraft != null && !combatAircraft.isDestroyed()){

        }
    }

    /*-------------------------touch-------------------------------------*/
    private boolean isSingleClick() {
        boolean singleClick = false;
        //我们检查一下是不是上次的单击事件在经过了doubleClickDurationTime毫秒后满足触发单击事件的条件
        if (lastSingleClickTime > 0) {
            //计算当前时刻距离上次发生单击事件的时间差
            long deltaTime = System.currentTimeMillis() - lastSingleClickTime;

            if (deltaTime >= doubleClickDurationTime) {
                //如果时间差超过了一次双击事件所需要的时间差，
                //那么就在此刻延迟触发之前本该发生的单击事件
                singleClick = true;
                //重置变量
                lastSingleClickTime = -1;
                touchDownTime = -1;
                touchUpTime = -1;
            }
        }
        return singleClick;
    }

    private void onSingleClick(float x, float y) {
        if (status == STATUS_GAME_STARTED) {
            if (isClickPause(x, y)) {
                //单击了暂停
                pause();
            }
        } else if (status == STATUS_GAME_PAUSED) {
            if (isClickContinueButton(x, y)) {
                //单击了继续
                resume();
            }
        } else if (status == STATUS_GAME_OVER) {
            if (isClickRestartButton(x, y)) {
                //单击了重新开始
                restart();
            }
        }
    }

    private boolean isClickPause(float x, float y) {
        RectF rectF = getPauseBitmapDstRecf();
        return rectF.contains(x, y);
    }

    private RectF getPauseBitmapDstRecf() {
        Bitmap pauseBitmap = status == STATUS_GAME_STARTED ? bitmaps.get(9) : bitmaps.get(10);
        RectF rectF = new RectF();
        rectF.left = 15 * density;
        rectF.top = 15 * density;
        rectF.right = rectF.left + pauseBitmap.getWidth();
        rectF.bottom = rectF.top + pauseBitmap.getHeight();
        return rectF;
    }

    private boolean isClickContinueButton(float x, float y) {
        return continueRect.contains((int) x, (int) y);
    }

    //是否点击了GAME OVER状态下的 重新开始 按钮
    private boolean isClickRestartButton(float x, float y) {
        return continueRect.contains((int) x, (int) y);
    }

    public float getDensity() {
        return density;
    }

    public Bitmap getExplosionBitmap() {
        if (bitmaps != null)
            return bitmaps.get(1);
        else {
            return null;
        }
    }

    public Bitmap getYellowBulletBitmap() {
        return bitmaps.get(2);
    }

    public Bitmap getBlueBulletBitmap(){
        return bitmaps.get(3);
    }

    public void addSprite(Sprite sprite) {
        this.spritesNeedAdded.add(sprite);
    }

    public void addScore(int value) {
        this.score += value;
    }

    //获取处于活动状态的敌机
    public List<SpriteEnemy> getAliveEnemyPlanes(){
        List<SpriteEnemy> enemyPlanes = new ArrayList<>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof SpriteEnemy){
                SpriteEnemy sprite = (SpriteEnemy)s;
                enemyPlanes.add(sprite);
            }
        }
        return enemyPlanes;
    }

    /**
     * 获取处于激活状态的奖励
     *
     * @return
     */
    public List<BulletAward> getAliveBulletAwards() {
        List<BulletAward> bulletAwards = new ArrayList<>();
        for (Sprite s : sprites) {
            if (!s.isDestroyed() && s instanceof BulletAward) {
                bulletAwards.add((BulletAward) s);
            }
        }
        return bulletAwards;
    }

    //获得处于活动状态的炸弹奖励
    public List<BombAward> getAliveBombAwards(){
        List<BombAward> bombAwards = new ArrayList<BombAward>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof BombAward){
                BombAward bombAward = (BombAward)s;
                bombAwards.add(bombAward);
            }
        }
        return bombAwards;
    }

    /**
     * 获取处于活动状态的子弹
     *
     * @return
     */
    public List<Bullet> getAliveBullets() {
        List<Bullet> bullets = new ArrayList<>();
        for (Sprite sprite : sprites) {
            if (!sprite.isDestroyed() && sprite instanceof Bullet) {
                Bullet bullet = (Bullet) sprite;
                bullets.add(bullet);
            }
        }
        return bullets;
    }

    public void destroy() {
        destroyNotRecycleBitmaps();

        //释放bitmap资源
        for (Bitmap bitmap : bitmaps) {
            bitmap.recycle();
        }
        bitmaps.clear();
    }

    private void destroyNotRecycleBitmaps() {
        //将游戏置位销毁状态
        status = STATUS_GAME_DESTROYED;

        //重置frame
        frame = 0;
        //重置得分
        score = 0;

        //销毁战斗机
        if (combatAircraft != null) {
            combatAircraft.destroy();
        }
        combatAircraft = null;

        //销毁敌机、子弹、奖励、爆炸
        for (Sprite s : sprites) {
            s.destroy();
        }
        sprites.clear();
    }
}
