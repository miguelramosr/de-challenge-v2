package com.personal.epl.fn;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.personal.epl.domain.EPLEntryDomain;
import com.personal.epl.domain.EPLSeasonDomain;
import com.personal.epl.domain.EPLSeasonHighestScorers;
import com.personal.epl.domain.EPLShotsRatio;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.TupleTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public class ParseSeasonEntryFn extends DoFn<MatchResult.Metadata, EPLSeasonDomain> {
    private EPLEntryDomain eplEntry;
    private Map<String, Integer> teamPoints = new HashMap<>();
    private Map<String, Integer> teamGoals = new HashMap<>();
    private Map<String, Integer> shotsOnTarget = new HashMap<>();
    private EPLSeasonDomain seasonResults;
    private EPLSeasonHighestScorers goalsResults;
    private EPLShotsRatio shotsGoalsRatioResult;

    private static final String AWAY = "A";
    private static final String HOME = "H";
    private static final String DRAW = "D";
    private final Integer winnerPoints = 3;
    private final Integer drawPoints = 1;

    TupleTag<EPLSeasonDomain> positionMainTablePerSeasonTag;
    TupleTag<EPLShotsRatio> shotsGoalsRatioTag;
    TupleTag<EPLSeasonHighestScorers> positionHighestScoringTeamTag;

    public ParseSeasonEntryFn(TupleTag<EPLSeasonDomain> winner, TupleTag<EPLSeasonHighestScorers> scorers, TupleTag<EPLShotsRatio> shotsRatio){
        positionMainTablePerSeasonTag = winner;
        positionHighestScoringTeamTag = scorers;
        shotsGoalsRatioTag =shotsRatio;
    }

    @StartBundle
    public void startBundle() {
    }

    @ProcessElement
    public void processElement(ProcessContext context) throws IOException {
        teamPoints.clear();
        teamGoals.clear();
        shotsOnTarget.clear();
        ReadableByteChannel channel = FileSystems.open(context.element().resourceId());
        String currentSeason = context.element().resourceId().getFilename().split("_")[0];
        InputStream inStream = Channels.newInputStream(channel);
        try (JsonReader reader = new JsonReader(new InputStreamReader(inStream))){
            reader.beginArray();
            while (reader.hasNext()){
                eplEntry = new Gson().fromJson(reader, EPLEntryDomain.class);
                populateRequiredEntries(eplEntry);
            }
            reader.endArray();
            seasonResults = new EPLSeasonDomain(sortReverseEntries(teamPoints));
            seasonResults.setSeason(currentSeason);
            goalsResults = new EPLSeasonHighestScorers(sortReverseEntries(teamGoals));
            goalsResults.setSeason(currentSeason);
            shotsGoalsRatioResult = new EPLShotsRatio(generateShotsGoalsRatio(shotsOnTarget, teamGoals));
            shotsGoalsRatioResult.setSeason(currentSeason);

            context.output(positionMainTablePerSeasonTag,seasonResults);
            context.output(positionHighestScoringTeamTag,goalsResults);
            context.output(shotsGoalsRatioTag,shotsGoalsRatioResult);
        }
    }

    public void populateRequiredEntries(EPLEntryDomain entry){
        populatePoints(entry);
        populateGoalsPerTeam(entry);
        populateShots(entry);
    }

    public LinkedHashMap<String, Integer> sortReverseEntries(Map<String, Integer> unsortedMap){
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        return sortedMap;
    }

    public LinkedHashMap<String, Float> generateShotsGoalsRatio(Map<String, Integer> shotsOnTargetMap, Map<String, Integer> teamGoalsMap ){
        LinkedHashMap<String, Float> shotsGoalsRatio = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry: shotsOnTargetMap.entrySet()) {
            float ratio =  ((float)teamGoalsMap.get(entry.getKey())/entry.getValue());
            shotsGoalsRatio.put(entry.getKey(),ratio);
        }
        LinkedHashMap<String, Float> sortedGoalRatio = new LinkedHashMap<>();
        shotsGoalsRatio.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedGoalRatio.put(x.getKey(), x.getValue()));
        return sortedGoalRatio;
    }

    public void populateShots(EPLEntryDomain entry){
        addShots(entry.getHomeTeam(), entry.getHST(), shotsOnTarget);
        addShots(entry.getAwayTeam(), entry.getAST(), shotsOnTarget);
    }

    public void addShots(String team, int shots, Map<String, Integer> shotsMap){
        if (shotsMap.containsKey(team)) {
            shotsMap.put(team, shots + shotsMap.get(team));
        } else {
            shotsMap.put(team, shots);
        }
    }

    public void populateGoalsPerTeam(EPLEntryDomain entry){
        populateGoals(entry.getHomeTeam(), entry.getFTHG());
        populateGoals(entry.getAwayTeam(), entry.getFTAG());
    }

    public void populateGoals(String team, int goals){
        if (teamGoals.containsKey(team)) {
            teamGoals.put(team, goals + teamGoals.get(team));
        } else {
            teamGoals.put(team, goals);
        }
    }

    public void populatePoints(EPLEntryDomain entry) {
        String winner = entry.getFTR();
        if (winner.equalsIgnoreCase(AWAY)) {
            addPoints(entry.getAwayTeam(), winnerPoints);
        } else if (winner.equalsIgnoreCase(HOME)) {
            addPoints(entry.getHomeTeam(), winnerPoints);
        } else if (winner.equalsIgnoreCase(DRAW)) {
            addPoints(entry.getAwayTeam(), drawPoints);
            addPoints(entry.getHomeTeam(), drawPoints);
        }
    }

    public void addPoints(String team, int points) {
        if (teamPoints.containsKey(team)) {
            teamPoints.put(team, points + teamPoints.get(team));
        } else {
            teamPoints.put(team, points);
        }
    }

    @FinishBundle
    public void finishBundle(FinishBundleContext c) throws IOException {
    }
}
