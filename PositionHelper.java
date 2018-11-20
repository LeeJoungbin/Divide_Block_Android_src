package com.pnu.cse.termspring2018;

public class PositionHelper {
    private int mWidth;
    private int mHeight;
    private int mMargin;
    private int mStartX;
    private int mStartY;

    PositionHelper(int margin) {
        this.mMargin = margin;
    }

    public void setUp(int startX, int startY, int width, int height) {
        this.mStartX = startX;
        this.mStartY = startY;
        this.mWidth = width;
        this.mHeight = height;
    }

    /* Returns position of grid 1 to 9. Returns 0 if none of specific grid. */
    int getGridPosition(float X, float Y)  {
        return 0;
    }
    /*

    int getRealVerticalPosition() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        getGlobalRect()

    }

    */
}
