package engine;

public abstract class Component {
    protected GameObject gameObject;
    
    public GameObject getGameObject() { return gameObject; }
    public void setGameObject(GameObject gameObject) { this.gameObject = gameObject; }
    
    public abstract void update(double deltaTime);
    public abstract void start();
    public abstract void destroy();
}