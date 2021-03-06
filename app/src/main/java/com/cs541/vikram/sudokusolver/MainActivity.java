package com.cs541.vikram.sudokusolver;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    private final String TAG = "SudokuSolver";
    private BoardState boardState = new BoardState();

    public enum DEFAULT_PUZZLE { EASY, HARD, HARDEST, NONE };

    public DEFAULT_PUZZLE PUZZLE_CHOICE = DEFAULT_PUZZLE.HARD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.sudoku_layout);

        //BasicSudokuView basicSudokuView = new BasicSudokuView(this); //todo maybe make this a field var
        //setContentView(basicSudokuView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void solveSudoku(View view){
        if (boardState.solve()){
            Log.v(TAG, "SOLVED!");
            setContentView(R.layout.sudoku_layout); //to redraw board (it wont reset since im not deleting boardState)
        }else{
            Log.v(TAG, "COULD NOT SOLVE");
        }

    }

    public void clearBoard(View view){
        boardState = new BoardState();
        PUZZLE_CHOICE = DEFAULT_PUZZLE.NONE; //don't use the default puzzle upon clear

        setContentView(R.layout.sudoku_layout);
    }

    //method to let activity access board through getContext()
    public BoardState getBoardState(){
        return boardState;
    }

    @Override
    protected void onPause(){
        super.onPause();

        //todo save puzzle
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
}
