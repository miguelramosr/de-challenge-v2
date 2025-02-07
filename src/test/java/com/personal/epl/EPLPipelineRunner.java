package com.personal.epl;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.TestPipeline;
import org.junit.Test;

public class EPLPipelineRunner
{

    private String tempLocation = "";
    private String fileExtension = "";
    private String dataInput = "";
    private String outputDir = "";

    public static void main(String[] args) {
        Pipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);
        String[] pipelineOptions = { "--dataInput=src/main/resources/data/*" };
        EPLPipeline.main(pipelineOptions);
        p.run().waitUntilFinish();
    }
}
