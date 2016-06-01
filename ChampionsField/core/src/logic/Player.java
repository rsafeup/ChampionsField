package logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import static logic.Match.entityMasks.BallMask;
import static logic.Match.entityMasks.CenterMask;
import static logic.Match.entityMasks.GoalMask;
import static logic.Match.entityMasks.PlayerMask;
import static logic.Match.entityMasks.ScreenBordersMask;

public class Player implements GestureDetector.GestureListener, Coordinates{
    Vector2 position;
    float speed;
    float radius;
    private boolean controlledPlayer;
    int score;
    String name;
    int team;
    Body body;

    //Pan movement detection
    private Vector2 panPosition;
    private boolean changingPath;
    ArrayList<Vector2> path;

    public Player(float xPosition, float yPosition, String name, float size, boolean controlledPlayer, World w) {
        position = new Vector2(xPosition * 0.01f, yPosition* 0.01f);
        speed = 5f;
        this.controlledPlayer = controlledPlayer;
        this.radius = size/2;
        this.score = 0;
        this.name = name;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        body = w.createBody(bodyDef);
        body.setAngularDamping(0.5f);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius * 0.01f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = PlayerMask.getMask();
        fixtureDef.filter.maskBits = (short)(PlayerMask.getMask() | BallMask.getMask() | ScreenBordersMask.getMask() | GoalMask.getMask() | CenterMask.getMask());
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();

        panPosition = new Vector2();
        changingPath = false;
        path = new ArrayList<Vector2>();

    }

    public void reposition(float x, float y) {
        float finalX = x * 0.01f;
        float finalY = y * 0.01f;
        body.setTransform(finalX, finalY, 0);
        setPositionToBody();
        body.setLinearVelocity(0, 0);
    }

    public void getCout() {
        System.out.println(name + ": final x = " + position.x);
    }

    public void stopPlayerMotion() {
        body.setLinearVelocity(0, 0);
    }

    public boolean isControlledPlayer() {
        return controlledPlayer;
    }

    public void setControlledPlayer(boolean controlledPlayer) {
        this.controlledPlayer = controlledPlayer;
    }

    public float getRadius() {
        return radius;
    }

    public void setPosition(Vector2 pos) {
        position = pos;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public ArrayList<Vector2> getPath() {
        return path;
    }

    public boolean isWaypointReached() {
        return path.get(0).x - position.x <= speed / 3 * Gdx.graphics.getDeltaTime() && path.get(0).y - position.y <= speed / 3 * Gdx.graphics.getDeltaTime();
    }

    public void updatePosition() {
        if (path.size() != 0) {
            float angle = (float) Math.atan2(path.get(0).y - position.y, path.get(0).x - position.x);
            Vector2 velocity = new Vector2((float) Math.cos(angle) * speed * Gdx.graphics.getDeltaTime(), (float) Math.sin(angle) * speed * Gdx.graphics.getDeltaTime());

            position.set(position.x + velocity.x, position.y + velocity.y);

            if (isWaypointReached()) {
                position.set(path.get(0).x, path.get(0).y);
                path.remove(0);
            }
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if(changingPath) {
            panPosition.set(position.x, position.y);
            for(int i = 0; i < path.size(); i++)
                path.clear();
            changingPath = false;
        }

        panPosition.x += deltaX * 0.01f;
        panPosition.y -= deltaY * 0.01f;
        path.add(new Vector2(panPosition.x + deltaX, panPosition.y - deltaY));
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        changingPath = true;
        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    //This need to be redefined, it's not good
    @Override
    public boolean equals(Object obj) {
        Player p = (Player) obj;
        if((this.position == p.position) && this.name == p.name && this.score == p.score) {
            return true;
        }
        return false;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPositionToBody(){
        setPosition(body.getPosition());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Vector2 getScreenCoordinates() {
        float x = getPosition().x * 100f + Gdx.graphics.getWidth()/2 - getRadius();
        float y = getPosition().y * 100f + Gdx.graphics.getHeight()/2 - getRadius();
        return new Vector2(x, y);
    }

    /*
    * BEGIN OF THE MULTIPLAYER FUNCTIONS
    * */

    public Player() {

    }

    public Player(float xPosition, float yPosition, String name, float size, int team) {
        this.position = new Vector2();
        this.position.x = xPosition;
        this.position.y = yPosition;
        this.name = name;
        this.team = team;
        this.radius = size / 2;
        this.score = 0;
        this.controlledPlayer = true;
    }

    public void addPhysics(World w) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        body = w.createBody(bodyDef);
        body.setAngularDamping(0.5f);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius * 0.01f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = PlayerMask.getMask();
        fixtureDef.filter.maskBits = (short)(PlayerMask.getMask() | BallMask.getMask() | ScreenBordersMask.getMask() | GoalMask.getMask() | CenterMask.getMask());
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    /*
    * END OF THE MULTIPLAYER FUNCTIONS
    * */
}
