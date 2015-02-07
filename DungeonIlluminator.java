import java.awt.*;
import java.util.ArrayList;

/**
 * Created by FerranRuiz on 06/02/2015.
 */
public class DungeonIlluminator {

    /**
     * Given a 2D matrix and an ArrayList of Points (light sources), returns a 2D matrix representing the light level on every tile of the 2D matrix
     * @param t 2D matrix
     * @param sources Arraylist of LightSources.
     * @param baselight Base level of light with no sources
     * @return 2D matrix representing the light level on every tile of the original 2D matrix
     */
    public static int[][] illuminate(int[][] t, ArrayList<LightSource> sources, int baselight){

        int sizex = t.length, sizey = t[0].length;
        int[][] tlight = new int[sizex][sizey];

        for (int i = 0; i < sizex; i += 1) {
            for (int j = 0; j < sizey; j += 1) {
                tlight[i][j] = baselight;
            }
        }

        for(LightSource ls:sources){
            if(inBounds(t, ls.getX(), ls.getY())) {
                fillLight(t, tlight, ls.getX(), ls.getY(), ls.getIntensity());
            }
        }
        return(tlight);
    }

    /**
     * Recursive function. Fills surrounding tiles with light, then calls itself on that tiles decreasing the light value by one.
     * @param t 2D matrix
     * @param tlight light values 2D matrix
     * @param i x position
     * @param j y position
     * @param light ligth intensity
     */
    private static void fillLight(int[][] t, int[][] tlight, int i, int j, int light){

        tlight[i][j] = light;

        if (t[i + 1][j] == 1 && tlight[i+1][j] < light) {
            tlight[i+1][j] = light;
            fillLight(t, tlight, i+1, j, light-1);
        }
        if (t[i - 1][j] == 1 && tlight[i-1][j] < light) {
            tlight[i-1][j] = light;
            fillLight(t, tlight, i-1, j, light-1);
        }
        if (t[i][j+1] == 1 && tlight[i][j+1] < light) {
            tlight[i][j+1] = light;
            fillLight(t, tlight, i, j+1, light-1);
        }
        if (t[i][j-1] == 1 && tlight[i][j-1] < light) {
            tlight[i][j-1] = light;
            fillLight(t, tlight, i, j-1, light-1);
        }
        if (t[i + 1][j] == 0 && tlight[i+1][j] < light) {
            tlight[i+1][j] = light;
        }
        if (t[i - 1][j] == 0 && tlight[i-1][j] < light) {
            tlight[i-1][j] = light;
        }
        if (t[i][j+1] == 0 && tlight[i][j+1] < light) {
            tlight[i][j+1] = light;
        }
        if (t[i][j-1] == 0 && tlight[i][j-1] < light) {
            tlight[i][j-1] = light;
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
