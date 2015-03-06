package com.cs541.vikram.sudokusolver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by vikram on 3/2/15.
 */
public class BasicSudokuView extends View {

    private final String TAG = "BasicSudokuView";

    private final String EASY_PUZZLE = "003020600900305001001806400008102900700000008006708200002609500800203009005010300";
    private final String HARD_PUZZLE = "400000805030000000000700000020000060000080400000010000000603070500200000104000000";
    //private final String HARD_PUZZLE = "630000000000500008005674000000020000003401020000000345000007004080300902947100080";
    //private final String HARD_PUZZLE = "850002400720000009004000000000107002305000900040000000000080070017000000000036040";
    private final String HARDEST_PUZZLE = "800000000003600000070090200050007000000045700000100030001000068008500010090000400";


    private final int DIM = 9;
    private final int BLOCK_SIZE = 3;

    private int WIDTH;
    private int HEIGHT;
    private float CELL_HEIGHT;
    private float CELL_WIDTH;

    private Rect clickedCell;
    private String clickedCellName;

    Paint lineColor = new Paint();
    Paint gridColor = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint clickedColor = new Paint();
    Paint numberColor = new Paint(Paint.ANTI_ALIAS_FLAG);



    public BasicSudokuView(Context context){
        this(context, null);

    }
    public BasicSudokuView(Context context, AttributeSet attrs){
        this(context, attrs, 0);

    }
    public BasicSudokuView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        setFocusable(true);
        setFocusableInTouchMode(true);
        initCanvas();

    }

    private void initCanvas(){
        gridColor.setColor(getResources().getColor(R.color.White));

        lineColor.setStyle(Paint.Style.STROKE);
        lineColor.setColor(getResources().getColor(R.color.Black));

        clickedCell = new Rect();
        clickedColor.setColor(getResources().getColor(R.color.LightYellow));

        numberColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberColor.setColor(getResources().getColor(R.color.Black));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() <= WIDTH && event.getY() <= HEIGHT){
            float x = event.getX();
            float y = event.getY();
            //figure out which square it belongs to, and create a rectangle that we can color later

//            Cell selected = new Cell(x, y);

            clickedCell = getBoardState().getRectForCoordinates(x, y);
            clickedCellName = getBoardState().getNameForCoordinates(x, y); //todo terrible terrible design

            invalidate(clickedCell); //refreshes just this cell. invalidate() takes too long
            openNumpad();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void openNumpad(){
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
        try {
            int intVal = Integer.parseInt(value);
            if (intVal < 1 || intVal > 9){
                throw new IllegalArgumentException("Invalid input");
            }else{
                //getBoardState().setAbsoluteValueWithName(clickedCellName, intVal);

                if (!getBoardState().userAddCell(clickedCellName, intVal)){//try to add it
                    throw new IllegalArgumentException("A neighbor already contains that digit");
                }
                //refresh the square again because a number was added
                invalidate(clickedCell); //todo if bug comes back, remove this
            }
        }catch(Exception e){
            Log.v(TAG, "INVALID VALUE ENTERED!");
            //todo invalid input, what do I do here?
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        WIDTH = getWidth();
        //HEIGHT = new Long(Math.round(getHeight() * .90)).intValue();
        HEIGHT = getHeight(); //my layout now handles scaling for me :D

        if (getBoardState().getIsNewPuzzle()){
            getBoardState().createStructure(WIDTH, HEIGHT, DIM);


            //todo make this modifiable with command line!
            //initialize some default values if necessary
            if (((MainActivity)getContext()).PUZZLE_CHOICE == MainActivity.DEFAULT_PUZZLE.EASY){
                String PUZZLE = EASY_PUZZLE;
                getBoardState().setDefaultPuzzle(PUZZLE);
            }else if(((MainActivity)getContext()).PUZZLE_CHOICE == MainActivity.DEFAULT_PUZZLE.HARD){
                String PUZZLE = HARD_PUZZLE;
                getBoardState().setDefaultPuzzle(PUZZLE);
            }else if(((MainActivity)getContext()).PUZZLE_CHOICE == MainActivity.DEFAULT_PUZZLE.HARDEST){
                String PUZZLE = HARDEST_PUZZLE;
                getBoardState().setDefaultPuzzle(PUZZLE);
            }

        }

        //set the size of the sudoku grid
        CELL_HEIGHT = HEIGHT / DIM;
        CELL_WIDTH = WIDTH / DIM;

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

        //draw the numbers that are set
        numberColor.setTextSize(CELL_HEIGHT * 0.75f);
        numberColor.setTextScaleX(CELL_WIDTH / CELL_HEIGHT);
        numberColor.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = numberColor.getFontMetrics();
        float middle_width = CELL_WIDTH / 2;
        float middle_height = CELL_HEIGHT / 2 - (fm.ascent + fm.descent) / 2;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                String toDraw = "";
                int val = getBoardState().getAbsoluteValueWithRowCol(j, i); //NOTICE IT IS (ROW, COL) = (Y, X)
                if (val != -1){
                    toDraw = val + "";
                }
                canvas.drawText(toDraw,  middle_width + (i * CELL_WIDTH), middle_height + (j * CELL_HEIGHT), numberColor);
            }
        }

    }

    //get board status from the activity
    public BoardState getBoardState(){
        return ((MainActivity)getContext()).getBoardState();
    }

}
