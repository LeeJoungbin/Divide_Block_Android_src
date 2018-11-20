package com.pnu.cse.termspring2018;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final int sGridBackgroundColor        = 0XFFE0E000;
    private static final int sGridResponseBackgroundColor= 0XFF4040E0;

    private static final int SEND_TIME = 1;

    private static final int DRAG_START_NEW_NUMBER  =   1;
    private static final int DRAG_START_KEEP        =   2;
    private static final int DRAG_START_ITEM_HORIZEN =  3;
    private static final int DRAG_START_ITEM_VERTICAL = 4;

    private     ConstraintLayout    mContainerView;
    private     TextView[]          mTvGrid;
    private     TextView[]          mTvNewNum;
    private     TextView            mTvKeepNum;
    private     DividerControl      mDiverControl;
    private     ProgressBar         mProgressTimer;
    private     TextView            Scoreview;
    private     TextView            ItemHorizen;
    private     TextView            ItemVertical;
    private     PopupWindow         mPopup;

    private     DBHelper dbHelper;

    private     MediaPlayer         mMediaPlayerDragStart;
    private     MediaPlayer         mMediaPlayerDivide;
    private     MediaPlayer         mMediaPlayerGameOver;
    private     MediaPlayer         mMediaPlayerItemUsed;
    private     MediaPlayer         mMediaPlayerImminent;

    private     int                 mDragStartPoint;

    private     boolean             isTimerOn;
    private     boolean             isRunning;
    private     Handler             mHandler;
    private     Thread              mTimerThread;

    public static int ingamescore = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.insertScore(User.getInstance().getUserName(), ingamescore);

        // 로그인 하지않으면 저장하지 않는다
        if(User.getInstance().getUserName().isEmpty()) {

        } else {

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this, "TermSpring", null, 1);

        mContainerView = findViewById(R.id.activity_container_view);

        mTvGrid = new TextView[9];
        mTvGrid[0] = findViewById(R.id.tv_grid_1_1);
        mTvGrid[1] = findViewById(R.id.tv_grid_1_2);
        mTvGrid[2] = findViewById(R.id.tv_grid_1_3);
        mTvGrid[3] = findViewById(R.id.tv_grid_2_1);
        mTvGrid[4] = findViewById(R.id.tv_grid_2_2);
        mTvGrid[5] = findViewById(R.id.tv_grid_2_3);
        mTvGrid[6] = findViewById(R.id.tv_grid_3_1);
        mTvGrid[7] = findViewById(R.id.tv_grid_3_2);
        mTvGrid[8] = findViewById(R.id.tv_grid_3_3);

        mTvNewNum = new TextView[4];
        mTvNewNum[0] = findViewById(R.id.tv_next_num_1);
        mTvNewNum[1] = findViewById(R.id.tv_next_num_2);
        mTvNewNum[2] = findViewById(R.id.tv_next_num_3);
        mTvNewNum[3] = findViewById(R.id.tv_next_num_4);

        mTvKeepNum = findViewById(R.id.tv_keep_num);

        ItemHorizen = findViewById(R.id.Itemhorizen);
        ItemVertical = findViewById(R.id.Itemvertical);

        mProgressTimer = findViewById(R.id.prog_Timerbar);

        Scoreview = findViewById(R.id.Scoreboard);

        /* check timer option */
        isTimerOn = getIntent().getBooleanExtra("timerOn", false);
        if(isTimerOn) {
            mProgressTimer.setMax(1200);
            mProgressTimer.setProgress(1200);

            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case SEND_TIME:
                            int progress = mProgressTimer.getProgress();
                            if(progress> 0) {
                                mProgressTimer.incrementProgressBy(-1);
                            }
                            if(progress == 200) {
                                mProgressTimer.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                            }
                            if(progress < 200 ) {
                                if(progress % 4 == 0) {
                                    mMediaPlayerImminent.start();
                                    if(mProgressTimer.getVisibility() == View.VISIBLE) mProgressTimer.setVisibility(View.INVISIBLE);
                                    else mProgressTimer.setVisibility(View.VISIBLE);
                                }
                            }
                            //mProgressTimer.setProgress(msg.arg1);
                            if(progress == 0) {
                                endGame();
                            }
                            break;
                        default:
                            super.handleMessage(msg);
                    }
                }
            };
            mTimerThread = new Thread(new TimerRunnable());
        }
        else {
            mProgressTimer.setVisibility(View.INVISIBLE);
        }

        /* initialize media player */
        initiateMediaPlayer();

        /* initialization and update Numbers */
        mDiverControl = new DividerControl();
        ingamescore = 0;
        updateNumbers();

        /* Drag start from New number */
        mTvNewNum[0].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                mDragStartPoint = DRAG_START_NEW_NUMBER;
                v.startDrag(data, shadow, null, 0);

                mMediaPlayerDragStart.start();
                return false;
            }
        });

        /* Drag start from Keep */
        mTvKeepNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mDiverControl.getKeepGrid() != 1) {
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                        mDragStartPoint = DRAG_START_KEEP;
                        v.startDrag(data, shadow, null, 0);

                        mMediaPlayerDragStart.start();
                    }
                }
                return false;
            }
        });

        ItemHorizen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                mDragStartPoint = DRAG_START_ITEM_HORIZEN;
                v.startDrag(data, shadow, null, 0);
                return false;
            }
        });

        ItemVertical.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                mDragStartPoint = DRAG_START_ITEM_VERTICAL;
                v.startDrag(data, shadow, null, 0);
                return false;
            }
        });

        mTvKeepNum.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (mDiverControl.getKeepGrid() == 1) {
                            return true;
                        }
                        else {
                            return false;
                        }
                    case DragEvent.ACTION_DRAG_ENTERED:
                        // show drag response
                        v.setBackgroundColor(sGridResponseBackgroundColor);
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // reset drag response
                        v.setBackgroundColor(sGridBackgroundColor);
                        return true;
                    case DragEvent.ACTION_DROP:
                        mDiverControl.insertKeep();
                        animateNewNumber();
                        updateNumbers();
                    case DragEvent.ACTION_DRAG_ENDED:
                        // reset all drag response
                        v.setBackgroundColor(sGridBackgroundColor);
                        return true;
                    default:
                        return false;
                }
            }
        });

        for(int i=0; i < 9; ++i) {
            mTvGrid[i].setTag(Integer.valueOf(i));
            mTvGrid[i].setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    int divideResult;
                    int action = event.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            if(mDragStartPoint == DRAG_START_ITEM_HORIZEN || mDragStartPoint == DRAG_START_ITEM_VERTICAL) return true;
                            if(((TextView)v).getText().equals("")) return true;
                            return false;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            // show drag response
                            v.setBackgroundColor(sGridResponseBackgroundColor);
                            return true;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            return true;
                        case DragEvent.ACTION_DRAG_EXITED:
                            // reset drag response
                            v.setBackgroundColor(sGridBackgroundColor);
                            return true;
                        case DragEvent.ACTION_DROP:
                            switch (mDragStartPoint) {
                                case DRAG_START_NEW_NUMBER:
                                    divideResult = mDiverControl.runMainDivide((Integer)v.getTag());
                                    break;
                                case DRAG_START_KEEP:
                                    divideResult = mDiverControl.runKeepDivide((Integer)v.getTag());
                                    break;
                                case DRAG_START_ITEM_HORIZEN :
                                    divideResult = mDiverControl.Itemhorizen((Integer)v.getTag());
                                    mMediaPlayerItemUsed.start();
                                    if(mDiverControl.AVAILABLE_ITEM_HORIZEN)      ItemHorizen.setVisibility(v.VISIBLE);
                                    else ItemHorizen.setVisibility(v.INVISIBLE);
                                    break;
                                case DRAG_START_ITEM_VERTICAL:
                                    divideResult = mDiverControl.Itemvertical((Integer)v.getTag());
                                    mMediaPlayerItemUsed.start();
                                    if(mDiverControl.AVAILABLE_ITEM_VERTICAL)      ItemVertical.setVisibility(v.VISIBLE);
                                    else    ItemVertical.setVisibility(v.INVISIBLE);
                                    break;
                                default:
                                    divideResult = DividerControl.RESULT_NOTING_TO_DIVIDE;
                            }
                            if (divideResult == DividerControl.RESULT_AREADY_NUMBER_EXIST) {
                            }
                            else if(divideResult == DividerControl.RESULT_GAME_END){
                                endGame();
                            }
                            else {
                                if (mDragStartPoint == DRAG_START_NEW_NUMBER)   animateNewNumber();
                                if (divideResult == DividerControl.RESULT_DIVIDE_DONE) mMediaPlayerDivide.start();
                                updateNumbers();
                            }

                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            // reset all drag response
                            v.setBackgroundColor(sGridBackgroundColor);
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }

        /*
        mContainerView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();

                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        mTvGrid[0][0].setText(String.format(Locale.ENGLISH, "%.0f", event.getX()));
                        mTvGrid[0][1].setText(String.format(Locale.ENGLISH, "%.0f", event.getY()));

                        return true;
                    case DragEvent.ACTION_DROP:
                        int dropPosition = mPositionHelper.getGridPosition(event.getX(), event.getY());
                        return false;
                    case DragEvent.ACTION_DRAG_ENDED:
                        return false;
                    default:
                        return false;
                }

            }
        });
        */
    }

    void endGame() {
        isRunning = false;
        updateNumbers();

        mMediaPlayerGameOver.start();

        View popupContent = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_main_finish, null);
        TextView tvScore = popupContent.findViewById(R.id.tv_score_finish);
        tvScore.setText("Score : " + Integer.toString(ingamescore));
        mPopup = new PopupWindow(popupContent, 300, 300, true);
        mPopup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mPopup.dismiss();
                }
                return false;
            }
        });
        mPopup.setAnimationStyle(R.style.popup_window_animation_fade);
        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                finish();
            }
        });
        //TODO: 로케이션 수정되어야함
        mPopup.showAtLocation(mTvKeepNum, Gravity.CENTER, 0, 0);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        anim.setFillAfter(true);
        mContainerView.startAnimation(anim);
    }


    /* TODO: calculate grid location and divide line? */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int[] loc = new int[2];
        int width, height;

        TableLayout tl = findViewById(R.id.tl_main_grid);

        tl.getLocationOnScreen(loc);
        width = tl.getWidth();
        height = tl.getHeight();
    }

    private void animateKeep() {

    }

    private  void initiateMediaPlayer() {
        mMediaPlayerDragStart = MediaPlayer.create(getApplicationContext(), R.raw.bubble);
        mMediaPlayerDivide = MediaPlayer.create(getApplicationContext(), R.raw.coin);
        mMediaPlayerGameOver = MediaPlayer.create(getApplicationContext(), R.raw.supermarios_boss_over2);
        mMediaPlayerItemUsed = MediaPlayer.create(getApplicationContext(), R.raw.useditem);
        mMediaPlayerImminent = MediaPlayer.create(getApplicationContext(), R.raw.supermarios_bros_castle);
    }

    private void animateNewNumber() {
        Animation an = AnimationUtils.loadAnimation(this,R.anim.new_number);
        for(int i=0; i < 4; ++i) {
            mTvNewNum[i].startAnimation(an);
        }
        //TODO: 애니메이션 동작 도중에 숫자를 바꾸려면 thread 예약 해야함
    }

    private void updateNumbers() {
        int[] gridNum = mDiverControl.getNumGrid();
        int[] newNumlist = mDiverControl.getNewNumList();
        int   keepNum = mDiverControl.getKeepGrid();

        Animation an = new RotateAnimation(0.0f, 360.f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        an.setDuration(500);

        for (int i=0; i < 9; ++i) {
            if(gridNum[i] != 1 && !mTvGrid[i].getText().equals(Integer.toString(gridNum[i])) ||
               gridNum[i] == 1 && !mTvGrid[i].getText().equals("")) {
                mTvGrid[i].startAnimation(an);
            }
            if(gridNum[i] == 1) mTvGrid[i].setText("");
            else                mTvGrid[i].setText(Integer.toString(gridNum[i]));
        }

        for (int i=0; i < 4; ++i) {
            mTvNewNum[i].setText(Integer.toString(newNumlist[i]));
        }

        if(keepNum == 1) mTvKeepNum.setText("");
        else            mTvKeepNum.setText((Integer.toString(keepNum)));

        Scoreview.setText("Score : " + Integer.toString(ingamescore));
    }

    class TimerRunnable implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    Thread.sleep(50); }
                catch (InterruptedException e) {e.printStackTrace(); }
                Message msg = mHandler.obtainMessage();
                msg.what = SEND_TIME;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isRunning = true;
        if (isTimerOn)  mTimerThread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        isRunning = false;
    }
}