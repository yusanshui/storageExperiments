package example.pack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@Component
public class TestService implements CommandLineRunner {
    @Value("${operation}")
    String operation;

    @Value("${filename}")
    String filename;

    @Value("${depth}")
    String depth;

    @Value("${batch.size}")
    String batchSize;

    @Value("${num.jobs}")
    String numJobs;

    @Value("${runtime}")
    int runtime;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        String command;

        switch (operation){
            case "sequential_read":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=read -ioengine=libaio -bs=" + batchSize + " -size=10G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=r_" + batchSize;
                break;
            case "sequential_write":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=write -ioengine=libaio -bs=" + batchSize + " -size=10G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=w_" + batchSize;;
                break;
            case "random_read":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=randread -ioengine=libaio -bs=" + batchSize + " -size=10G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=randr_" + batchSize;;
                break;
            case "random_write":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=randwrite -ioengine=libaio -bs=" + batchSize + " -size=10G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=randw_" + batchSize;;
                break;
            default:
                throw new Exception("Parameter errors:"
                        + " operation=" + operation
                        + " filename=" + filename
                        + " depth=" + depth
                        + " batch.size=" + batchSize
                        + " num.jobs=" + numJobs
                        + " runtime=" + runtime
                );
        }
        System.out.println(command);
        System.out.println("start to test......");
        Process exec = Runtime.getRuntime().exec(command);
        InputStream inputStream = exec.getInputStream();
        InputStream stderr = exec.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        BufferedReader brerr = new BufferedReader(new InputStreamReader(stderr, StandardCharsets.UTF_8));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null){
            sb.append(line).append("\n");
        }
        while((line = brerr.readLine()) != null){
            sb.append(line).append("\n");
        }
        exec.waitFor();
        if(exec.exitValue() != 0){
            throw new Exception("failed to execute");
        }
        System.out.println(sb.toString());
        System.out.println("successfully");
        context.close();
    }
}
