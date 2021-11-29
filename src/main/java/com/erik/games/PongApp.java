package com.erik.games;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class PongApp extends GameApplication {

	Entity player1;
	Entity player2;
	Entity ball;

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setWidth(900);
		settings.setHeight(600);
		settings.setTitle("Pong");
		settings.setVersion("1.1");
	}

	@Override
	protected void initInput() {
		onKey(KeyCode.W, () -> {
			if (player1.getY() >= 10)
				player1.translateY(-10);
		});
		onKey(KeyCode.S, () -> {
			if (player1.getY() <= getAppHeight() - 110)
				player1.translateY(+10);
		});

		onKey(KeyCode.UP, () -> {
			if (player2.getY() >= 10)
				player2.translateY(-10);
		});
		onKey(KeyCode.DOWN, () -> {
			if (player2.getY() <= getAppHeight() - 110)
				player2.translateY(+10);
		});
	}

	@Override
	protected void initGameVars(Map<String, Object> vars) {
		vars.put("score1", 0);
		vars.put("score2", 0);
	}

	@Override
	protected void initGame() {
		getGameWorld().addEntityFactory(new PongEntityFactory());
		spawn("background");
		spawn("centerLine");
//		spawn("screenBounds");
		spawn("border", 0, 10).setVisible(false);
		spawn("border", 0, getAppHeight() + 10).setVisible(false);
		spawn("borderLeft", -10, 0).setVisible(false);
		spawn("borderRight", getAppWidth() + 10, 0).setVisible(false);

		player1 = spawn("player", 25, getAppHeight() / 2 - 50);
		player2 = spawn("player", getAppWidth() - 40, getAppHeight() / 2 - 50);
		ball = spawn("ball");
	}

	@Override
	protected void initPhysics() {

		onCollisionBegin(EntityType.PLAYER, EntityType.BALL, (player, ball) -> {
			ProjectileComponent p = ball.getComponent(ProjectileComponent.class);
			Point2D dir = p.getDirection();
			p.setDirection(new Point2D(dir.getX() * -1, dir.getY()));
		});

		onCollisionBegin(EntityType.BORDER, EntityType.BALL, (border, ball) -> {
			ProjectileComponent p = ball.getComponent(ProjectileComponent.class);
			Point2D dir = p.getDirection();
			p.setDirection(new Point2D(dir.getX(), dir.getY() * -1));
		});

		onCollisionBegin(EntityType.LEFT, EntityType.BALL, (border, ball) -> {
			inc("score2", 1);
			if (geti("score2") == 10)
				newGame("Right");
			runOnce(() -> spawn("ball"), Duration.seconds(0.5));
		});

		onCollisionBegin(EntityType.RIGHT, EntityType.BALL, (border, ball) -> {
			inc("score1", 1);
			if (geti("score1") == 10)
				newGame("Left");
			runOnce(() -> spawn("ball"), Duration.seconds(0.5));
		});

	}

	private void newGame(String p) {
		getDialogService().showMessageBox(p + " player won!\n\nGame will restart.");
		set("score1", 0);
		set("score2", 0);
	}

	@Override
	protected void initUI() {
		var scoreP1 = getUIFactoryService().newText("", Color.WHITE, 25);
		var scoreP2 = getUIFactoryService().newText("", Color.WHITE, 25);
		scoreP1.textProperty().bind(getip("score1").asString("%d"));
		scoreP2.textProperty().bind(getip("score2").asString("%d"));
		addUINode(scoreP1, getAppWidth() / 2 - 26, 30);
		addUINode(scoreP2, getAppWidth() / 2 + 10, 30);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
