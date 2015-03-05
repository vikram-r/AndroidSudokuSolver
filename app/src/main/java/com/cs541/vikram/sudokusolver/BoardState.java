package com.cs541.vikram.sudokusolver;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vikram on 3/3/15.
 */
public class BoardState {
    private final String TAG = "BoardState";
    private boolean isNewPuzzle;

    private int WIDTH;
    private int HEIGHT;
    private int DIM;

    private final int A_VAL = 65; //ascii value of 'A'


    Map<String, PosValues> possibleValues; //each cell name maps to an object which contains the cell's rectangle and potential values

    Map<String, List<Set<String>>> neighborhoods; //each cell name maps to a set containing all of it's neighborhoods (3 each)
                                                  //A single neighborhood is all the cells in it, including itself



    public BoardState(){
        possibleValues = new HashMap<>();
        neighborhoods = new HashMap<>();
        isNewPuzzle = true;
    }

    public void createStructure(int width, int height, int dim){
        WIDTH = width;
        HEIGHT = height;
        DIM = dim;

        //ascii value of A is 65
        for (int i = A_VAL; i < A_VAL + DIM; i++) {//go through 81 squares and add their value to the map
            String rowIdent = Character.toString((char)i);
            for (int j = 1; j <= DIM; j++) { //1 to 9 (not 0 to 8)
                String cellName = rowIdent + j;

                Set<Integer> initialNumValues = new HashSet(Arrays.asList(1,2,3,4,5,6,7,8,9)); //initially a cell can be any value
                Rect rectValue = makeRectForCell(i - A_VAL, j - 1);//the row and column here are 0 indexed for easier calculation

                possibleValues.put(cellName, new PosValues(initialNumValues, rectValue));

            }
        }

        createNeighborhoods();

        isNewPuzzle = false;
        Log.v(TAG, possibleValues.toString());
    }

    //todo this code is a nightmare
    //make all of the valid possible neighborhoods, then assign each cell to them
    private void createNeighborhoods(){
        List<Set<String>> neighborhoodList= new ArrayList<>();

        //first add all row neighborhoods (9)
        for (int i = A_VAL; i < A_VAL + DIM; i++) {
            Set<String> thisNeighborhood = new HashSet<>();
            for (int j = 1; j <= DIM; j++) {
                String cellName = Character.toString((char)i) + j;
                thisNeighborhood.add(cellName);
            }
            neighborhoodList.add(thisNeighborhood);
        }

        //now add all column neighborhoods (9)
        for (int j = 1; j <= DIM; j++) {
            Set<String> thisNeighborhood = new HashSet<>();
            for (int i = A_VAL; i < A_VAL + DIM; i++) {
                String cellName = Character.toString((char) i) + j;
                thisNeighborhood.add(cellName);
            }
            neighborhoodList.add(thisNeighborhood);
        }

        //now add all block neighborhoods (9)
        for (int i = A_VAL; i < A_VAL + DIM; i += 3) {
            for (int j = 1; j <= DIM; j += 3) {
                neighborhoodList.add(computeBlockNeighborhood(i, i + 3, j, j + 3));
            }
        }

        //now go through all the neighborhoods and add them to all cells inside them
        for (Set<String> neighborhood : neighborhoodList){
            for (String cellName : neighborhood){
                List<Set<String>> thisCellsNeighborhoods = neighborhoods.get(cellName); //get existing list of neighborhoods
                if (thisCellsNeighborhoods == null){
                    thisCellsNeighborhoods = new ArrayList<>();
                }
                thisCellsNeighborhoods.add(neighborhood); //add the new neighborhood to the existing list
                neighborhoods.put(cellName, thisCellsNeighborhoods); //store the list of neighborhoods back in the map
            }
        }

        Log.v(TAG, "HERE WE GO: " + neighborhoods.toString());
    }

    private Set<String> computeBlockNeighborhood(int letStart, int letEnd, int numStart, int numEnd){
        Set<String> thisNeighborhood = new HashSet<>();
        for (int i = letStart; i < letEnd; i++) {
            for (int j = numStart; j < numEnd; j++) {
                String cellName = Character.toString((char) i) + j;
                thisNeighborhood.add(cellName);
            }
        }
        return thisNeighborhood;
    }

    //used when initially setting cell values. Can maybe do validation here
    public void setAbsoluteValueWithName(String name, int absValue){
        Log.v(TAG, "Setting Value " + absValue + " for Cell: " + name);
        //possibleValues.get(name).valueSet.retainAll(new HashSet(Arrays.asList(absValue))); //make the set only contain this value, since it is correct
        possibleValues.get(name).valueSet = new HashSet(Arrays.asList(absValue)); //make the set only contain this value, since it is correct
        Log.v(TAG, possibleValues.toString());
    }

    public int getAbsoluteValueWithRowCol(int row, int col){
        Set<Integer> vals = possibleValues.get(convertRowColToName(row, col)).valueSet;
        if (vals.size() == 1){
            for (int val : vals){ //todo ugly, replace with iterator
                return val;     //this will only loop once, because there is only 1 element anyways
            }
        }
        return -1; //bad value
    }

    public Rect getRectForCoordinates(float x, float y){
        return possibleValues.get(getNameForCoordinates(x, y)).rect;
    }

    //helper method for getRectForCoordinates, but also used in view to keep track of selected key - bad design
    public String getNameForCoordinates(float x, float y){
        int row = Double.valueOf(Math.floor( y / (HEIGHT / DIM) )).intValue();
        int column = Double.valueOf(Math.floor( x / (WIDTH / DIM) )).intValue();

        return convertRowColToName(row, column);
    }

    //helper method for getRectForCoordinates
    //incoming rows/col will be 0 to 8
    private String convertRowColToName(int row, int col){
//        Log.v(TAG, "converter converting (" + row + "," + col + ") into " + Character.toString((char)(row + A_VAL)) + (col + 1));
        return Character.toString((char)(row + A_VAL)) + (col + 1);
    }


    //Helper method for constructor
    //should only ever be called once per cell in the constructor
    private Rect makeRectForCell(int row, int column){
        Rect rect = new Rect();
        //todo lazy coding, because cell_width/height are doubles in the global scope
        int CELL_WIDTH = WIDTH / DIM;
        int CELL_HEIGHT = HEIGHT / DIM;
        //left, top, right, bottom
        int left = column * CELL_WIDTH;
        int top = row * CELL_HEIGHT;
        rect.set(left, top, left + CELL_WIDTH, top + CELL_HEIGHT);
        return rect;
    }

    public boolean getIsNewPuzzle(){
        return isNewPuzzle;
    }

    public void setDefaultPuzzle(String PUZZLE){
        Log.v(TAG, "Setting up default puzzle");
        int counter = 0;

        for (int i = A_VAL; i < A_VAL + DIM; i++) {
            for (int j = 1; j <= DIM; j++) {
                if (PUZZLE.charAt(counter) != '0'){
                    String cellName = Character.toString((char)i) + j;
                    setAbsoluteValueWithName(cellName, Integer.parseInt(Character.toString(PUZZLE.charAt(counter)))); //trusting input is correct
                }

                counter++;
            }
        }
    }




    //wrapper object so i'm not constantly generating new rect objects
    private class PosValues{
        public Set<Integer> valueSet;
        public Rect rect;

        public PosValues(Set<Integer> valueSet, Rect rect){
            this.valueSet = valueSet;
            this.rect = rect;
        }

        @Override
        public String toString(){
           return valueSet.toString();
        }
    }

}
