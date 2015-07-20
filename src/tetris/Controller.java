package tetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener{
        
    public boolean state[];
    
    public static final int LEFT = 0, RIGHT = 1, DOWN = 2, DROP = 3, ROTATE = 4, PAUSE = 5;
    public Controller() {
        state = new boolean[6];
        for(int i = 0; i < state.length; i++) 
            state[i] = false;
    }
    
     @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
        switch (KeyEvent.getKeyText(e.getKeyCode())) {
            case "Left":
                state[LEFT] = true;
                break;
            case "Right":
                state[RIGHT] = true;
                break;
            case "Up":
                state[ROTATE] = true;
                break;
            case "Down":
                state[DOWN] = true;
                break;
            case "Space":
                state[DROP] = true;
                break;
            case "Escape":
                System.out.println("Encerrando o jogo");
                System.exit(0);
                break;
        }
    }
    
    @Override
    public void keyTyped (KeyEvent e) {
        
    }
    
    @Override
    public void keyReleased (KeyEvent e) {
        //System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
        switch (KeyEvent.getKeyText(e.getKeyCode())) {
            case "Down":
                state[DOWN] = false;
                break;
            case "P":
                state[PAUSE] = !state[PAUSE];
                if (state[PAUSE] == false)
                    for(int i = 0; i < state.length; i++)
                        state[i] = false;
                break;
        }
    }
}
