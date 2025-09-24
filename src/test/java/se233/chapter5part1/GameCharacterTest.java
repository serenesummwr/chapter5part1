package se233.chapter5part1;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se233.chapter5part1.model.GameCharacter;
import se233.chapter5part1.model.Keys;
import se233.chapter5part1.view.GameStage;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class GameCharacterTest {
    Field xVelocityField, yVelocityField, yAccelerationField, canJumpField, isJumpingField, isFallingField;
    private GameCharacter gameCharacter;

    @BeforeAll
    public static void initJfxRuntime() {
        javafx.application.Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException {
        gameCharacter = new GameCharacter(0, 30, 30, "assets/Character1.png", 4, 3, 2, 111, 97, KeyCode.A, KeyCode.D, KeyCode.W);
        xVelocityField = gameCharacter.getClass().getDeclaredField("xVelocity");
        yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");
        yAccelerationField = gameCharacter.getClass().getDeclaredField("yAcceleration");
        canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        xVelocityField.setAccessible(true);
        yVelocityField.setAccessible(true);
        yAccelerationField.setAccessible(true);
        canJumpField.setAccessible(true);
        isJumpingField.setAccessible(true);
        isFallingField.setAccessible(true);
    }

    @Test
    public void respawn_givenNewGameCharacter_thenCoordinatesAre30_30() {
        gameCharacter.respawn();
        assertEquals(30, gameCharacter.getX(), "Initial x");
        assertEquals(30, gameCharacter.getY(), "Initial y");
    }

    @Test
    public void respawn_givenNewGameCharacter_thenScoreIs0() {
        gameCharacter.respawn();
        assertEquals(0, gameCharacter.getScore(), "Initial score");
    }

    @Test
    public void moveX_givenMoveRightOnce_thenXCoordinateIncreasedByXVelocity() throws IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveRight();
        gameCharacter.moveX();
        assertEquals(30 + xVelocityField.getInt(gameCharacter), gameCharacter.getX(), "Move right x");
    }

    @Test
    public void moveY_givenTwoConsecutiveCalls_thenYVelocityIncreases() throws IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveY();
        int yVelocity1 = yVelocityField.getInt(gameCharacter);
        gameCharacter.moveY();
        int yVelocity2 = yVelocityField.getInt(gameCharacter);
        assertTrue(yVelocity2 > yVelocity1, "Velocity is increasing");
    }

    @Test
    public void moveY_givenTwoConsecutiveCalls_thenYAccelerationUnchanged() throws IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveY();
        int yAcceleration1 = yAccelerationField.getInt(gameCharacter);
        gameCharacter.moveY();
        int yAcceleration2 = yAccelerationField.getInt(gameCharacter);
        assertTrue(yAcceleration1 == yAcceleration2, "Acceleration is not change");
    }

    @Test
    public void whenTryToGoThroughLeftWall_thenReachLeftWall(){
        gameCharacter.respawn();
        gameCharacter.setX(0);
        gameCharacter.moveLeft();
        gameCharacter.moveX();
        gameCharacter.checkReachGameWall();

        assertEquals(0, gameCharacter.getX(),"Not equal to 0");
    }

    @Test
    public void whenTryToGoThroughRightWall_thenReachRightWall(){
        gameCharacter.respawn();
        gameCharacter.setX(GameStage.WIDTH);
        gameCharacter.moveRight();
        gameCharacter.moveX();
        gameCharacter.checkReachGameWall();
        System.out.println(GameStage.WIDTH);
        System.out.println(gameCharacter.getCharacterWidth());
        System.out.println(gameCharacter.getX());
        assertEquals(GameStage.WIDTH- gameCharacter.getCharacterWidth(),gameCharacter.getX(),"Not at right wall");
    }

    @Test
    public void whenNotJumping_canJump() throws IllegalAccessException {
        gameCharacter.respawn();
        canJumpField.setBoolean(gameCharacter,true);
        isJumpingField.setBoolean(gameCharacter, false);
        int initialY=gameCharacter.getY();
        gameCharacter.jump();
        gameCharacter.moveY();
        assertTrue(gameCharacter.getY() < initialY, "Jumping is not possible");
    }

    @Test
    public void whenJumpingAndFalling_cannotJump() throws IllegalAccessException {
        gameCharacter.respawn();
        int initialY=gameCharacter.getY();
        isJumpingField.set(gameCharacter,false);
        isFallingField.set(gameCharacter,true);
        gameCharacter.jump(); //jump while falling
        gameCharacter.moveY();
        assertTrue(gameCharacter.getY() > initialY, "Current Y must be below of initY  when falling.");
        assertTrue(yVelocityField.getInt(gameCharacter)>0,"yVelocity should be greater than 0 while falling");
    }

    @Test
    public void whenMovingX_collideAndStop(){
        gameCharacter.respawn();
        GameCharacter mockGameCharacter= Mockito.mock(GameCharacter.class);
        mockGameCharacter=new GameCharacter(0, 141, 30, "assets/Character1.png",
                4, 3, 2, 111, 97, KeyCode.A, KeyCode.D, KeyCode.W);

        gameCharacter.moveRight();
        gameCharacter.moveX();
        gameCharacter.moveX();
        gameCharacter.moveX();
        System.out.println(gameCharacter.getX());
        gameCharacter.collided(mockGameCharacter);
        System.out.println(gameCharacter.getX());

        assertEquals(gameCharacter.getX()+gameCharacter.getCharacterWidth(), mockGameCharacter.getX(), "characters overlapped");
    }

    @Test
    public void whenFallingX_collideAndStop() throws IllegalAccessException {
        gameCharacter.respawn();
        GameCharacter mockGameCharacter= Mockito.mock(GameCharacter.class);
        mockGameCharacter=new GameCharacter(0, 30, gameCharacter.getY()+gameCharacter.getCharacterHeight(), "assets/Character1.png",
                4, 3, 2, 111, 97, KeyCode.A, KeyCode.D, KeyCode.W);

        isFallingField.set(gameCharacter,true);
        gameCharacter.moveY();

        System.out.println(gameCharacter.getY());
        gameCharacter.collided(mockGameCharacter);
        System.out.println(gameCharacter.getY());

        assertTrue(gameCharacter.getY() <= GameStage.GROUND-gameCharacter.getCharacterHeight(), "s");
    }

    @Test
    public void testingSingleKeyPress(){
        Keys keys = new Keys();
        keys.add(KeyCode.D);
        assertTrue(keys.isPressed(KeyCode.D), "D should be detected as pressed");
        assertFalse(keys.isPressed(KeyCode.A), "A should not be detected as pressed");
    }

    @Test
    public void testingSequentialKeyPress(){
        Keys keys = new Keys();
        keys.add(KeyCode.A);
        assertTrue(keys.isPressed(KeyCode.A), "A should be detected as pressed");

        keys.add(KeyCode.D);
        assertTrue(keys.isPressed(KeyCode.D), "D should be detected as pressed");
        assertTrue(keys.isPressed(KeyCode.A), "A should be detected as pressed");
    }

}

