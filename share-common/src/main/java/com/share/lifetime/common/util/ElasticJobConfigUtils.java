package com.share.lifetime.common.util;

import org.apache.commons.lang3.StringUtils;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.script.ScriptJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;

public class ElasticJobConfigUtils {

    /**
     * 
     * @param elasticJob
     *            DataflowJob<T>,ScriptJob,SimpleJob 实现类对象
     * @param regCenter
     *            分布式服务的注册中心(如：zookeeper)
     * @param jobName
     *            作业名称
     * @param cron
     *            作业启动时间的cron表达式
     * @param shardingTotalCount
     *            作业分片总数
     * @param shardingItemParameters
     *            设置分片序列号和个性化参数对照表. 分片序列号和参数用等号分隔, 多个键值对用逗号分隔. 类似map. 分片序列号从0开始, 不可大于或等于作业分片总数. 如: 0=a,1=b,2=c
     * @param jobExceptionHandler
     *            作业异常处理器(JobExceptionHandler)实现类；允许为null，则使用默认作业异常处理器
     * @param description
     *            作业描述信息
     * @param elasticJobListeners
     *            作业监听器接口(ElasticJobListener)实现类
     * @return
     */
    public static JobScheduler getSpringJobScheduler(final ElasticJob elasticJob,
        final CoordinatorRegistryCenter regCenter, final String jobName, final String cron,
        final int shardingTotalCount, final String shardingItemParameters, final String jobExceptionHandler,
        final String description, final ElasticJobListener... elasticJobListeners) {
        LiteJobConfiguration jobConfig = getLiteJobConfiguration(elasticJob, jobName, cron, shardingTotalCount,
            shardingItemParameters, jobExceptionHandler, description);
        return new SpringJobScheduler(elasticJob, regCenter, jobConfig, elasticJobListeners);
    }

    /**
     * 
     * @param elasticJob
     *            DataflowJob<T>,ScriptJob,SimpleJob 实现类对象
     * @param regCenter
     *            分布式服务的注册中心(如：zookeeper)
     * @param jobName
     *            作业名称
     * @param cron
     *            作业启动时间的cron表达式
     * @param shardingTotalCount
     *            作业分片总数
     * @param shardingItemParameters
     *            设置分片序列号和个性化参数对照表. 分片序列号和参数用等号分隔, 多个键值对用逗号分隔. 类似map. 分片序列号从0开始, 不可大于或等于作业分片总数. 如: 0=a,1=b,2=c
     * @param jobExceptionHandler
     *            作业异常处理器(JobExceptionHandler)实现类；允许为null，则使用默认作业异常处理器
     * @param description
     *            作业描述信息
     * @param jobEventConfig
     *            作业事件配置标识接口(JobEventConfiguration)实现类JobEventRdbConfiguration
     * @param elasticJobListeners
     *            作业监听器接口(ElasticJobListener)实现类
     * @return
     */
    public static JobScheduler getSpringJobScheduler(final ElasticJob elasticJob,
        final CoordinatorRegistryCenter regCenter, final String jobName, final String cron,
        final int shardingTotalCount, final String shardingItemParameters, final String jobExceptionHandler,
        final String description, final JobEventConfiguration jobEventConfig,
        final ElasticJobListener... elasticJobListeners) {
        LiteJobConfiguration jobConfig = getLiteJobConfiguration(elasticJob, jobName, cron, shardingTotalCount,
            shardingItemParameters, jobExceptionHandler, description);
        return new SpringJobScheduler(elasticJob, regCenter, jobConfig, jobEventConfig, elasticJobListeners);
    }

    private static LiteJobConfiguration getLiteJobConfiguration(final ElasticJob elasticJob, final String jobName,
        final String cron, final int shardingTotalCount, final String shardingItemParameters,
        final String jobExceptionHandler, final String description) {
        boolean defaultJobExceptionHandler = false;
        JobTypeConfiguration jobConfig = null;
        Class<? extends ElasticJob> elasticJobClass = elasticJob.getClass();
        String jobClass = elasticJobClass.getCanonicalName();
        if (StringUtils.isEmpty(jobExceptionHandler)) {
            defaultJobExceptionHandler = true;
            // DefaultJobExceptionHandler.class.getCanonicalName();
        }
        if (elasticJob instanceof DataflowJob) {

        } else if (elasticJob instanceof ScriptJob) {

        } else if (elasticJob instanceof SimpleJob) {
            JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
                .shardingItemParameters(shardingItemParameters)
                .jobProperties("job_exception_handler", (defaultJobExceptionHandler
                    ? DefaultJobExceptionHandler.class.getCanonicalName() : jobExceptionHandler))
                .description(description).build();
            jobConfig = new SimpleJobConfiguration(coreConfig, jobClass);
        }

        return LiteJobConfiguration.newBuilder(jobConfig).overwrite(true).build();
    }

}
