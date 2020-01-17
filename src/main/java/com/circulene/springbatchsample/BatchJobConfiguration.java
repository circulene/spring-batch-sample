package com.circulene.springbatchsample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchJobConfiguration {
    
    private final JobBuilderFactory jobBuilderFactory;

    private final Flow sampleFlow;

    @Bean
    public Job sampleJob() {
        return jobBuilderFactory.get("sampleJob")
            .start(sampleFlow)
            .end()
            .build();
    }
}