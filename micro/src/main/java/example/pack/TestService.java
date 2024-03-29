package example.pack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    @Value("${file.size}")
    String fs;

    @Value("${num.jobs}")
    String numJobs;

    @Value("${runtime}")
    int runtime;

    @Value("${environment}")
    String environment;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        String command;

        switch (operation){
            case "sequential_read":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=read -ioengine=libaio -bs=" + batchSize + "k -size=" + fs +"G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=r_" + batchSize;
                break;
            case "sequential_write":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=write -ioengine=libaio -bs=" + batchSize + "k -size=" + fs +"G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=w_" + batchSize;;
                break;
            case "random_read":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=randread -ioengine=libaio -bs=" + batchSize + "k -size=" + fs +"G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=randr_" + batchSize;;
                break;
            case "random_write":
                command =  "fio -filename=" + filename + " -direct=1 -iodepth " + depth +" -thread -rw=randwrite -ioengine=libaio -bs=" + batchSize + "k -size=" + fs +"G -numjobs=" + numJobs + " -runtime=" + runtime + " -group_reporting -name=randw_" + batchSize;;
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
        String out = sb.toString();
        System.out.println(out);

        Pattern p = Pattern.compile("IOPS="+"[0-9]{1,}");
        Matcher m = p.matcher(out);
        String iops = "";
        if(m.find()){
            iops = m.group();
            int indexOf = iops.indexOf("IOPS");
            iops = iops.substring(indexOf + 5);
        }

        p = Pattern.compile("BW="+"[0-9]{1,}");
        m = p.matcher(out);
        String bw = "";
        if(m.find()){
            bw = m.group();
            int indexOf = bw.indexOf("BW=");
            bw = bw.substring(indexOf + 3);
        }

        String sql = "insert into microtest values('" + operation + "', '" + environment + "', " + iops + ", " + bw + ", '" + filename + "', " + depth + ", " + batchSize + ", " + fs + ", " + numJobs + ", " + runtime + ", now());";
        System.out.println(sql);
        jdbcTemplate.execute(sql);
        System.out.println("successfully");
        context.close();
    }
}
