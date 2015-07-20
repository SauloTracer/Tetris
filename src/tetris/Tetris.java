/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Saulo
 */
public class Tetris extends Canvas implements Runnable {

    public static final int WIDTH = 280, HEIGHT = 620, DOWN = 0, LEFT = -1, RIGHT = 1;
    private Image[] blocks;
    private Shape piece, nextPiece;
    int level, speed, speedBoost, lines, linesToNextLevel, score, grid[][], matrix[][], teste;
    long time, currentTime, gravity;
    boolean running, loaded, online, lost;
    Controller controls;
    Socket socket;
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        
        JMenuBar menu = new JMenuBar();
        menu.setBounds(0,0,WIDTH, 25);
        
        JMenu file = new JMenu("Arquivo");
        
        JMenuItem info = new JMenuItem("Instruções");
        
        info.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Code for new game
                System.out.println("Exibindo instruções");
                String strTitle, strLeft, strRight, strUp, strDown, strSpace, strP, strEsc;
                
                strTitle = "Controles:";
                strLeft = "<- (Seta para a esquerda): Move as peças para a esquerda.";
                strRight = "-> (Seta para a direita): Move as peças para a direita.";
                strDown = "\\/ (Seta para baixo): Acelera a descida das peças.";
                strUp = "/\\ (Seta para cima): Rotaciona a peça.";
                strSpace = "[_____] (Barra de espaço): Hard Drop";
                strP = "P (Letra P): Pausa o jogo.";
                strEsc = "ESC (Tecla ESC): Fecha o jogo";
                
                final JFrame alert = new JFrame("Instruções");
                alert.setSize(400, 400);
                alert.setLayout(null);
                alert.setResizable(false);
                alert.setLocationRelativeTo(null);
                
                JLabel lblTitle = new JLabel(strTitle);
                lblTitle.setBounds(10, 10, 100, 26);
                alert.add(lblTitle);
                
                JLabel lblLeft = new JLabel(strLeft);
                lblLeft.setBounds(10, 35, 380, 26);
                alert.add(lblLeft);
                
                JLabel lblRight = new JLabel(strRight);
                lblRight.setBounds(10, 50, 380, 26);
                alert.add(lblRight);
                
                JLabel lblDown = new JLabel(strDown);
                lblDown.setBounds(10, 75, 380, 26);
                alert.add(lblDown);
                
                JLabel lblUp = new JLabel(strUp);
                lblUp.setBounds(10, 100, 380, 26);
                alert.add(lblUp);
                
                JLabel lblSpace = new JLabel(strSpace);
                lblSpace.setBounds(10, 125, 380, 26);
                alert.add(lblSpace);
                
                JLabel lblP = new JLabel(strP);
                lblP.setBounds(10, 150, 380, 26);
                alert.add(lblP);
                
                JLabel lblEsc = new JLabel(strEsc);
                lblEsc.setBounds(10, 175, 380, 26);
                alert.add(lblEsc);
                
                alert.setVisible(true);
                        
            }
        });
        
        JMenuItem exit = new JMenuItem("Sair");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Code for exiting the game
                System.out.println("Encerrando jogo");
                System.exit(0);
            }
        });
        
        file.add(info); 
        file.add(exit);
        
        Tetris t = new Tetris();
        t.setBounds(0,25,WIDTH, HEIGHT-25);
        
        menu.add(file);
        
        frame.add(menu);
        frame.add(t);
        
        frame.setVisible(true);
        
        t.start(); //t = Tetris
    }
    
    public void start() {
        Thread t = new Thread(this);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }
    
    @Override
    public void run() {
        init();
        loaded = false;
        while (running) {
            time = System.currentTimeMillis();

            BufferStrategy buffer = getBufferStrategy();
            if (buffer == null) {
               createBufferStrategy(3);
               continue;
            }
            Graphics2D g = (Graphics2D)buffer.getDrawGraphics();
            if (getState() != 1) loaded = false;
            if (loaded) update(); 

            render(g);
            buffer.show();
            loaded = true;
            time = System.currentTimeMillis()-time;
            gravity += time;
            if(time < 30) {
                try {
                    Thread.sleep(30-time);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void init() {
        grid = new int[20][10];
        piece = null;
        genNextPiece();        
        
        level = 1;
        score = 0;
        lines = 0;
        linesToNextLevel = 10;
        speed = 2;
        
        controls = new Controller();
        this.addKeyListener(controls);
        requestFocus();
        
        try {
            blocks = ImageLoader.loadImage("/blocks.png", 25);
        } catch (IOException e) {
            System.out.println("Erro ao carregar blocos." + e.getMessage());
            System.exit(1);
        }

        for(int gridLine = 0; gridLine < 20; gridLine++)
            for(int gridCol = 0; gridCol < 10; gridCol++)
                grid[gridLine][gridCol] = 0;
        try {
            socket = new Socket("localhost",7375);        
        } catch (UnknownHostException ex) {
            System.out.println("Erro. Servidor offline. Executar TetrisServer.java" + ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Erro. Servidor offline. Executar TetrisServer.java" + ex.getMessage());
            System.exit(1);
        }
        running = true;
    }
    
    public void update() {
        if (!controls.state[Controller.PAUSE]) {
            if (piece == null) {
                piece = nextPiece;
                matrix = piece.getShape();
                piece.setPosition((int)(((10+matrix[0].length)-1)/2), 1-matrix.length);
                genNextPiece();
            } else {
                matrix = piece.getShape();
            }

            speedBoost = (controls.state[Controller.DOWN]) ? 20 : 0;
            if(controls.state[Controller.DROP]) hardDrop();
            if(controls.state[Controller.LEFT]) move(LEFT);
            if(controls.state[Controller.RIGHT]) move(RIGHT);
            if(controls.state[Controller.ROTATE]) rotate();

            if(gravity >= 20 - (level+speedBoost)) {
                move(DOWN);
                gravity = 0;
            }
            
            getEnemyLines();
        }
    }
    
    public void render (Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.PLAIN, 20));
        
        if(loaded) {
            g.drawString("Score: " + score + "  Level: " + level + "  Lines: " + linesToNextLevel, 10, 26);
            drawGrid(g);
            if(piece != null)drawPiece(g);
        } else {
            int state = getState();
            if(state == 3) {
                g.drawString(".:!You Won!:.", 75, 26);
                g.drawString("Score: " + score, 10, 60);
                g.drawString("Level: " + level, 10, 90);
                g.drawString("Lines cleared: " + lines, 10, 120);
            } else {
                if (lost) {
                    g.drawString(".:!Game Over!:.", 75, 26);
                    g.drawString("Score: " + score, 10, 60);
                    g.drawString("Level: " + level, 10, 90);
                    g.drawString("Lines cleared: " + lines, 10, 120);
                } else {
                    g.drawString("Aguardando oponente...", 10, 26);
                }
            }
        }
    }
    
    public void drawGrid(Graphics2D g) {
        int x, y, linhas, colunas, color;
        for (linhas = 0; linhas < grid.length; linhas++) {
            for(colunas = 0; colunas < grid[linhas].length; colunas++) {
                x = 10 + colunas*25;
                y = 80 + linhas*25;
                color = grid[linhas][colunas]-1;
                if (color >= 0) {
                    g.drawImage(blocks[color], x, y, null);
                }
            }
        }
    }
    
    public void drawPiece(Graphics2D g) {
        for(int i = 0; i < matrix.length; i++)
            for(int j = 0; j < matrix[i].length; j++)
                if (piece.y + i > 0)
                    if (matrix[i][j] > 0)
                        g.drawImage(blocks[piece.color-1], 10 + ((piece.x + j) * 25), 80 + ((piece.y + i) * 25), null);
    }
    
    public boolean canMove(int direction) {
        if (piece == null) return false;
        
        int x, y, w, h;
        
        x = piece.x;
        y = piece.y;
        h = matrix.length;
        w = matrix[0].length;
        
        switch (direction) {
            case LEFT:
                if (x < 1) return false;
                for(int l = 0; l < h; l++)
                    if(y+l > -1)
                        for(int c = 0; c < w; c++)
                            if(matrix[l][c] > 0)
                                if(grid[y + l][x + c - 1] > 0) {
                                    controls.state[Controller.LEFT] = false;
                                    return false;
                                }
                break;
            case DOWN:
                if (y + h >  19) return false;
                for(int i = h-1; i > -1; i --)
                    if (y+i+1 > -1)
                        for(int c = 0; c < w; c++) 
                            if (matrix[i][c] > 0) {
                                if (grid[y+i+1][c+x] > 0) {
                                    if ((y+i+1) == 0) gameOver();
                                        return false;
                                }
                            }
                break;
            case RIGHT:
                if (x + w-1 > 8) return false;
                for(int l = 0; l < h; l++)
                    if(y+l > -1)
                        for(int c = 0; c < w; c++)
                            if(matrix[l][c] > 0)
                                if(grid[y + l][x + c + 1] > 0) {
                                    controls.state[Controller.RIGHT] = false;
                                    return false;
                                }
                break;
            default:
                return false;
        }
        
        return true;
    }
    
    public boolean canRotate() {
        if (piece == null) return false;
        
        Shape p = new Shape();
        p.init();
        p.shape = piece.shape;
        p.setPosition(piece.x, piece.y);
        p.rotation = piece.rotation;
        p.color = piece.color;
        p.rotate();
        
        int[][] rotated;
        rotated = p.getShape();
        
        if(p.x + rotated[0].length-1 > 9) return false;
        
        for(int i = 0; i < rotated.length; i++)
            for(int j = 0; j < rotated[i].length; j++)
                if (p.y + i > -1)
                    if (rotated[i][j] > 0)
                        if (grid[p.y + i][p.x + j] > 0) return false;
        
        return true;
    }
    
    public void move(int direction) {
        if(canMove(direction)) {
            switch (direction) {
                case DOWN: piece.setPosition(piece.x, piece.y+1); break;
                case LEFT: 
                    piece.setPosition(piece.x-1, piece.y); 
                    controls.state[Controller.LEFT] = false;
                    controls.state[Controller.RIGHT] = false;
                    break;
                case RIGHT: 
                    piece.setPosition(piece.x+1, piece.y); 
                    controls.state[Controller.RIGHT] = false;
                    controls.state[Controller.LEFT] = false;
                    break;
            }
        } else {
            if(direction == DOWN) bindToGrid();
        }
    }
    
    public void bindToGrid() {
        //Transfere peça pro grid.
        for(int l = 0; l < matrix.length; l++) 
            for(int c = 0; c < matrix[l].length; c++) 
                if(piece.y + l > -1)
                    if(matrix[l][c] > 0)
                        grid[piece.y+l][piece.x+c] = matrix[l][c];
        int l = checkLines();
        piece.y -= l;
        if(piece.y < 0) gameOver();
        piece = null;
    }
    
    public void genNextPiece() {
        nextPiece = null;
        nextPiece = new Shape();
        nextPiece.init();
        nextPiece.shape = (int)(7*Math.random());
        nextPiece.color = nextPiece.shape + 1;
        nextPiece.rotation = 0;
    }
    
    public int checkLines() {
        boolean full;
        Integer fullLines = 0;
        for(int gridLine = 0; gridLine < 20; gridLine++) {
            full = true;
            for(int gridCol = 0; gridCol < 10; gridCol++) {
                if (grid[gridLine][gridCol] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                fullLines++;
                score+= 100*level;
                linesToNextLevel--;
                moveLinesDown(gridLine-1);
            }
        } 
        lines += fullLines;
        sendLines(fullLines);
        if(linesToNextLevel <= 0) levelUp();
        
        return fullLines;
    }
    
    public void moveLinesDown(int line) {
        if(line >= -1 && line <= 18) {
            if (line == -1) {
                grid[0] = new int[] {0,0,0,0,0,0,0,0,0,0};
            } else {
                grid[line+1] = grid[line];
                moveLinesDown(line-1);
            }
        }
    }
    
    public void moveLinesUp(int line) {
        if(line < grid.length && line >= 0 ) {
            if(line == 0) {
                for(int i = 0; i < grid[0].length; i++)
                    if(grid[0][i] > 0 ) 
                        gameOver();
            } else {
                moveLinesUp(line-1);
                grid[line-1] = grid[line];
            }
        }
    }
    
    public void addLinesDown(int l) {
        if(l > 0) {
            for(int line = 0; line < l; line++) {
                moveLinesUp(19);
                grid[19] = new int[] {1,1,1,1,1,1,1,1,1,1};
                int blank;
                for(int i = 0; i < level; i++) {
                    blank = (int)(10*Math.random());
                    grid[19][blank] = 0;
                }
            }
        }
    }
    
    public void hardDrop() {
        while(canMove(DOWN))
            move(DOWN);
        controls.state[Controller.DROP] = false;
    }
    
    public void gameOver() {
        running = false;
        System.out.println("Game Over");
        System.out.println("Final score: " + score);
        System.out.println("Lines cleared: " + lines);
        System.out.println("Final level: " + level);
        
        try {
            System.out.println("Fechando socket.");
            write("close");            
        } catch(IOException ex) {
            //do nothing
            System.out.println("Erro de comunicação com o socket.");
        }
        
        lost = true;
    }
    
    public void rotate() {
        if(canRotate()) {
            piece.rotate();
            matrix = piece.getShape();
        }
        controls.state[Controller.ROTATE] = false;
    }
    
    public void levelUp() {
        level++;
        speed = level * 2;
        linesToNextLevel = 10 + level;
    }
    
    public void getEnemyLines() {
        String msg;
        try {
            write("getLines");
            msg = read();
            System.out.println("Received lines: " + msg);
            int l = Integer.parseInt(msg);
            addLinesDown(l);
        } catch(IOException ex) {
            //do nothing
            System.out.println("Erro de comunicação com o socket.");
        }
    }
    
    public void sendLines(Integer l) {
        //if(l > 0) {
            String msg;
            try {
                write("sendLines");
                msg = read();
                System.out.println("Answer: " + msg);
                if(msg.equals("howMany")) write(l.toString());
            } catch(IOException ex) {
                System.out.println("Erro de comunicação com o socket.");
            }
        //}
    }
    
    public int getState() {
        String msg;
        try {
            write("getState");
            msg = read();
            System.out.println("State: " + msg);
            return Integer.parseInt(msg);
        } catch(IOException ex) {
            System.out.println("Erro de comunicação com o socket.");
        }
        return 0;
    }
    
    public void gameOverAnimation(Graphics2D g) throws InterruptedException {
        for(int i = 19; i > -1; i--) {
            grid[i] = new int[] {1,1,1,1,1,1,1,1,1,1};
            render(g);
            Thread.sleep(300);
        }
    }
    
    public void write(String message) throws IOException {
        DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
        ostream.writeUTF(message);
        ostream.flush();
    }
    
    public String read() throws IOException {
        DataInputStream istream = new DataInputStream(socket.getInputStream());
        return istream.readUTF();
    }
    
}
