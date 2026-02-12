package engine.input;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Keyboard {
    private static Keyboard instance;
    private BitSet keysPressed;
    private BitSet keysJustPressed;
    private BitSet keysJustReleased;
    private Map<Character, Boolean> characterKeys;
    private InputEventDispatcher dispatcher;
    
    private Keyboard() {
        this.keysPressed = new BitSet(256);
        this.keysJustPressed = new BitSet(256);
        this.keysJustReleased = new BitSet(256);
        this.characterKeys = new HashMap<>();
        this.dispatcher = InputEventDispatcher.getInstance();
    }
    
    public static Keyboard getInstance() {
        if (instance == null) {
            instance = new Keyboard();
        }
        return instance;
    }
    
    public void keyPressed(KeyEvent.KeyCode keyCode, char keyChar, 
                           boolean shift, boolean ctrl, boolean alt, boolean meta) {
        int code = keyCode != null ? keyCode.getCode() : -1;
        
        if (code >= 0) {
            if (!keysPressed.get(code)) {
                keysJustPressed.set(code);
            }
            keysPressed.set(code);
            keysJustReleased.clear(code);
        }
        
        characterKeys.put(keyChar, true);
        
        KeyEvent event = new KeyEvent(InputEvent.Type.KEY_PRESSED, keyCode, keyChar, shift, ctrl, alt, meta);
        dispatcher.dispatchKeyEvent(event);
    }
    
    public void keyReleased(KeyEvent.KeyCode keyCode, char keyChar,
                           boolean shift, boolean ctrl, boolean alt, boolean meta) {
        int code = keyCode != null ? keyCode.getCode() : -1;
        
        if (code >= 0) {
            keysPressed.clear(code);
            keysJustPressed.clear(code);
            keysJustReleased.set(code);
        }
        
        characterKeys.put(keyChar, false);
        
        KeyEvent event = new KeyEvent(InputEvent.Type.KEY_RELEASED, keyCode, keyChar, shift, ctrl, alt, meta);
        dispatcher.dispatchKeyEvent(event);
    }
    
    public void keyTyped(char keyChar, boolean shift, boolean ctrl, boolean alt, boolean meta) {
        KeyEvent event = new KeyEvent(InputEvent.Type.KEY_TYPED, null, keyChar, shift, ctrl, alt, meta);
        dispatcher.dispatchKeyEvent(event);
    }
    
    public boolean isKeyPressed(KeyEvent.KeyCode key) {
        return key != null && keysPressed.get(key.getCode());
    }
    
    public boolean isKeyJustPressed(KeyEvent.KeyCode key) {
        return key != null && keysJustPressed.get(key.getCode());
    }
    
    public boolean isKeyJustReleased(KeyEvent.KeyCode key) {
        return key != null && keysJustReleased.get(key.getCode());
    }
    
    public boolean isKeyPressed(int keyCode) {
        return keyCode >= 0 && keyCode < keysPressed.size() && keysPressed.get(keyCode);
    }
    
    public boolean isKeyJustPressed(int keyCode) {
        return keyCode >= 0 && keyCode < keysJustPressed.size() && keysJustPressed.get(keyCode);
    }
    
    public boolean isKeyJustReleased(int keyCode) {
        return keyCode >= 0 && keyCode < keysJustReleased.size() && keysJustReleased.get(keyCode);
    }
    
    public boolean isCharacterTyped(char character) {
        return characterKeys.getOrDefault(character, false);
    }
    
    public void update() {
        // Clear just pressed and just released states
        keysJustPressed.clear();
        keysJustReleased.clear();
        
        // Clear character keys (they're only valid for the current frame)
        characterKeys.clear();
    }
    
    public void reset() {
        keysPressed.clear();
        keysJustPressed.clear();
        keysJustReleased.clear();
        characterKeys.clear();
    }
    
    // Convenience methods for common keys
    public boolean isUp() { return isKeyPressed(KeyEvent.KeyCode.UP); }
    public boolean isDown() { return isKeyPressed(KeyEvent.KeyCode.DOWN); }
    public boolean isLeft() { return isKeyPressed(KeyEvent.KeyCode.LEFT); }
    public boolean isRight() { return isKeyPressed(KeyEvent.KeyCode.RIGHT); }
    public boolean isSpace() { return isKeyPressed(KeyEvent.KeyCode.SPACE); }
    public boolean isEnter() { return isKeyPressed(KeyEvent.KeyCode.ENTER); }
    public boolean isEscape() { return isKeyPressed(KeyEvent.KeyCode.ESCAPE); }
    public boolean isShift() { return isKeyPressed(KeyEvent.KeyCode.SHIFT); }
    public boolean isCtrl() { return isKeyPressed(KeyEvent.KeyCode.CTRL); }
    public boolean isAlt() { return isKeyPressed(KeyEvent.KeyCode.ALT); }
    
    public boolean isUpJustPressed() { return isKeyJustPressed(KeyEvent.KeyCode.UP); }
    public boolean isDownJustPressed() { return isKeyJustPressed(KeyEvent.KeyCode.DOWN); }
    public boolean isLeftJustPressed() { return isKeyJustPressed(KeyEvent.KeyCode.LEFT); }
    public boolean isRightJustPressed() { return isKeyJustPressed(KeyEvent.KeyCode.RIGHT); }
    public boolean isSpaceJustPressed() { return isKeyJustPressed(KeyEvent.KeyCode.SPACE); }
    public boolean isEnterJustPressed() { return isKeyJustPressed(KeyEvent.KeyCode.ENTER); }
    public boolean isEscapeJustPressed() { return isKeyJustPressed(KeyEvent.KeyCode.ESCAPE); }
    
    // Character methods
    public boolean isA() { return isKeyPressed(KeyEvent.KeyCode.A); }
    public boolean isB() { return isKeyPressed(KeyEvent.KeyCode.B); }
    public boolean isC() { return isKeyPressed(KeyEvent.KeyCode.C); }
    public boolean isD() { return isKeyPressed(KeyEvent.KeyCode.D); }
    public boolean isE() { return isKeyPressed(KeyEvent.KeyCode.E); }
    public boolean isF() { return isKeyPressed(KeyEvent.KeyCode.F); }
    public boolean isG() { return isKeyPressed(KeyEvent.KeyCode.G); }
    public boolean isH() { return isKeyPressed(KeyEvent.KeyCode.H); }
    public boolean isI() { return isKeyPressed(KeyEvent.KeyCode.I); }
    public boolean isJ() { return isKeyPressed(KeyEvent.KeyCode.J); }
    public boolean isK() { return isKeyPressed(KeyEvent.KeyCode.K); }
    public boolean isL() { return isKeyPressed(KeyEvent.KeyCode.L); }
    public boolean isM() { return isKeyPressed(KeyEvent.KeyCode.M); }
    public boolean isN() { return isKeyPressed(KeyEvent.KeyCode.N); }
    public boolean isO() { return isKeyPressed(KeyEvent.KeyCode.O); }
    public boolean isP() { return isKeyPressed(KeyEvent.KeyCode.P); }
    public boolean isQ() { return isKeyPressed(KeyEvent.KeyCode.Q); }
    public boolean isR() { return isKeyPressed(KeyEvent.KeyCode.R); }
    public boolean isS() { return isKeyPressed(KeyEvent.KeyCode.S); }
    public boolean isT() { return isKeyPressed(KeyEvent.KeyCode.T); }
    public boolean isU() { return isKeyPressed(KeyEvent.KeyCode.U); }
    public boolean isV() { return isKeyPressed(KeyEvent.KeyCode.V); }
    public boolean isW() { return isKeyPressed(KeyEvent.KeyCode.W); }
    public boolean isX() { return isKeyPressed(KeyEvent.KeyCode.X); }
    public boolean isY() { return isKeyPressed(KeyEvent.KeyCode.Y); }
    public boolean isZ() { return isKeyPressed(KeyEvent.KeyCode.Z); }
    
    // Number methods
    public boolean isNum0() { return isKeyPressed(KeyEvent.KeyCode.NUM_0); }
    public boolean isNum1() { return isKeyPressed(KeyEvent.KeyCode.NUM_1); }
    public boolean isNum2() { return isKeyPressed(KeyEvent.KeyCode.NUM_2); }
    public boolean isNum3() { return isKeyPressed(KeyEvent.KeyCode.NUM_3); }
    public boolean isNum4() { return isKeyPressed(KeyEvent.KeyCode.NUM_4); }
    public boolean isNum5() { return isKeyPressed(KeyEvent.KeyCode.NUM_5); }
    public boolean isNum6() { return isKeyPressed(KeyEvent.KeyCode.NUM_6); }
    public boolean isNum7() { return isKeyPressed(KeyEvent.KeyCode.NUM_7); }
    public boolean isNum8() { return isKeyPressed(KeyEvent.KeyCode.NUM_8); }
    public boolean isNum9() { return isKeyPressed(KeyEvent.KeyCode.NUM_9); }
}