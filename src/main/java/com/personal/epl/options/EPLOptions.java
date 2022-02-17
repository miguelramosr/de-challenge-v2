package com.personal.epl.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.ValueProvider;

public interface EPLOptions  extends PipelineOptions {

    @Description("Path of the file to read from")
    @Default.String("gs://test-base-bucket-personal/challenge/input/*")
    ValueProvider<String> getDataInput();

    void setDataInput(ValueProvider<String> fileExtension);
}
