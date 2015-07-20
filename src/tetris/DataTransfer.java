package tetris;

public final class DataTransfer {
    private static final DataTransfer INSTANCE = new DataTransfer();
    //PLAYER DATA
    private static final int STATUS = 0, ENEMY = 1, LINES_TO_RECEIVE = 2;
    //STATUS
    public static final int WAITING_FOR_AN_ENEMY = 0, PLAYING = 1, DEFEATED = 2, WINNER = 3;
            
    private int limit, playerCount, playerData[][];
    private boolean instantiated = false;
    
    private void init() {
        if(!instantiated) {
            limit = 100;
            playerCount = 0;
            playerData = new int[limit][3];
            for (int i = 0; i < limit; i++)
                for(int j = 0; j < 3; j++)
                    playerData[i][j] = -1;
            instantiated = true;
        }
    }
    
    public static DataTransfer getInstance() {
        return INSTANCE;
    }
    
    public int addPlayer() {
        init();
        if(playerCount < limit) {
            int playerId = -1, state = 0, enemy = -1, linesToReceive = 0;
            for(int i = 0; i < limit; i++) {
                if (playerData[i][STATUS] == -1) {
                    playerId = i;
                    playerData[playerId] = new int[]{state, enemy, linesToReceive};
                    playerData[i][STATUS] = WAITING_FOR_AN_ENEMY;
                    break;
                }
            }
            if (playerId > -1) {
                findEnemy(playerId);
                playerCount++;
                return playerId;
            }
        } 
        return -1;
    }
    
    public int findEnemy(int playerId) {
        if(playerId > -1) {
            for(int i = 0; i < limit; i++) {
                if(i == playerId) continue;
                if (playerData[i][STATUS] == 0) {
                    playerData[i][STATUS] = 1;
                    playerData[i][ENEMY] = playerId;
                    playerData[playerId][STATUS] = 1;
                    playerData[playerId][ENEMY] = i;
                    return i;
                } else {
                    continue;
                }
            }
        }
        return -1;
    }
    
    public void freePlayer (int playerId) {
        if(playerId > -1) {
            int enemy = playerData[playerId][ENEMY];
            if (enemy > -1) {
                playerData[enemy][STATUS] = WINNER;
                playerData[enemy][ENEMY] = -1;
            }
            for(int j = 0; j < 3; j++)
                playerData[playerId][j] = -1;
            playerCount--;
        }
    }
    
    public int getEnemy(int playerId) {
        if(playerId > -1) {
            return playerData[playerId][ENEMY];
        }
        return -1;
    }
    
    public int getState(int playerId) {
        if(playerId > -1) {
            return playerData[playerId][STATUS];
        } else {
            return 0;
        }
    }
    
    public int receiveLines(int playerId) {
        if(playerId > -1) {
            int lines = playerData[playerId][LINES_TO_RECEIVE];
            playerData[playerId][LINES_TO_RECEIVE] = 0;
            return lines;
        }
        return 0;
    }
    
    public void sendLines(int playerId, int lines) {
        if(playerId > -1) {
            int enemy = playerData[playerId][ENEMY];
            if(enemy > -1) playerData[enemy][LINES_TO_RECEIVE] += lines;
        }
    }
    
    public void setState(int playerId, int state) {
        if(playerId > -1) playerData[playerId][STATUS]  = state;
    }
}
