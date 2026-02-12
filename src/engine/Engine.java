package engine;

public class Engine {
    private static Engine instance;
    private Game currentGame;
    
    private Engine() {
        // Private constructor for singleton
    }
    
    public static Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }
    
    public Game getCurrentGame() { return currentGame; }
    
    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }
    
    public void startGame(String title) {
        Game game = new Game(title);
        setCurrentGame(game);
        game.start();
    }
    
    public void stopGame() {
        if (currentGame != null) {
            currentGame.stop();
        }
    }
    
    public Game createGame(String title) {
        return new Game(title);
    }
}