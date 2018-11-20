package com.pnu.cse.termspring2018;

import android.util.Log;

import java.util.Random;
import java.util.logging.Level;

public class DividerControl {
    public final static int RESULT_AREADY_NUMBER_EXIST = 1;
    public final static int RESULT_DIVIDE_DONE = 2;
    public final static int RESULT_NOTING_TO_DIVIDE = 3;
    public final static int RESULT_GAME_END = 4;
    public final static int RESULT_USE_ITEM = 5;

    public static boolean AVAILABLE_ITEM_HORIZEN =  true;
    public static boolean AVAILABLE_ITEM_VERTICAL = true;

    private int[] MAX_LEVEL = {2,3,5,7,11,13,17};
    private int Level_index = 4;
    private int[] mNumGrid;
    private int[] mNewNumList;
    private int mKeepGrid;
    private int   count;

    private int newnumcnt = 0;

    public int[] getNumGrid()    { return mNumGrid; }
    public int[] getNewNumList() { return mNewNumList; }
    public int getKeepGrid()     { return mKeepGrid;   }

    public DividerControl() {
        mNumGrid = new int[9];
        mNewNumList = new int[4];

        for (int i=0; i < 9; ++i) makeEmpty(i);
        for (int i=0; i < 4; ++i) mNewNumList[i] = generateNumber();

        mKeepGrid = 1;
    }

    private void makeEmpty(int id) {
        // indivisible number is empty
        mNumGrid[id] = 1;
    }

    private int generateNumber() {
        Random rd = new Random();
        int result;
        int Score = MainActivity.ingamescore;
        while(true) {
            result = 1;
            if(Score > 800 && Level_index == 4) Level_index++;
            else if(Score > 2000 && Level_index == 5)   Level_index++;
            else if(Score > 4000 && Level_index == 6)   Level_index++;

            int a[] = new int[Level_index];
            for(int i=0; i< Level_index; i++){
                int bound;
                if(i == Level_index-1) bound = 2;
                else bound = 3;
                a[i] = MAX_LEVEL[i] * rd.nextInt(bound);
                if(a[i] == 0) a[i] = 1;
                result *= a[i];
                if(result > 100 && result == 1) break;
            }

            if (result < 100 && result > 1) break;
        }
        return result;
    }
    //generate a number and slide num number list
    private void makeNewNumber() {
        for (int i=0; i < 3; ++i) {
            mNewNumList[i] = mNewNumList[i+1];
        }
        mNewNumList[3] = generateNumber();
    }

    //@param id: drop(drag and drop) grid id from NumGrid
    public int runMainDivide (int id) {

        int old_count;
        int combo_count = 0;

        if (mNumGrid[id] != 1) {
            return RESULT_AREADY_NUMBER_EXIST;
        }
        mNumGrid[id] = mNewNumList[0];
        makeNewNumber();

        Log.d("progress", "Instered");

        count = 0;
        old_count = count;


        divide(id);

        while (true) {
            for(int i=0; i < 9; ++i) divide(i);
            if(count == 0) {
                if(isEndgame()) return RESULT_GAME_END;
                return RESULT_NOTING_TO_DIVIDE;
            }
            if(old_count == count) break;
            old_count = count;
            combo_count++;
            MainActivity.ingamescore += (count * combo_count) * 10;
            ClearGrid();
        }
        if (count > 0) {
            return RESULT_DIVIDE_DONE;
        }
        return RESULT_NOTING_TO_DIVIDE;
    }

    public int insertKeep(){
        mKeepGrid = mNewNumList[0];
        makeNewNumber();
        return RESULT_DIVIDE_DONE;

    }

    //@param id: drop(drag and drop) grid id from KeepGrid
    public int runKeepDivide (int id) {

        int old_count;
        int combo_count = 0;

        if (mNumGrid[id] != 1) {
            return RESULT_AREADY_NUMBER_EXIST;
        }

        mNumGrid[id] = mKeepGrid;
        mKeepGrid = 1;

        Log.d("progress", "Instered");

        count = 0;
        old_count = count;

        divide(id);

        while (true) {

            for(int i=0; i < 9; ++i) divide(i);
            if(count == 0) {
                if(isEndgame()) return RESULT_GAME_END;
                return RESULT_NOTING_TO_DIVIDE;
            }
            if(old_count == count) break;
            old_count = count;
            combo_count++;
            MainActivity.ingamescore += (count * combo_count) * 10;
            ClearGrid();
        }
        if (count > 0) {
            return RESULT_DIVIDE_DONE;
        }
        return  RESULT_NOTING_TO_DIVIDE;
    }

    private int findLeft(int id) {
        if ( id % 3 == 0 ) return -1;
        else return id - 1;
    }
    private int findRight(int id) {
        if ( id % 3 == 2 ) return -1;
        else return id + 1;
    }
    private int findUp(int id) {
        if ( id / 3 == 0 ) return -1;
        else return id - 3;
    }
    private  int findDown(int id) {
        if ( id / 3 == 2 ) return -1;
        else return id + 3;
    }

    private void divide (int id) {
        int self = id;
        int[] adjacent = new int[4];
        adjacent[0] = findLeft(id);
        adjacent[1] = findRight(id);
        adjacent[2] = findUp(id);
        adjacent[3] = findDown(id);

        int biggest = 1;
        //int biggest2 = 1;

        //int position = -1;

        for (int i = 0; i < 4; ++i) {
            if (adjacent[i] != -1) {
                biggest = findBiggestDivisor(self, adjacent[i], biggest);
                // position = findBiggestPosition(self, adjacent[i], biggest2, position);
            }

        }

        if (biggest != 1) {
            for (int i = 0; i < 4; ++i) {
                if (adjacent[i] != -1) {
                    divideBiggest(adjacent[i], biggest);
                }
            }
            divideBiggest(self, biggest);
        }
    }


    private int findBiggestDivisor(int a_id, int b_id, int biggest) {
        int a = mNumGrid[a_id];
        int b = mNumGrid[b_id];
        //Log.d("ab: ", a + " " + b );
        if( a % b == 0 || b % a == 0) {             //입력한 값과 비교값이 나누어 떨어지는지 확인 후
            if (a > b && b > biggest) return b;     //기존의 Biggest보다 divider가 크면 Biggest를 새로 바꿔줌.
            else if (a <= b && a > biggest) return a;
        }
        return biggest;
    }

    private int findBiggestPosition(int a_id, int b_id, int biggest, int position) {
        int a = mNumGrid[a_id];
        int b = mNumGrid[b_id];
        //Log.d("ab: ", a + " " + b );
        if( a % b == 0 || b % a == 0) {             //입력한 값과 비교값이 나누어 떨어지는지 확인 후
            if (a > b && b > biggest) return b_id;     //기존의 Biggest보다 divider가 크면 Biggest를 새로 바꿔줌.
            else if (a <= b && a > biggest) return a_id;
        }
        return position;
    }

    private void divideBiggest(int id, int biggest) {
        int input = mNumGrid[id];
        if (input != 1) {
            if (biggest <= input) {
                if (input % biggest == 0) {
                    count++;
                    mNumGrid[id] = input / biggest;
                }
            } else {
                if (biggest % input == 0) {
                    count++;
                    mNumGrid[id] = 1;
                }
            }
        }
    }


    public void ClearGrid(){        // if all grids are empty, get 100 scores.
        boolean Clear = true;
        for(int i=0; i<9; i++){
            if(mNumGrid[i] != 1)    Clear = false;
        }
        if(Clear == true)   MainActivity.ingamescore += 100;
    }

    public boolean isEndgame(){
        boolean End = true;
        for(int i=0; i<9; i++){
            if(mNumGrid[i] == 1)    End = false;
        }
        return End;
    }


    public int Itemvertical(int id){
        int initialized = 0;
        if(AVAILABLE_ITEM_VERTICAL) {
            int a = 0;
            int b = 0;
            if (id / 3 == 0) {
                a = id + 3;
                b = id + 6;
            } else if (id / 3 == 1) {
                a = id - 3;
                b = id + 3;
            } else {
                a = id - 6;
                b = id - 3;
            }

            if(mNumGrid[a] != 1){
                mNumGrid[a] = 1;
                initialized++;
            }
            if(mNumGrid[b] != 1){
                mNumGrid[b] = 1;
                initialized++;
            }
            if(mNumGrid[id] != 1) {
                mNumGrid[id] = 1;
                initialized++;
            }
            MainActivity.ingamescore += initialized * 10;

            AVAILABLE_ITEM_VERTICAL = false;
        }

        return RESULT_USE_ITEM;
    }

    public int Itemhorizen(int id){
        int initialized = 0;
        if(AVAILABLE_ITEM_HORIZEN) {
            int a = 0;
            int b = 0;
            if (id % 3 == 0) {
                a = id + 1;
                b = id + 2;
            } else if (id % 3 == 1) {
                a = id - 1;
                b = id + 1;
            } else {
                a = id - 2;
                b = id - 1;
            }

            if(mNumGrid[a] != 1){
                mNumGrid[a] = 1;
                initialized++;
            }
            if(mNumGrid[b] != 1){
                mNumGrid[b] = 1;
                initialized++;
            }
            if(mNumGrid[id] != 1) {
                mNumGrid[id] = 1;
                initialized++;
            }
            MainActivity.ingamescore += initialized * 10;

            AVAILABLE_ITEM_HORIZEN = false;
        }

        return RESULT_USE_ITEM;
    }

}

// invisible 해제 조건 및 점수설정