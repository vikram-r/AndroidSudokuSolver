package com.cs541.vikram.sudokusolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by vikram on 3/2/15.
 */
public class BasicSudokuView extends View {

    private static final String TAG = "SudokuSolver";
    private static int DIM = 9;
    private static int BLOCK_SIZE = 3;
    Paint lineColor = new Paint();
    Paint gridColor = new Paint(Paint.ANTI_ALIAS_FLAG);



    public BasicSudokuView(Context context){
        super(context);
        Log.v(TAG, "CREATED VIEW");
        setFocusable(true);
        setFocusableInTouchMode(true);
        init();
    }

    private void init(){
        gridColor.setColor(getResources().getColor(R.color.White));

        lineColor.setStyle(Paint.Style.STROKE);
        lineColor.setColor(getResources().getColor(R.color.Black));

    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.v(TAG, "Drawing Board");
        //set the size of the sudoku grid
        final int WIDTH = getWidth();
        final int HEIGHT = new Long(Math.round(getHeight() * .90)).intValue();
        final float CELL_HEIGHT = HEIGHT / DIM;
        final float CELL_WIDTH = WIDTH / DIM;

        Log.v(TAG, "height: " + HEIGHT);
        Log.v(TAG, "width: " + WIDTH);
        Log.v(TAG, "cell height: " + CELL_HEIGHT);
        Log.v(TAG, "cell width: " + CELL_WIDTH);

        canvas.drawRect(0, 0, WIDTH, HEIGHT, gridColor); //left, top, right, bottom, color

        //draw lines
        for (int i = 0; i <= DIM; i++) {
            lineColor.setStrokeWidth(3);
            if (i % BLOCK_SIZE == 0){
                lineColor.setColor(getResources().getColor(R.color.Darkblue));
                lineColor.setStrokeWidth(40);
            }
            //horiz line
            canvas.drawLine(0, i * CELL_HEIGHT, WIDTH, i * CELL_HEIGHT, lineColor); //startx, starty, endx, endy
            Log.v(TAG, "Drawing " + lineColor.getStrokeWidth() + "line from (" + 0 + "," + i * CELL_HEIGHT + ") to (" + WIDTH + "," + i *CELL_HEIGHT + ")");
            //vert line
            canvas.drawLine(i * CELL_WIDTH, 0, i * CELL_WIDTH, HEIGHT, lineColor);
            Log.v(TAG, "Drawing " + lineColor.getStrokeWidth() + "line from (" + i * CELL_WIDTH + "," + 0 + ") to (" + i * CELL_WIDTH + "," + HEIGHT + ")");

        }

    }




}
