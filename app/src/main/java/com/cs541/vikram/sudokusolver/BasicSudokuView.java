package com.cs541.vikram.sudokusolver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by vikram on 3/2/15.
 */
public class BasicSudokuView extends View {

    private final String TAG = "SudokuSolver";
    private final int DIM = 9;
    private final int BLOCK_SIZE = 3;

    private int WIDTH;
    private int HEIGHT;
    private float CELL_HEIGHT;
    private float CELL_WIDTH;

    private Rect clickedCell;

    Paint lineColor = new Paint();
    Paint gridColor = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint clickedColor = new Paint();



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

        clickedCell = new Rect();
        clickedColor.setColor(getResources().getColor(R.color.Red));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            float x = event.getX();
            float y = event.getY();
            Log.v(TAG, "Touched (" + x + ", " + y + ")");
            //figure out which square it belongs to, and create a rectangle that we can color later

            Cell selected = new Cell(x, y);
            Log.v(TAG, "GOT Cell" + "[" + selected.row + "," + selected.column + "]");
            clickedCell = selected.getRect();
            invalidate(clickedCell); //refreshes just this cell. invalidate() takes too long
            openNumpad();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void openNumpad(){ //todo does this belong in application and not view?
        Log.v(TAG, "HERE");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //builder.setTitle("Select a value");
        builder.setMessage("Select a value");
        final EditText et = new EditText(this.getContext());
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(et);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                setValueForCell(et.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void setValueForCell(String value){
        Log.v(TAG, "Try to set value " + value + "!");
        try {
            if (Integer.parseInt(value) < 1 || Integer.parseInt(value) > 9){
                throw new IllegalArgumentException("Invalid input");
            }

        }catch(Exception e){
            Log.v(TAG, "INVALID VALUE ENTERED!");
            //todo invalid input, what do I do here?
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.v(TAG, "Drawing Board");
        //set the size of the sudoku grid
        WIDTH = getWidth();
        HEIGHT = new Long(Math.round(getHeight() * .90)).intValue();
        CELL_HEIGHT = HEIGHT / DIM;
        CELL_WIDTH = WIDTH / DIM;

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
            //vert line
            canvas.drawLine(i * CELL_WIDTH, 0, i * CELL_WIDTH, HEIGHT, lineColor);

        }

        //draw clicked cell if possible
        canvas.drawRect(clickedCell, clickedColor);

    }

    private class Cell {
        int row;
        int column;

        public Cell(float x, float y){
            //height/dim and width/dim are the cell height and cell width respectively
            this.row = Double.valueOf(Math.floor( y / (HEIGHT / DIM) )).intValue();
            this.column = Double.valueOf(Math.floor( x / (WIDTH / DIM) )).intValue();
        }

        public Rect getRect(){
            Rect rect = new Rect();
            //todo lazy coding, because cell_width/height are doubles in the global scope
            int CELL_WIDTH = WIDTH / DIM;
            int CELL_HEIGHT = HEIGHT / DIM;
            //left, top, right, bottom
            int left = column * CELL_WIDTH;
            int top = row * CELL_HEIGHT;
            rect.set(left, top, left + CELL_WIDTH, top + CELL_HEIGHT);
            Log.v(TAG,"Rect: " + rect.toShortString());
            return rect;
        }


    }


}
