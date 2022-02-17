package com.personal.epl.options;

import org.apache.beam.sdk.options.PipelineOptions;

public interface EPLOptions  extends PipelineOptions {
    String getTempLocation();

    String getDataInput();

    String getOutputDir();

    String getFileExtension();

    void setTempLocation(String tempLocation);

    void setDataInput(String dataInput);

    void setOutputDir(String outputDir);

    void setFileExtension(String fileExtension);
}
