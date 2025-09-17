package se233.chapter5part1.model;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import se233.chapter5part1.Launcher;
import se233.chapter5part1.view.GameStage;
import java.util.concurrent.TimeUnit;

public class GameCharacter extends Pane {
    private Image characterImg;
    private AnimatedSprite imageView;
    private int x;
    private int y;
    private int startX;
    private int startY;
    private int characterWidth;
    private int characterHeight;
    private int score=0;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private KeyCode upKey;
    int xVelocity = 0;
    int yVelocity = 0;
    int xAcceleration = 1;
    int yAcceleration = 1;
    int xMaxVelocity = 7;
    int yMaxVelocity = 17;
    boolean isMoveLeft = false;
    boolean isMoveRight = false;
    boolean isFalling = true;
    boolean canJump = false;
    boolean isJumping = false;

    public GameCharacter(int id, int x, int y, String imgName, int count, int column, int row, int width, int height, KeyCode leftKey, KeyCode rightKey, KeyCode upKey) {
        this.startX=x;
        this.startY=y;
        this.x = x;
        this.y = y;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.characterWidth = width;
        this.characterHeight = height;
        this.characterImg = new Image(Launcher.class.getResourceAsStream(imgName));
        this.imageView = new AnimatedSprite(characterImg, count, column, row, 0, 0, width, height);
        this.imageView.setFitWidth((int) (width * 1.2));
        this.imageView.setFitHeight((int) (height * 1.2));
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.getChildren().addAll(this.imageView);
        setScaleX(id % 2 * 2 - 1);
    }
    public void moveLeft() {
        setScaleX(1);
        isMoveLeft = true;
        isMoveRight = false;
    }
    public void moveRight() {
        setScaleX(-1);
        isMoveLeft = false;
        isMoveRight = true;
    }
    public void stop() {
        isMoveLeft = false;
        isMoveRight = false;
    }
    public void moveX() {
        setTranslateX(x);
        if(isMoveLeft) {
            xVelocity = xVelocity>=xMaxVelocity? xMaxVelocity : xVelocity+xAcceleration;
            x = x - xVelocity;
        }
        if(isMoveRight) {
            xVelocity = xVelocity>=xMaxVelocity? xMaxVelocity : xVelocity+xAcceleration;
            x = x + xVelocity;
        }
    }
    public void moveY() {
        setTranslateY(y);
        if(isFalling) {
            yVelocity = yVelocity >= yMaxVelocity? yMaxVelocity : yVelocity+yAcceleration;
            y = y + yVelocity;
        } else if(isJumping) {
            yVelocity = yVelocity <= 0 ? 0 : yVelocity-yAcceleration;
            y = y - yVelocity;
        }
    }
    public void checkReachGameWall() {
        if(x <= 0) {
            x = 0;
        } else if( x+getCharacterWidth() >= GameStage.WIDTH) {
            x = GameStage.WIDTH-(int)getCharacterWidth();
        }
    }
    public void jump() {
        if (canJump) {
            yVelocity = yMaxVelocity;
            canJump = false;
            isJumping = true;
            isFalling = false;
        }
    }
    public void checkReachHighest () {
        if(isJumping && yVelocity <= 0) {
            isJumping = false;
            isFalling = true;
            yVelocity = 0;
        }
    }
    public void checkReachFloor() {
        if(isFalling && y >= GameStage.GROUND - this.characterHeight) {
            isFalling = false;
            canJump = true;
            yVelocity = 0;
        }
    }
    public void repaint() {
        moveX();
        moveY();
    }

    public boolean collided(GameCharacter c) {
        if (this.isMoveLeft && this.x > c.getX()) {
            this.x = Math.max(this.x, c.getX() + c.getCharacterWidth());
            this.stop();
        } else if (this.isMoveRight && this.x < c.getX()) {
            this.x = Math.min(this.x, c.getX() - this.characterWidth);
            this.stop();
        }
        if (this.isFalling && this.y < c.getY()) {
            score++;
            this.y = Math.min(GameStage.GROUND - this.characterHeight, c.getY());
            this.repaint();
            c.collapsed();
            c.respawn();
            return true;
        }
        return false;
    }

    public void collapsed() {
        this.imageView.setFitHeight(5);
        this.y = this.y + this.characterHeight - 5;
        this.repaint();
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void respawn() {
        this.x = this.startX;
        this.y = this.startY;
        this.imageView.setFitWidth(this.characterWidth*1.2);
        this.imageView.setFitHeight(this.characterHeight*1.2);
        this.isMoveLeft = false;
        this.isMoveRight = false;
        this.isFalling = true;
        this.canJump = false;
        this.isJumping = false;
    }


    // *****Getter Setter*****
    // ***********************
    public KeyCode getLeftKey() {

        return leftKey;
    }
    public KeyCode getRightKey() {

        return rightKey;
    }
    public KeyCode getUpKey() {

        return upKey;
    }
    public AnimatedSprite getImageView() {

        return imageView;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getCharacterImg() {
        return characterImg;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getCharacterWidth() {
        return characterWidth;
    }

    public int getCharacterHeight() {
        return characterHeight;
    }

    public int getScore() {
        return score;
    }

    public void setCharacterImg(Image characterImg) {
        this.characterImg = characterImg;
    }

    public void setImageView(AnimatedSprite imageView) {
        this.imageView = imageView;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public void setCharacterWidth(int characterWidth) {
        this.characterWidth = characterWidth;
    }

    public void setCharacterHeight(int characterHeight) {
        this.characterHeight = characterHeight;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLeftKey(KeyCode leftKey) {
        this.leftKey = leftKey;
    }

    public void setRightKey(KeyCode rightKey) {
        this.rightKey = rightKey;
    }

    public void setUpKey(KeyCode upKey) {
        this.upKey = upKey;
    }
}