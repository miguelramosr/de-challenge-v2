package com.personal.epl.fn;

import com.google.api.services.bigquery.model.TableSchema;
import com.personal.epl.domain.EPLSeasonDomain;
import com.personal.epl.domain.EPLSeasonHighestScorers;
import com.personal.epl.domain.EPLShotsRatio;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ShowResultsFn<t> extends DoFn<t, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowResultsFn.class);

    @StartBundle
    public void startBundle() {
    }

    @ProcessElement
    public void processElement(ProcessContext context) throws IOException {
        Object element = null;
        if(context.element() instanceof EPLSeasonDomain){
             element = (EPLSeasonDomain) context.element();
        }
        else if (context.element() instanceof EPLSeasonHighestScorers){
             element = (EPLSeasonHighestScorers) context.element();
        }
        else if (context.element() instanceof EPLShotsRatio){
             element = (EPLShotsRatio) context.element();
        }
        context.output(element.toString());
    }


    @FinishBundle
    public void finishBundle(FinishBundleContext c) throws IOException { // NOSONAR
    }

}
