package engine.input;

import engine.collision.Vector2D;

public class PlayerController extends InputComponent {
    private double jumpForce;
    private boolean canJump;
    private boolean isGrounded;
    
    public PlayerController() {
        super();
        this.jumpForce = 300.0;
        this.canJump = true;
        this.isGrounded = false;
        setMovementEnabled(true);
    }
    
    public PlayerController(double moveSpeed, double jumpForce) {
        this();
        setMoveSpeed(moveSpeed);
        this.jumpForce = jumpForce;
    }
    
    @Override
    protected void onSpecificInputEvent(InputEvent event) {
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            
            switch (event.getType()) {
                case KEY_PRESSED:
                    handleKeyPressed(keyEvent);
                    break;
                case KEY_RELEASED:
                    handleKeyReleased(keyEvent);
                    break;
            }
        }
    }
    
    private void handleKeyPressed(KeyEvent event) {
        if (gameObject == null) return;
        
        // Jump
        if (event.is(KeyEvent.KeyCode.SPACE) && canJump && isGrounded) {
            Vector2D currentVelocity = gameObject.getVelocity();
            currentVelocity = new Vector2D(currentVelocity.getX(), -jumpForce);
            gameObject.setVelocity(currentVelocity);
            onJump();
        }
        
        // Dash
        if (event.is(KeyEvent.KeyCode.SHIFT)) {
            Vector2D currentVelocity = gameObject.getVelocity();
            double dashMultiplier = 2.0;
            gameObject.setVelocity(currentVelocity.multiply(dashMultiplier));
            onDash();
        }
        
        // Action button
        if (event.is(KeyEvent.KeyCode.E)) {
            onAction();
        }
    }
    
    private void handleKeyReleased(KeyEvent event) {
        if (event.is(KeyEvent.KeyCode.SHIFT)) {
            // End dash - return to normal speed
            Vector2D currentVelocity = gameObject.getVelocity();
            double normalMultiplier = 0.5;
            gameObject.setVelocity(currentVelocity.multiply(normalMultiplier));
        }
    }
    
    @Override
    protected void onMouseClicked(MouseEvent event) {
        if (event.is(MouseEvent.Button.LEFT)) {
            onPrimaryAttack(event.getPosition());
        } else if (event.is(MouseEvent.Button.RIGHT)) {
            onSecondaryAttack(event.getPosition());
        }
    }
    
    // Physics integration
    public void setGrounded(boolean grounded) {
        this.isGrounded = grounded;
    }
    
    public boolean isGrounded() {
        return isGrounded;
    }
    
    // Character abilities
    public void enableJump(boolean canJump) {
        this.canJump = canJump;
    }
    
    public boolean canJump() {
        return canJump;
    }
    
    public double getJumpForce() {
        return jumpForce;
    }
    
    public void setJumpForce(double jumpForce) {
        this.jumpForce = jumpForce;
    }
    
    // Overrideable callbacks for game-specific behavior
    protected void onJump() {
        System.out.println("Player jumped!");
    }
    
    protected void onDash() {
        System.out.println("Player dashed!");
    }
    
    protected void onAction() {
        System.out.println("Player performed action!");
    }
    
    protected void onPrimaryAttack(Vector2D targetPosition) {
        System.out.println("Player primary attacked at: " + targetPosition);
    }
    
    protected void onSecondaryAttack(Vector2D targetPosition) {
        System.out.println("Player secondary attacked at: " + targetPosition);
    }
    
    @Override
    protected void onMouseHover(Vector2D mousePos) {
        // Could be used for UI feedback, crosshair changes, etc.
    }
}