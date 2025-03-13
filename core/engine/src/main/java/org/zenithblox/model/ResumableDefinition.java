/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.model;

import org.zenithblox.resume.ResumeStrategy;
import org.zenithblox.resume.ResumeStrategyConfiguration;
import org.zenithblox.resume.ResumeStrategyConfigurationBuilder;
import org.zenithblox.spi.Metadata;

/**
 * Resume EIP to support resuming processing from last known offset.
 */
@Metadata(label = "eip,routing")
public class ResumableDefinition extends NoOutputDefinition<ResumableDefinition> {

    private ResumeStrategy resumeStrategyBean;
    private ResumeStrategyConfiguration resumeStrategyConfiguration;

    @Metadata(required = true, javaType = "org.zenithblox.resume.ResumeStrategy")
    private String resumeStrategy;
    @Metadata(label = "advanced", javaType = "org.zenithblox.LoggingLevel", defaultValue = "ERROR",
              enums = "TRACE,DEBUG,INFO,WARN,ERROR,OFF")
    private String loggingLevel;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String intermittent;

    public ResumableDefinition() {
    }

    protected ResumableDefinition(ResumableDefinition source) {
        super(source);
        this.resumeStrategyBean = source.resumeStrategyBean;
        this.resumeStrategyConfiguration = source.resumeStrategyConfiguration;
        this.resumeStrategy = source.resumeStrategy;
        this.loggingLevel = source.loggingLevel;
        this.intermittent = source.intermittent;
    }

    @Override
    public ResumableDefinition copyDefinition() {
        return new ResumableDefinition(this);
    }

    @Override
    public String getShortName() {
        return "resumable";
    }

    @Override
    public String getLabel() {
        return "resumable";
    }

    public ResumeStrategy getResumeStrategyBean() {
        return resumeStrategyBean;
    }

    public String getResumeStrategy() {
        return resumeStrategy;
    }

    public void setResumeStrategy(String resumeStrategy) {
        this.resumeStrategy = resumeStrategy;
    }

    public void setResumeStrategy(ResumeStrategy resumeStrategyBean) {
        this.resumeStrategyBean = resumeStrategyBean;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevelRef) {
        this.loggingLevel = loggingLevelRef;
    }

    public String getIntermittent() {
        return intermittent;
    }

    public void setIntermittent(String intermitent) {
        this.intermittent = intermitent;
    }

    public ResumeStrategyConfiguration getResumeStrategyConfiguration() {
        return resumeStrategyConfiguration;
    }

    public void setResumeStrategyConfiguration(ResumeStrategyConfiguration resumeStrategyConfiguration) {
        this.resumeStrategyConfiguration = resumeStrategyConfiguration;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Sets the resume strategy to use
     */
    public ResumableDefinition resumeStrategy(String resumeStrategyRef) {
        setResumeStrategy(resumeStrategyRef);
        return this;
    }

    /**
     * Sets the resume strategy to use
     */
    public ResumableDefinition resumeStrategy(String resumeStrategyRef, String loggingLevelRef) {
        setResumeStrategy(resumeStrategyRef);
        setLoggingLevel(loggingLevelRef);
        return this;
    }

    /**
     * Sets the resume strategy to use
     */
    public ResumableDefinition resumeStrategy(ResumeStrategy resumeStrategy) {
        setResumeStrategy(resumeStrategy);
        return this;
    }

    /**
     * Sets the resume strategy to use
     */
    public ResumableDefinition resumeStrategy(ResumeStrategy resumeStrategy, String loggingLevelRef) {
        setResumeStrategy(resumeStrategy);
        setLoggingLevel(loggingLevelRef);
        return this;
    }

    /***
     * Uses a configuration builder to auto-instantiate the resume strategy
     */
    public ResumableDefinition configuration(
            ResumeStrategyConfigurationBuilder<? extends ResumeStrategyConfigurationBuilder, ? extends ResumeStrategyConfiguration> builder) {
        setResumeStrategyConfiguration(builder.build());
        return this;
    }

    /**
     * Sets whether the offsets will be intermittently present or whether they must be present in every exchange
     */
    public ResumableDefinition intermittent(boolean intermittent) {
        setIntermittent(Boolean.toString(intermittent));

        return this;
    }
}
