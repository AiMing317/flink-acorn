package com.isxcode.acorn.deploy;

import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

@RequestMapping
@RestController
@SpringBootApplication
public class DeployApplication {

	public static void main(String[] args) {
        SpringApplication.run(DeployApplication.class, args);
	}

    @GetMapping("/demo")
    public String demo() {

        System.out.println("hello world");
        return "hello world";
    }

    @GetMapping("/deploy")
    public String deploy() throws IOException, YarnException, ClusterDeploymentException {

        // 初始化yarnClient
        YarnConfiguration yarnConfig = new YarnConfiguration();
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConfig);
        yarnClient.start();

        // 配置容器资源
        YarnClientApplication application = yarnClient.createApplication();
        ApplicationSubmissionContext appContext = application.getApplicationSubmissionContext();
        appContext.setApplicationName("spring-demo");
        appContext.setResource(Resource.newInstance(1024, 1));
        appContext.setPriority(Priority.newInstance(0));
        appContext.setQueue("default");
        appContext.setApplicationType("Flink Sql Job");

        // 初始化flinkConfig
        Configuration flinkConfig = new Configuration();
        flinkConfig.setString("yarn.application.name", "spring-demo");

        // 配置flink yarn job环境
        YarnClusterDescriptor descriptor = new YarnClusterDescriptor(
            flinkConfig,
            yarnConfig,
            yarnClient,
            YarnClientYarnClusterInformationRetriever.create(yarnClient),
            false);
        descriptor.addShipFiles(Collections.singletonList(new File("/home/ispong/flink-acorn/demos/sql-job/target/sql-job-0.0.1.jar")));

        // 配置yarn job的资源分配
        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder()
            .setMasterMemoryMB(1024)
            .setTaskManagerMemoryMB(1024)
            .setSlotsPerTaskManager(1)
            .createClusterSpecification();

        // 部署作业
        ClusterClientProvider<ApplicationId> provider = descriptor.deployJobCluster(clusterSpecification, new JobGraph("flink job"), true);

        // 打印应用信息
        System.out.println("applicationId:" + provider.getClusterClient().getClusterId().toString());

        return "deploy success";
    }

}
