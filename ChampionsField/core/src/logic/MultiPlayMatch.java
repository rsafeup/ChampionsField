package logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.Random;

import utils.Constants;
import utils.Statistics;

public class MultiPlayMatch extends Match {
    public enum matchState {
        KickOff,
        Pause,
        Score,
        Play;
    }

    public boolean moved;

    public MultiPlayMatch(){
        super(0);

        Random r = new Random();
        int aux = r.nextInt(2);     //MUDAR AQUI, SO PARA QUESTOES DE DEBUG! :D
        aux = 0;
        if(aux == 0){
            homeTeam = new Team("Benfica", Team.TeamState.Attacking, w);
            visitorTeam = new Team("Porto", Team.TeamState.Defending, w);
        } else{
            homeTeam = new Team("Benfica", Team.TeamState.Defending, w);
            visitorTeam = new Team("Porto", Team.TeamState.Attacking, w);
        }
        //homeTeam.controlPlayer(0);

        numberOfPlayers = 0;
        moved = false;
    }

    public void addPlayerToMatch(String name, int team, boolean controlledPlayer) {
        if(team == 0)
            homeTeam.addPlayer(name, team, playerSize, controlledPlayer, w);
        else
            visitorTeam.addPlayer(name, team, playerSize, controlledPlayer, w);

        numberOfPlayers++;
    }

    @Override
    public void teamScored(Team defendingTeam, Team attackingTeam, String lastTouch) {
        ArrayList<String> attackingTeamNames = attackingTeam.getPlayerNames();

        //auto goal
        if (attackingTeamNames.contains(lastTouch)) {
            attackingTeam.autoGoal(lastTouch);
        } else defendingTeam.goalScored(lastTouch);
        defendingTeam.teamState = Team.TeamState.Defending;
        attackingTeam.teamState = Team.TeamState.Attacking;
        currentState = Match.matchState.Score;
    }

    @Override
    public void updateMatch(float x, float y, Rain rain, float dt) {
        homeTeam.updateControlledPlayerOnline(x, y);

        rain.update();
        w.step(Constants.GAME_SIMULATION_SPEED, 6, 2);

        if(x != 0 || y != 0) moved = true;
        //else moved = false;
    }

    @Override
    public void endScoreState() {

    }

    @Override
    public void endGame() {
        ArrayList<Statistics> stats = new ArrayList<Statistics>();
        String name;
        int score, matches;

        Statistics parser = new Statistics("", 0, 0);
        FileHandle globalStatistics = Gdx.files.local("Statistics.txt");
        String info;
        boolean exist;
        exist=Gdx.files.local("Statistics.txt").exists();
        if(!exist){
            System.out.println("Error opening file!");
            return;
        } else{
            info = globalStatistics.readString();
            stats = parser.parseStatisticsToArray(info);
        }

        for (int i = 0; i < homeTeam.getNumberPlayers(); i++) {
            homeTeam.players.get(i).addMatchPlayed();
            name = homeTeam.players.get(i).name;
            score = homeTeam.players.get(i).score;
            matches = homeTeam.players.get(i).matchesPlayed;
            Statistics s = new Statistics(name, score, matches);
            if(stats.contains(s)){
                stats.remove(s);
                stats.add(s);
            } else stats.add(s);
        }

        for (int i = 0; i < visitorTeam.getNumberPlayers(); i++) {
            visitorTeam.players.get(i).addMatchPlayed();
            name = visitorTeam.players.get(i).name;
            score = visitorTeam.players.get(i).score;
            matches = visitorTeam.players.get(i).matchesPlayed;
            Statistics s = new Statistics(name, score, matches);
            if(stats.contains(s)){
                stats.remove(s);
                stats.add(s);
            } else stats.add(s);
        }

        globalStatistics.writer(false);
        String output = "";
        for(int i = 0; i < stats.size(); i++){
            output = stats.get(i).stringToFile();
            globalStatistics.writeString(output, true);
            if(i != stats.size() - 1)
                globalStatistics.writeString("\n", true);
        }
    }

    public float getClientPlayerX(int team) {
        if(team == 0) {
            for(Player player : homeTeam.getPlayers())
                if(player.getControlled())
                    return player.body.getTransform().getPosition().x;
        } else {
            for(Player player : visitorTeam.getPlayers())
                if(player.getControlled())
                    return player.body.getTransform().getPosition().x;
        }

        return -1;
    }

    public float getClientPlayerY(int team) {
        if(team == 0) {
            for(Player player : homeTeam.getPlayers())
                if(player.getControlled())
                    return player.body.getTransform().getPosition().y;
        } else {
            for(Player player : visitorTeam.getPlayers())
                if(player.getControlled())
                    return player.body.getTransform().getPosition().y;
        }

        return -1;
    }

    public void setPlayerPosition(float x, float y, String name, int team) {
        if(team == 0)
            homeTeam.changePlayerPosition(x, y, name);
        else
            visitorTeam.changePlayerPosition(x, y, name);
    }
}