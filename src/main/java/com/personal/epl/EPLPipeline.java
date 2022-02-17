package com.personal.epl;


import com.personal.epl.domain.EPLSeasonDomain;
import com.personal.epl.domain.EPLSeasonHighestScorers;
import com.personal.epl.domain.EPLShotsRatio;
import com.personal.epl.fn.ParseSeasonEntryFn;
import com.personal.epl.fn.ShowResultsFn;
import com.personal.epl.options.EPLOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Contextful;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.*;

public class EPLPipeline {
    private static TupleTag<EPLSeasonDomain> positionTablePerSeason = new TupleTag<EPLSeasonDomain>() {};
    private static TupleTag<EPLSeasonHighestScorers> positionGoalsTeamPerSeason = new TupleTag<EPLSeasonHighestScorers>() {};
    private static TupleTag<EPLShotsRatio> shotsGoalsRatio = new TupleTag<EPLShotsRatio>() {};
    private static final String SEASON_DOMAIN = EPLSeasonDomain.class.getCanonicalName();
    private static final String HIGHEST_SCORE_DOMAIN = EPLSeasonHighestScorers.class.getCanonicalName();
    private static final String GOALS_RATIO_DOMAIN = EPLShotsRatio.class.getCanonicalName();
    private static String output = "src/main/resources/output/";
    private static String extension = ".json";

    public static void main(String[] args) {
        EPLPipeline.setup(args);
    }

    public static void setup(String[] args) {
        PipelineOptionsFactory.register(EPLOptions.class);
        EPLOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(EPLOptions.class);
        Pipeline pipeline = Pipeline.create(options);
        FileSystems.setDefaultPipelineOptions(options);
        process(pipeline, options);
        PipelineResult result = pipeline.run();
        PipelineResult.State state = result.waitUntilFinish();
        if (state.equals(PipelineResult.State.FAILED) || state.equals(PipelineResult.State.CANCELLED))
            throw new RuntimeException("Job has ended with failing status " + state);
    }

    public static void process(Pipeline pipeline, EPLOptions options) {
        PCollection<MatchResult.Metadata> files = extract(pipeline, options);
        PCollectionTuple transformedTuple = transformProcess(files);
        PCollection<EPLSeasonDomain> winners = transformedTuple.get(positionTablePerSeason);
        PCollection<EPLSeasonHighestScorers> scorersPCollection = transformedTuple.get(positionGoalsTeamPerSeason);
        PCollection<EPLShotsRatio> shotsRatio = transformedTuple.get(shotsGoalsRatio);
//        String outputDir = options.getOutputDir().get();
        showResults(winners, "WinnerPerSeason");
        showResults(scorersPCollection, "HighestScoreResults");
        showResults(shotsRatio, "GoalsShotRatio");
        loadResults(winners, "WinnerPerSeason", options);
        loadResults(scorersPCollection, "HighestScoreResults", options);
        loadResults(shotsRatio, "GoalsShotRatio", options);
    }

    public static PCollection<MatchResult.Metadata> extract(Pipeline pipeline, EPLOptions options){
        return pipeline.apply("Read Data Season Files", FileIO.match().filepattern(options.getDataInput()));
    }

    public static PCollectionTuple transformProcess(PCollection<MatchResult.Metadata> files) {
        TupleTagList list = TupleTagList.of(positionGoalsTeamPerSeason).and(shotsGoalsRatio);
        PCollectionTuple seasonsTuples = files.apply("Parse Seasons", ParDo.of(new ParseSeasonEntryFn(positionTablePerSeason, positionGoalsTeamPerSeason, shotsGoalsRatio)).withOutputTags(positionTablePerSeason, list));
        return seasonsTuples;
    }

    public static void showResults(PCollection collectionDesired,String type) {
        collectionDesired.apply("Show Results of " + type, ParDo.of(new ShowResultsFn()));
    }

    public static void loadResults(PCollection collectionDesired,String type,EPLOptions options ){

        if(collectionDesired.getTypeDescriptor().toString().equals(SEASON_DOMAIN)){
            collectionDesired.apply("Writing " +  type, FileIO.<String, EPLSeasonDomain>writeDynamic().by(e1 -> e1.getSeason()).
                    withDestinationCoder(StringUtf8Coder.of()).via(Contextful.fn(e2 -> e2.toString()), TextIO.sink()).
                    withNaming(e2 -> FileIO.Write.defaultNaming(e2, extension)).to(output+type+"/"));
        }
        else  if(collectionDesired.getTypeDescriptor().toString().equals(HIGHEST_SCORE_DOMAIN)){
            collectionDesired.apply("Writing " +  type, FileIO.<String, EPLSeasonHighestScorers>writeDynamic().by(e1 -> e1.getSeason()).
                    withDestinationCoder(StringUtf8Coder.of()).via(Contextful.fn(e2 -> e2.toString()), TextIO.sink()).
                    withNaming(e2 -> FileIO.Write.defaultNaming(e2, extension)).to(output+type+"/"));
        }
        else  if(collectionDesired.getTypeDescriptor().toString().equals(GOALS_RATIO_DOMAIN)){
            String finalExtension2 = extension;
            collectionDesired.apply("Writing " +  type, FileIO.<String, EPLShotsRatio>writeDynamic().by(e1 -> e1.getSeason()).
                    withDestinationCoder(StringUtf8Coder.of()).via(Contextful.fn(e2 -> e2.toString()), TextIO.sink()).
                    withNaming(e2 -> FileIO.Write.defaultNaming(e2, extension)).to(output+type+"/"));
        }

    }


}
