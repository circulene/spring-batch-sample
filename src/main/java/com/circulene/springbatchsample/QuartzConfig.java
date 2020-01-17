package com.circulene.springbatchsample;

import java.io.IOException;
import java.util.Properties;
import java.util.TimeZone;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import lombok.RequiredArgsConstructor;

/**
 * @see https://howtodoinjava.com/spring-batch/batch-quartz-java-config-example/
 */
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final JobLauncher jobLauncher;

    private final JobLocator jobLocator;

    @Value("${cron.schedule:0 0 1 * * ? *}")
    private String cronSchedule;

    @Value("${cron.schedule.timezone:GMT}")
    private String cronScheduleTimezone;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean
    public JobDetail jobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "sampleJob");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(QuartzJob.class)
                .withIdentity("sampleJob")
                .setJobData(jobDataMap)
                .storeDurably() // remain job data
                .build();
    }

    @Bean
    public Trigger jobTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronSchedule)
                .inTimeZone(TimeZone.getTimeZone(cronScheduleTimezone));

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity("jobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(jobTrigger());
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setJobDetails(jobDetail());
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean properties = new PropertiesFactoryBean();
        properties.setLocation(new ClassPathResource("/quartz.properties"));
        properties.afterPropertiesSet();
        return properties.getObject();
    }
}