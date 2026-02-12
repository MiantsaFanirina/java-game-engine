package engine.input;

import engine.collision.Vector2D;

public class KeyEvent extends InputEvent {
    public enum KeyCode {
        // Letters
        A(65), B(66), C(67), D(68), E(69), F(70), G(71), H(72), I(73), J(74),
        K(75), L(76), M(77), N(78), O(79), P(80), Q(81), R(82), S(83), T(84),
        U(85), V(86), W(87), X(88), Y(89), Z(90),
        
        // Numbers
        NUM_0(48), NUM_1(49), NUM_2(50), NUM_3(51), NUM_4(52),
        NUM_5(53), NUM_6(54), NUM_7(55), NUM_8(56), NUM_9(57),
        
        // Special keys
        SPACE(32), ENTER(13), BACKSPACE(8), TAB(9), ESCAPE(27),
        SHIFT(16), CTRL(17), ALT(18), META(157),
        
        // Arrow keys
        LEFT(37), UP(38), RIGHT(39), DOWN(40),
        
        // Function keys
        F1(112), F2(113), F3(114), F4(115), F5(116), F6(117), F7(118), F8(119),
        F9(120), F10(121), F11(122), F12(123),
        
        // Other
        HOME(36), END(35), PAGE_UP(33), PAGE_DOWN(34),
        INSERT(45), DELETE(46), PRINT_SCREEN(154),
        SCROLL_LOCK(145), PAUSE(19),
        
        // Numpad
        NUMPAD_0(96), NUMPAD_1(97), NUMPAD_2(98), NUMPAD_3(99), NUMPAD_4(100),
        NUMPAD_5(101), NUMPAD_6(102), NUMPAD_7(103), NUMPAD_8(104), NUMPAD_9(105),
        NUMPAD_MULTIPLY(106), NUMPAD_ADD(107), NUMPAD_SUBTRACT(109),
        NUMPAD_DECIMAL(110), NUMPAD_DIVIDE(111),
        
        // Symbols
        MINUS(45), EQUALS(61), LEFT_BRACKET(91), RIGHT_BRACKET(93),
        SEMICOLON(59), QUOTE(222), COMMA(44), PERIOD(46), SLASH(47),
        BACK_SLASH(92), TILDE(192);
        
        private final int code;
        
        KeyCode(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
        
        public static KeyCode fromCode(int code) {
            for (KeyCode key : values()) {
                if (key.code == code) {
                    return key;
                }
            }
            return null;
        }
    }
    
    private KeyCode keyCode;
    private char keyChar;
    private boolean shiftPressed;
    private boolean ctrlPressed;
    private boolean altPressed;
    private boolean metaPressed;
    
    public KeyEvent(Type type, KeyCode keyCode, char keyChar, 
                   boolean shiftPressed, boolean ctrlPressed, 
                   boolean altPressed, boolean metaPressed) {
        super(type);
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.shiftPressed = shiftPressed;
        this.ctrlPressed = ctrlPressed;
        this.altPressed = altPressed;
        this.metaPressed = metaPressed;
    }
    
    public KeyCode getKeyCode() { return keyCode; }
    public char getKeyChar() { return keyChar; }
    public boolean isShiftPressed() { return shiftPressed; }
    public boolean isCtrlPressed() { return ctrlPressed; }
    public boolean isAltPressed() { return altPressed; }
    public boolean isMetaPressed() { return metaPressed; }
    
    public boolean is(KeyCode key) {
        return keyCode == key;
    }
    
    public boolean isModifierPressed() {
        return shiftPressed || ctrlPressed || altPressed || metaPressed;
    }
    
    @Override
    public String toString() {
        return String.format("KeyEvent{type=%s, keyCode=%s, keyChar='%c', modifiers=[shift=%s, ctrl=%s, alt=%s, meta=%s]}",
            getType(), keyCode, keyChar, shiftPressed, ctrlPressed, altPressed, metaPressed);
    }
}