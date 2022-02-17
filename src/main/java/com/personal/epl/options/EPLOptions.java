package com.personal.epl.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.ValueProvider;

public interface EPLOptions  extends PipelineOptions {
    @Description("Path of the file to read from")
    @Default.String("gs://test-base-bucket-personal/challenge/input/*")
    ValueProvider<String> getDataInput();

    @Description("Path of the file to write the results to")
    @Default.String("gs://test-base-bucket-personal/challenge/output/")
    ValueProvider<String> getOutputDir();

    @Description("File extension")
    @Default.String("json")
    ValueProvider<String> getFileExtension();

    void setDataInput(ValueProvider<String> dataInput);

    void setOutputDir(ValueProvider<String> outputDir);

    void setFileExtension(ValueProvider<String> fileExtension);
}
