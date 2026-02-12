package engine;

import engine.collision.*;
import engine.input.InputManager;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private String title;
    private List<Scene> scenes;
    private Scene currentScene;
    private boolean running;
    private double targetFPS;
    private InputManager inputManager;
    
    public Game(String title) {
        this.title = title;
        this.scenes = new ArrayList<>();
        this.running = false;
        this.targetFPS = 60.0;
        this.inputManager = InputManager.getInstance();
    }
    
    public String getTitle() { return title; }
    public List<Scene> getScenes() { return new ArrayList<>(scenes); }
    public Scene getCurrentScene() { return currentScene; }
    public boolean isRunning() { return running; }
    public double getTargetFPS() { return targetFPS; }
    
    public void setTargetFPS(double targetFPS) { this.targetFPS = targetFPS; }
    
    public void addScene(Scene scene) {
        scenes.add(scene);
        if (currentScene == null) {
            currentScene = scene;
        }
    }
    
    public void removeScene(Scene scene) {
        scenes.remove(scene);
        if (currentScene == scene) {
            currentScene = scenes.isEmpty() ? null : scenes.get(0);
        }
    }
    
    public Scene getScene(String name) {
        for (Scene scene : scenes) {
            if (scene.getName().equals(name)) {
                return scene;
            }
        }
        return null;
    }
    
    public void setCurrentScene(Scene scene) {
        if (scenes.contains(scene)) {
            currentScene = scene;
        }
    }
    
    public void setCurrentScene(String name) {
        Scene scene = getScene(name);
        if (scene != null) {
            setCurrentScene(scene);
        }
    }
    
    public void start() {
        running = true;
        inputManager.initialize();
        System.out.println("Game '" + title + "' started!");
    }
    
    public void stop() {
        running = false;
        inputManager.shutdown();
        System.out.println("Game '" + title + "' stopped!");
    }
    
    public void update(double deltaTime) {
        if (!running || currentScene == null) return;
        
        // Update input system
        inputManager.update();
        
        // Update scene
        currentScene.update(deltaTime);
    }
    
    public void gameLoop() {
        if (!running) {
            start();
        }
        
        final long targetTime = (long) (1_000_000_000.0 / targetFPS);
        long lastTime = System.nanoTime();
        
        while (running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            lastTime = currentTime;
            
            update(deltaTime);
            
            long elapsedTime = System.nanoTime() - currentTime;
            long sleepTime = targetTime - elapsedTime;
            
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    public int getTotalGameObjectCount() {
        int count = 0;
        for (Scene scene : scenes) {
            count += scene.getGameObjectCount();
        }
        return count;
    }
    
    public InputManager getInputManager() {
        return inputManager;
    }
}