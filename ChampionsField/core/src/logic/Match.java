package logic;

import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Match{
    private Field field;
    private Team homeTeam;
    private Team visitorTeam;
    private Ball ball;
    private int numberOfPlayers;

    public Match(int playersSize, int numberOfPlayers, World w){
        Random r = new Random();
        int aux = r.nextInt(2);
        if(aux == 0){
            homeTeam = new Team(numberOfPlayers, playersSize, "Benfica", Team.TeamState.Attacking, w);
            visitorTeam = new Team(numberOfPlayers, playersSize, "Porto", Team.TeamState.Defending, w);
        } else{
            homeTeam = new Team(numberOfPlayers, playersSize, "Benfica", Team.TeamState.Defending, w);
            visitorTeam = new Team(numberOfPlayers, playersSize, "Porto", Team.TeamState.Attacking, w);
        }
        this.numberOfPlayers = numberOfPlayers;
        ball = new Ball(0, 0, 24, w);
        field = new Field(w);
        //homeTeam.controlPlayer(0);
    }

    public void updateMatch(float x, float y){
        ball.body.setLinearVelocity(x, y);
        homeTeam.updateControlledPlayer(x, y);
        visitorTeam.updateControlledPlayer(x, y);
        homeTeam.updatePlayers();
        visitorTeam.updatePlayers();
    }

    public Ball getBall(){
        return this.ball;
    }

    public Field getField() {
        return field;
    }

    public Team getHomeTeam(){
        return this.homeTeam;
    }

    public Team getVisitorTeam(){
        return this.visitorTeam;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

}