package com.isxcode.acorn.template.controller;

import com.isxcode.acorn.common.menu.TemplateType;
import com.isxcode.acorn.common.pojo.model.AcornModel1;
import com.isxcode.acorn.common.response.AcornResponse;
import com.isxcode.acorn.common.template.AcornTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
public class TestController {

    private final AcornTemplate acornTemplate;

    public TestController(AcornTemplate acornTemplate) {

        this.acornTemplate = acornTemplate;
    }

    @GetMapping("/execute")
    public void execute() {

        AcornModel1 acornModel1 = AcornModel1.builder()
            .jobName("ispong-test-flink-job")
            .executeId("1314520")
            .fromConnectorSql("CREATE TABLE from_table ( " +
                "  username STRING," +
                "  age INT " +
                ") WITH ( " +
                "  'scan.startup.mode'='latest-offset'," +
                "  'properties.group.id'='test-consumer-group'," +
                "  'connector'='kafka'," +
                "  'topic'='ispong_3'," +
                "  'properties.zookeeper.connect'='xxx.xxx.xxx.xxx:xxx'," +
                "  'properties.bootstrap.servers'='xxx.xxx.xxx.xxx:xxx'," +
                "  'format'='csv'," +
                "  'csv.ignore-parse-errors' = 'true')")
            .toConnectorSql("CREATE TABLE to_table ( " +
                "  username varchar(100)," +
                "  age int" +
                ") WITH (" +
                "  'connector'='jdbc'," +
                "  'url'='jdbc:mysql://xxx.xxx.xxx.xxx:xxx/xxx'," +
                "  'table-name'='ispong_flink_table'," +
                "  'driver'='com.mysql.cj.jdbc.Driver'," +
                "  'username'='xxx'," +
                "  'password'='xxx')")
            .filterCode("fromData = fromData.select($(\"username\").as(\"username\"),$(\"age\").as(\"age\"));")
            .templateName(TemplateType.KAFKA_INPUT_MYSQL_OUTPUT)
            .build();

        log.info(acornTemplate.build().execute(acornModel1).toString());
    }

    @GetMapping("/getLog")
    public void getLog() {

        AcornResponse flinkLog = acornTemplate.build().getJobLog("f992fe1e3d554ba4a9b214a07012fc97");
        log.info(flinkLog.toString());
    }

    @GetMapping("/getJobStatus")
    public void getJobStatus(@RequestParam String jobId) {

        AcornResponse jobInfo = acornTemplate.build().getJobStatus(jobId);
        log.info(jobInfo.toString());
    }

    @GetMapping("/queryJobStatus")
    public void queryJobStatus() {

        AcornResponse jobInfo = acornTemplate.build().queryJobStatus();
        log.info(jobInfo.toString());
    }

    @GetMapping("/stopJob")
    public void stop(@RequestParam String jobId) {

        AcornResponse d = acornTemplate.build().stopJob(jobId);
        log.info(d.toString());
    }
}
