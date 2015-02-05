import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Raindrinker on 05/02/2015.
 */
public class DungeonGenerator {

    /**
     * Returns a dungeon represented by an array of integers, where 0s are walls
     * @param xsize horizontal size of the dungeon
     * @param ysize vertical size of the dungeon
     * @param numberOfRooms numberOfRooms wanted in the dungeon
     * @param minRoomSize minimum room width and height
     * @param maxRoomSize maximum room width and height
     * @param trim number of times we try to erase dead ends
     * @param eraseColumns if we want lone walls to be erased or not
     * @param expand number of times we break walls surrounded by 3 or more empty spaces
     * @return 2D Integer matrix
     */
    public static int[][] Generate(int xsize, int ysize, int numberOfRooms, int minRoomSize, int maxRoomSize, int trim, boolean eraseColumns, int expand){

        //Create the Integer grid
        int[][] t = new int[xsize][ysize];

        //Place rooms
        int num = placeRooms(t, numberOfRooms, maxRoomSize, minRoomSize);

        //Create maze
        createMaze(t, num+1);

        //Flood fill, ensures that connected regions have the same number
        floodFill(t);

        //Connect all regions
        connectRegions(t);

        //Trim
        trim(t, trim);

        //Erase columns
        eraseColumns(t, eraseColumns);

        //Expand
        expand(t, expand);


        return t;
    }

    /**
     * Places rooms in a given 2D matrix
     * @param t 2D matrix
     * @param number number of rooms wanted
     * @param maxsize maximum room width and height
     * @param minsize minimum room width and height
     * @return number of regions created, needed for further use
     */
    private static int placeRooms(int[][] t, int number, int maxsize, int minsize){
        int ypos, xpos, roomsizex, roomsizey, sizex = t.length, sizey = t[0].length;
        int num = 0;
        boolean ok;

        for (int i = 0; i < number*10 && num < number; i++) {
            ypos = (int) (Math.random() * (sizey-2)) + 1;
            xpos = (int) (Math.random() * (sizex-2)) + 1;
            if(xpos % 2 == 0){
                xpos--;
            }
            if(ypos % 2 == 0){
                ypos--;
            }
            roomsizex = ((int) (Math.random() * (maxsize - minsize)) + minsize);
            roomsizey = ((int) (Math.random() * (maxsize - minsize)) + minsize);
            if(roomsizex % 2 == 0){
                roomsizex--;
            }
            if(roomsizey % 2 == 0){
                roomsizey--;
            }
            ok = true;
            for (int j = xpos; j < xpos + roomsizex; j++) {
                for (int k = ypos; k < ypos + roomsizey; k++) {
                    if (inBounds(t, j, k)) {
                        if (t[j][k] != 0) {
                            ok = false;
                        }
                    } else {
                        ok = false;
                    }
                }

            }
            if (ok) {
                num++;
                for (int l = xpos; l < xpos + roomsizex; l++) {
                    for (int m = ypos; m < ypos + roomsizey; m++) {
                        t[l][m] = num;
                    }
                }
            }
        }
        return num;
    }

    /**
     * Fills the empty space in a given 2D matrix with random passageways
     * @param t 2D matrix
     * @param num number of regions already created in the matrix
     */
    private static void createMaze(int[][] t, int num) {
        int sizex = t.length, sizey = t[0].length;

        for (int i = 1; i < sizex; i += 2) {
            for (int j = 1; j < sizey; j += 2) {
                if (numOuts(t, i, j) == 0) {
                    t[i][j] = num;
                    tunnel(t, i, j);
                    num++;

                }
            }
        }
    }

    /**
     * Connects all regions in a 2D matrix
     * @param t 2D matrix
     */
    private static void connectRegions(int[][] t){
        int sizex = t.length, sizey = t[0].length;
        boolean done = false;
        for(int i = 0; done == false; i++) {
            connectMainRegion(t, t[1][1]);
            for (int k = 1; k < sizex; k += 1) {
                for (int j = 1; j < sizey; j += 1) {
                    if(t[k][j] == -1){
                        t[k][j] = 0;
                    }
                    if (isConnector(t, k, j)) {
                        t[k][j] = -1;
                    }

                }
            }
            done = true;
            for (int k = 1; k < sizex; k += 1) {
                for (int j = 1; j < sizey; j += 1) {
                    if(t[k][j] > 0 && t[k][j] != t[1][1]){
                        done = false;
                    }
                }
            }
        }
    }

    private static void eraseColumns(int[][] t, boolean yes){
        int sizex = t.length, sizey = t[0].length;
        if(yes){
            for (int i = 1; i < sizex; i += 1) {
                for (int j = 1; j < sizey; j += 1) {
                    if(numOuts(t, i, j) == 4){
                        t[i][j] = 1;
                        fill(t, i, j, 1);
                    }
                }
            }
        }
    }

    /**
     * Erases dead ends from tha 2D matrix. A tile is considered a dead end if it only has one surrounding free tile
     * @param t 2D matrix
     * @param trim number of times to trim
     */
    private static void trim(int[][] t, int trim){

        int sizex = t.length, sizey = t[0].length;
        int[][] taux = new int[sizex][sizey];

        for (int i = 1; i < sizex; i += 1) {
            for (int j = 1; j < sizey; j += 1) {
                taux[i][j] = t[i][j];
            }
        }
        for(int k = 0; k < trim; k++){
            for (int i = 1; i < sizex; i += 1) {
                for (int j = 1; j < sizey; j += 1) {
                    if(numOuts(t, i, j) == 1){
                        taux[i][j] = 0;
                    }
                }
            }
            for (int i = 1; i < sizex; i += 1) {
                for (int j = 1; j < sizey; j += 1) {
                    t[i][j] = taux[i][j];
                }
            }
        }
    }

    /**
     * In the 2D matrix, removes walls that are surrounded by 3 empty spaces or more.
     * @param t 2D matrix
     * @param expand number of times to expand
     */
    private static void expand(int[][] t, int expand){

        int sizex = t.length, sizey = t[0].length;
        int[][] taux = new int[sizex][sizey];

        for (int i = 1; i < sizex; i += 1) {
            for (int j = 1; j < sizey; j += 1) {
                taux[i][j] = t[i][j];
            }
        }
        for(int k = 0; k < expand; k++){
            for (int i = 1; i < sizex; i += 1) {
                for (int j = 1; j < sizey; j += 1) {
                    if(numOuts(t, i, j) >= 3){
                        taux[i][j] = 1;
                        fill(t, i, j, 1);
                    }
                }
            }
            for (int i = 1; i < sizex; i += 1) {
                for (int j = 1; j < sizey; j += 1) {
                    t[i][j] = taux[i][j];
                }
            }
        }
    }

    /**
     * Recursive function. Given a point in a 2D matrix, tunnels 2 tiles in one of the cardinal directions at random if possible, then calls itself in the new point
     * in order to create a random maze to fill the empty space in the matrix
     * @param t 2D matrix
     * @param i x position to tunnel from
     * @param j y position to tunnel from
     */
    private static void tunnel(int[][] t, int i, int j){
        if(inBounds(t, i, j)){
            int[] options = new int[4];
            for(int k = 0; k < options.length; k++){
                options[k] = 1;
            }
            boolean done = false;
            while(!done) {
                if(options[0]+options[1]+options[2]+options[3] == 0){
                    done = true;
                }else {
                    int choice;
                    do {
                        choice = (int) (Math.random() * 4);
                    } while (options[choice] != 1);
                    options[choice] = 0;
                    switch (choice) {
                        case 0:
                            if (inBounds(t, i + 2, j) && numOuts(t, i + 2, j) <= 1 && t[i+2][j] != t[i][j]) {
                                t[i + 2][j] = t[i][j];
                                t[i + 1][j] = t[i][j];
                                done = true;
                                tunnel(t, i + 2, j);
                            }
                            break;
                        case 1:
                            if (inBounds(t, i - 2, j) && numOuts(t, i - 2, j) <= 1 && t[i-2][j] != t[i][j]) {
                                t[i - 2][j] = t[i][j];
                                t[i - 1][j] = t[i][j];
                                done = true;
                                tunnel(t, i - 2, j);
                            }
                            break;
                        case 2:
                            if (inBounds(t, i, j + 2) && numOuts(t, i, j + 2) <= 1 && t[i][j+2] != t[i][j]) {
                                t[i][j + 2] = t[i][j];
                                t[i][j + 1] = t[i][j];
                                done = true;
                                tunnel(t, i, j + 2);
                            }
                            break;
                        case 3:
                            if (inBounds(t, i, j - 2) && numOuts(t, i, j - 2) <= 1 && t[i][j-2] != t[i][j]) {
                                t[i][j - 2] = t[i][j];
                                t[i][j - 1] = t[i][j];
                                done = true;
                                tunnel(t, i, j - 2);
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * Given a 2D matrix and one position in it, returns the number of empty spaces adjacent to that tile in one of the 4 cardinal directions (0-4).
     * Returns -1 if the position is out of bounds.
     * @param t 2D matrix
     * @param xpos x position
     * @param ypos y position
     * @return number of empty spaces adjacent to the tile in one of the cardinal directions
     */
    private static int numOuts(int[][] t, int xpos, int ypos){
        int numOuts = 0;
        if(inBounds(t, xpos, ypos)) {
            if (inBounds(t, xpos + 1, ypos)) {
                if (t[xpos + 1][ypos] > 0) {
                    numOuts++;
                }
            }
            if (inBounds(t, xpos - 1, ypos)) {
                if (t[xpos - 1][ypos] > 0) {
                    numOuts++;
                }
            }
            if (inBounds(t, xpos, ypos + 1)) {
                if (t[xpos][ypos + 1] > 0) {
                    numOuts++;
                }
            }
            if (inBounds(t, xpos, ypos - 1)) {
                if (t[xpos][ypos - 1] > 0) {
                    numOuts++;
                }
            }
        }else{
            numOuts = -1;
        }
        return numOuts;
    }

    /**
     * Given a position in the 2D matrix, checks if that tile is a wall that is separating two different regions and is in a odd position in at least one of the two coordinates
     * @param t 2D matrix
     * @param i x position
     * @param j y position
     * @return boolean that indicates if the tile is a wall that is separating two different regions
     */
    private static boolean isConnector(int[][] t, int i, int j){
        int[] outs = new int[2];
        int index = 0;
        if(numOuts(t, i, j) == 2 && t[i][j] == 0){
            if(t[i+1][j] > 0){
                outs[index] = t[i+1][j];
                index++;
            }
            if(t[i-1][j] > 0){
                outs[index] = t[i-1][j];
                index++;
            }
            if(t[i][j+1] > 0){
                outs[index] = t[i][j+1];
                index++;
            }
            if(t[i][j-1] > 0){
                outs[index] = t[i][j-1];
                index++;
            }
            if(outs[0] != outs[1] && ((i%2==1)||(j%2==1))){
                return true;
            }
        }
        return false;
    }

    /**
     * Given a 2D matrix, ensures that connected regions have the same number
     * @param t 2D matrix
     */
    private static void floodFill(int[][] t){
        int sizex = t.length, sizey = t[0].length;
        for (int i = 1; i < sizex; i += 1) {
            for (int j = 1; j < sizey; j += 1) {
                if(inBounds(t, i, j) && t[i][j] > 0){
                    fill(t, i, j, t[i][j]);
                }
            }
        }
    }

    /**
     * Recursive function. Given a position in the 2D matrix and a value, changes the value of all non-wall adjacent tiles to that value, then calls itself on those tiles
     * Used to make two connected regions into one
     * @param t 2D matrix
     * @param i x position
     * @param j y position
     * @param v region value to fill with
     */
    private static void fill(int[][] t, int i, int j, int v){

        if (t[i + 1][j] > 0 && t[i+1][j] != v) {
            t[i + 1][j] = v;
            fill(t, i+1, j, v);
        }
        if (t[i - 1][j] > 0 && t[i-1][j] != v) {
            t[i - 1][j] = v;
            fill(t, i-1, j, v);
        }
        if (t[i][j+1] > 0 && t[i][j+1] != v) {
            t[i][j+1] = v;
            fill(t, i, j+1, v);
        }
        if (t[i][j-1] > 0 && t[i][j-1] != v) {
            t[i][j-1] = v;
            fill(t, i, j-1, v);
        }
    }

    /**
     * In the 2D matrix, looks for all the connectors between the main region and other regions and chooses one of them at random.
     * Then connects the main region and another region using that connector and discards other connectors between that two regions,
     * with a small chance of using them too.
     * Then fills tha main region so it assimilates the region it's been connected to.
     * @param t 2D matrix
     * @param region number of the region used as main region
     */
    private static void connectMainRegion(int[][] t, int region){
        int sizex = t.length, sizey = t[0].length;
        ArrayList<Point> connectors = new ArrayList<Point>(0);
        for (int i = 1; i < sizex; i += 1) {
            for (int j = 1; j < sizey; j += 1) {
                if(t[i][j] == region){
                    if(t[i+1][j] == -1){
                        connectors.add(new Point(i+1, j));
                    }
                    if(t[i-1][j] == -1){
                        connectors.add(new Point(i-1, j));
                    }
                    if(t[i][j+1] == -1){
                        connectors.add(new Point(i, j+1));
                    }
                    if(t[i][j-1] == -1){
                        connectors.add(new Point(i, j-1));
                    }
                }
            }
        }

        if(connectors.size() > 0) {
            for (Point p : connectors) {
                if(Math.random()*1000 > 999){
                    t[(int) p.getX()][(int) p.getY()] = region;
                }
            }
            int choice = (int) (Math.random() * connectors.size());
            Point out = connectors.get(choice);
            t[(int) out.getX()][(int) out.getY()] = region;
            fill(t, (int) out.getX(), (int) out.getY(), region);
        }

    }

    /**
     * Given a position in the 2D matrix, checks if it is within its usable boundaries, which means that its not in its borders,
     * the borders being placed in the higher odd number possible.
     * @param t 2D matrix
     * @param xpos x position
     * @param ypos y position
     * @return boolean answer
     */
    private static boolean inBounds(int[][] t, int xpos, int ypos){
        int sizex = t.length, sizey = t[0].length;
        return (xpos>=1&&ypos>=1&&xpos<sizex-2+sizex%2&&ypos<sizey-2+sizey%2);
    }

}
