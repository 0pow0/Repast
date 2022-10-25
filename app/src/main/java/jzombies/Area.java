package jzombies;

import java.util.Random;
import java.util.ArrayList;
import java.lang.Math; 

class Area{
    int[] noFlyZone = new int[2];   // currently only one no fly zone
                                    //and we only record its top-left corner
    int nfzSize= 10;    // no fly zone size is 10 * 10

    ArrayList<int[]> launchingAreas = new ArrayList<int[]>();   // max 5 lauching areas
    ArrayList<int[]> landingAreas = new ArrayList<int[]>();     // max 5 landing areas
    int areaSize = 3;   // lauching and landing area size is 3 * 3

    // generate two random integers as row and col
    // parameter size is area size
    public int[] getRandLocation(int size) {
        Random r = new Random();
        int row = r.nextInt(100-size);
        int col = r.nextInt(100-size);
        int[] result = {row, col};
        return result;
    }

    // create a new top-left corner of no fly zone
    public void newNoFlyZone() {
        this.noFlyZone = getRandLocation(nfzSize);
    }

    // check if new area is qualified with two conditions:
    // 1. distances between itself and other areas are larger than 7
    // 2. not in the no fly zone
    public Boolean isQualified(int row, int col) {
        // condition 1
        for (int[] selectedArea : launchingAreas) {
            int sRow = selectedArea[0];
            int sCol = selectedArea[1];
            if (Math.abs(row-sRow) < 7 && Math.abs(col-sCol) < 7) {
                return false;
            }
        }
        for (int[] selectedArea : landingAreas) {
            int sRow = selectedArea[0];
            int sCol = selectedArea[1];
            if (Math.abs(row-sRow) < 7 && Math.abs(col-sCol) < 7) {
                return false;
            }
        }
        //condition 2
        int nfzRow1 = noFlyZone[0]; // top-left corner of no fly zone
        int nfzCol1 = noFlyZone[1];
        int nfzRow2 = noFlyZone[0] + nfzSize; // bottom-right corner of no fly zone
        int nfzCol2 = noFlyZone[1] + nfzSize;
        if ( (nfzRow1 <= row && row <= nfzRow2 && nfzCol1 <= col && col <= nfzCol2)
            || (nfzRow1 <= row && row <= nfzRow2 && nfzCol1 <= col+5 && col+5 <= nfzCol2)
            || (nfzRow1 <= row+5 && row+5 <= nfzRow2 && nfzCol1 <= col && col <= nfzCol2)
            || (nfzRow1 <= row+5 && row+5 <= nfzRow2 && nfzCol1 <= col+5 && col+5 <= nfzCol2)
        ) {
            return false;
        }
        return true;
    }

    public void addLaunchingArea() {
        while(true) {
            int[] newLoc = getRandLocation(areaSize);
            int row = newLoc[0];
            int col = newLoc[1];
            if (isQualified(row, col)) {
                launchingAreas.add(newLoc);
                break;
            }
        }
    }

    public void addLandingArea() {
        while(true) {
            int[] newLoc = getRandLocation(areaSize);
            int row = newLoc[0];
            int col = newLoc[1];
            if (isQualified(row, col)) {
                landingAreas.add(newLoc);
                break;
            }
        }
    }
    
//    public static void main(String[] args) {
//        Area a = new Area();
//        a.newNoFlyZone();
//        for (int i = 0; i<5; i++) {
//            a.addLaunchingArea();
//        }
//        for (int i = 0; i<5; i++) {
//            a.addLandingArea();
//        }
//        
//        System.out.print("No fly zone top-left corner (row, col): ");
//        System.out.printf("(%d,%d)\n", a.noFlyZone[0], a.noFlyZone[1]);
//        System.out.println("5 launching areas top-left corner (row, col):");
//        for (int[] areas : a.launchingAreas) {
//            System.out.printf(" (%d,%d)\n", areas[0], areas[1]);
//        }
//        System.out.println("5 landing areas top-left corner (row, col):");
//        for (int[] areas : a.landingAreas) {
//            System.out.printf(" (%d,%d)\n", areas[0], areas[1]);
//        }
//
//    }


}