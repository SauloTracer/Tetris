package tetris;

public class Shape {
    
    int shapeMatrix[][][][]; //shape - rotação - linha - coluna
    int x, y, color, rotation, shape;
    
    public void init() {        
        rotation = 0;
        shapeMatrix = new int[7][4][][];
        
        /* Shape I */
            /*Rotation 0*/
            shapeMatrix[0][0] = new int[][]{
                {1},
                {1},
                {1},
                {1}
            };      
            /*Rotation 1*/
            shapeMatrix[0][1] = new int[][]{
                {1,1,1,1}
            };
            /*Rotation 2*/
            shapeMatrix[0][2] = shapeMatrix[0][0];
            /*Rotation 3*/
            shapeMatrix[0][3] = shapeMatrix[0][1];
        /* Fim Shape I */
            
        /* Shape J */
            /*Rotation 0*/
            shapeMatrix[1][0] = new int[][]{
                {0, 2},
                {0, 2},
                {2, 2}
            };
            /*Rotation 1*/
            shapeMatrix[1][1] = new int[][] {
                {2, 0, 0},
                {2, 2, 2}
            };
            /*Rotation 2*/
            shapeMatrix[1][2] = new int[][]{
                {2, 2},
                {2, 0},
                {2, 0}
            };
            /*Rotation 3*/
            shapeMatrix[1][3] = new int[][] {
                {2, 2, 2},
                {0, 0, 2}
            };
        /* Fim Shape J */
            
        /* Shape L*/
            /*Rotation 0*/
            shapeMatrix[2][0] = new int[][]{
                {3, 0},
                {3, 0},
                {3, 3}
            };
            /*Rotation 1*/
            shapeMatrix[2][1] = new int[][] {
                {3, 3, 3},
                {3, 0, 0}
            };
            /*Rotation 2*/
            shapeMatrix[2][2] = new int[][]{
                {3, 3},
                {0, 3},
                {0, 3}
            };
            /*Rotation 3*/
            shapeMatrix[2][3] = new int[][] {
                {0, 0, 3},
                {3, 3, 3}
            };
        /* Fim Shape L */
            
        /* Shape O */
            /*Rotation 0*/
            shapeMatrix[3][0] = new int[][]{
                {4, 4},
                {4, 4}
            };
            /*Rotation 1*/
            shapeMatrix[3][1] = shapeMatrix[3][0];
            /*Rotation 2*/
            shapeMatrix[3][2] = shapeMatrix[3][0];
            /*Rotation 3*/
            shapeMatrix[3][3] = shapeMatrix[3][0];
        /* Fim Shape O */
            
        /* Shape S */
            /*Rotation 0*/
            shapeMatrix[4][0] = new int[][]{
                {0, 5, 5},
                {5, 5, 0}
            };
            /*Rotation 1*/
            shapeMatrix[4][1] = new int[][] {
                {5, 0},
                {5, 5},
                {0, 5}
            };            
            /*Rotation 2*/
            shapeMatrix[4][2] = shapeMatrix[4][0];
            /*Rotation 3*/
            shapeMatrix[4][3] = shapeMatrix[4][1];
        /* Fim Shape S */
        
        /* Shape T */
            /*Rotation 0*/
            shapeMatrix[5][0] = new int[][]{
                {0, 6, 0},
                {6, 6, 6}
            };
            /*Rotation 1*/
            shapeMatrix[5][1] = new int[][]{
                {6, 0},
                {6, 6},
                {6, 0}
            };
            /*Rotation 2*/
            shapeMatrix[5][2] = new int[][]{
                {6, 6, 6},
                {0, 6, 0}
            };
            /*Rotation 3*/
            shapeMatrix[5][3] = new int[][]{
                {0, 6},
                {6, 6},
                {0, 6}
            };
        /* Emd Shape T */
            
        /* Shape Z */
            /*Rotation 0*/
            shapeMatrix[6][0] = new int[][]{
                {7, 7, 0},
                {0, 7, 7}
            };
            /*Rotation 1*/
            shapeMatrix[6][1] = new int[][] {
                {0, 7},
                {7, 7},
                {7, 0}
            };            
            /*Rotation 2*/
            shapeMatrix[6][2] = shapeMatrix[6][0];
            /*Rotation 3*/
            shapeMatrix[6][3] = shapeMatrix[6][1];
        /* Fim Shape Z */
    }
    
    public Shape(){
        init();
    }
    
    public int[][] newShape (int shape, int x, int y) {
        this.setPosition(x, y);
        switch (shape) {
            case 0: //i
                this.color = 1;
                this.shape = 0;
                return shapeMatrix[0][this.rotation];
            case 1: //'j'
                this.color = 2;
                this.shape = 1;
                return shapeMatrix[1][this.rotation];
            case 2: //'l'
                this.color = 3;
                this.shape = 2;
                return shapeMatrix[2][this.rotation];
            case 3: //'o'
                this.color = 4;
                this.shape = 3;
                return shapeMatrix[3][this.rotation];
            case 4: //'s'
                this.color = 5;
                this.shape = 4;
                return shapeMatrix[4][this.rotation];
            case 5: //'t'
                this.color = 6;
                this.shape = 5;
                return shapeMatrix[5][this.rotation];
            case 6: //'z'
                this.color = 7;
                this.shape = 6;
                return shapeMatrix[6][this.rotation];
            default:
                this.color = 0;
                this.shape = -1;
                return null;
        }
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void rotate() {
        rotation+=1;
        if(rotation >= 4) rotation = 0;
    }
    
    public int[][] getShape() {
        return shapeMatrix[this.shape][this.rotation];
    }
}
