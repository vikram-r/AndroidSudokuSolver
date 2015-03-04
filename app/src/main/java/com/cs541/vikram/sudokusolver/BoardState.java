package com.cs541.vikram.sudokusolver;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by vikram on 3/3/15.
 */
public class BoardState {
    private final String TAG = "SudokuSolver-BoardState";

    private int WIDTH;
    private int HEIGHT;
    private int DIM;

    private final int A_VAL = 65; //ascii value of 'A'


    Map<String, Set<Integer>> possibleValues;

    public BoardState(){
        possibleValues = new HashMap<>();
    }

    public void createStructure(int width, int height, int dim){
        Log.v(TAG, "I made it!");
        WIDTH = width;
        HEIGHT = height;
        DIM = dim;

        //ascii value of A is 65
        for (int i = A_VAL; i < A_VAL + DIM; i++) {//go through 81 squares and add their value to the map
            String rowIdent = Character.toString((char)i);
            for (int j = 1; j <= DIM; j++) { //1 to 9 (not 0 to 8)
                String cellName = rowIdent + j;

                Set<Integer> initialValues = new HashSet(Arrays.asList(1,2,3,4,5,6,7,8,9)); //initially a cell can be any value
                possibleValues.put(cellName, initialValues);

            }
        }

        Log.v(TAG, possibleValues.toString());
    }

}
