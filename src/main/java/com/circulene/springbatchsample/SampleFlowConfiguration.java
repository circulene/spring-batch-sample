package com.circulene.springbatchsample;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SampleFlowConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Flow sampleFlow(Step step1, Step step2) {
        Flow flow = new FlowBuilder<Flow>("sampleFlow")
            .from(step1)
            .next(step2)
            .build();

        return flow;
    }

    @Bean
    public Step step1(@Value("${sample.data}") String data) {
        return stepBuilderFactory.get("step1")
            .tasklet(new Tasklet(){
            
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    System.out.println("Hey, step1! Data is " + data);
                    return RepeatStatus.FINISHED;
                }
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet(new Tasklet() {

				@Override
				public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
					System.out.println("Hey, step2!");
					return RepeatStatus.FINISHED;
				}
                
            })
            .build();
    }
}