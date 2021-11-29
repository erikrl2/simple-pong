package com.erik.games;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ProjectileComponent;

public class PongEntityFactory implements EntityFactory {

	@Spawns("screenBounds")
	public static Entity makeScreenBounds(SpawnData data) {
		  double thickness = 40;
		  double w = getSettings().getWidth();
		  double h = getSettings().getHeight();
		  return entityBuilder()
		      .bbox(new HitBox("LEFT",  new Point2D(-thickness, 0), BoundingShape.box(thickness, h)))
		      .bbox(new HitBox("RIGHT", new Point2D(w, 0), BoundingShape.box(thickness, h)))
		      .bbox(new HitBox("TOP",   new Point2D(0, -thickness), BoundingShape.box(w, thickness)))
		      .bbox(new HitBox("BOT",   new Point2D(0, h), BoundingShape.box(w, thickness)))
		      .with(new PhysicsComponent())
		      .build();
		}
	
	@Spawns("background")
	public Entity newBackground(SpawnData data) {
		return entityBuilder(data)
				.view(new Rectangle(getAppWidth(), getAppHeight()))
				.build();
	}
	
	@Spawns("centerLine")
	public Entity newCenterLine(SpawnData data) {
		return entityBuilder(data)
				.view(new Rectangle(1, getAppHeight(), Color.WHITE))
				.at(getAppWidth() / 2 - 1, 0)
				.build();
	}
	
	@Spawns("border")
	public Entity newBorder(SpawnData data) {
		return entityBuilder(data)
				.type(EntityType.BORDER)
				.viewWithBBox(new Rectangle(getAppWidth(), 1))
				.collidable()
				.build();
	}
	
	@Spawns("borderLeft")
	public Entity newBorderLeft(SpawnData data) {
		return entityBuilder(data)
				.type(EntityType.LEFT)
				.viewWithBBox(new Rectangle(1, getAppHeight()))
				.collidable()
				.build();
	}
	
	@Spawns("borderRight")
	public Entity newBorderRight(SpawnData data) {
		return entityBuilder(data)
				.type(EntityType.RIGHT)
				.viewWithBBox(new Rectangle(1, getAppHeight()))
				.collidable()
				.build();
	}

	@Spawns("player")
	public Entity newPlayer(SpawnData data) {
		return entityBuilder(data)
				.type(EntityType.PLAYER)
				.view(new Rectangle(10, 100, Color.WHITE))
				.bbox(BoundingShape.box(20, 100))
				.collidable()
				.build();
	}

	@Spawns("ball")
	public Entity newBall(SpawnData data) {
		double x; double y;
		do {
			x = FXGLMath.random(-1, 1);
			y = FXGLMath.random(-0.5, 0.5);
		} while (x == 0 || (y > -0.2 && y < 0.2));

		PhysicsComponent physics = new PhysicsComponent();
		physics.setBodyType(BodyType.DYNAMIC);
		physics.setFixtureDef(new FixtureDef().restitution(1f));
		
		double v = 600;
		Point2D dir = new Point2D(x , y);
		physics.setOnPhysicsInitialized(() -> physics.setLinearVelocity(dir.multiply(v)));

		return entityBuilder(data)
				.type(EntityType.BALL)
				.viewWithBBox(new Circle(10, Color.AQUA))
				.with(new ProjectileComponent(new Point2D(x, y), 600).allowRotation(false))
				.collidable()
				.at(getAppCenter())
				.build();
	}

}
