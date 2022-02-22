package example.pack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@Component
public class TestService implements CommandLineRunner {
    @Override
    public void run(String... args) throws IOException, InterruptedException {
        String command;

        switch (args[0]){
            case "sequential read":
                command =  "fio -filename=" + args[1] + " -direct=1 -iodepth " + args[2] +" -thread -rw=read -ioengine=libaio -bs=" + args[3] + " -size=10G -numjobs=" + args[4] + " -runtime=" + args[5] + " -group_reporting -name=r_" + args[3];
                break;
            case "sequential write":
                command =  "fio -filename=" + args[1] + " -direct=1 -iodepth " + args[2] +" -thread -rw=write -ioengine=libaio -bs=" + args[3] + " -size=10G -numjobs=" + args[4] + " -runtime=" + args[5] + " -group_reporting -name=w_" + args[3];;
                break;
            case "random read":
                command =  "fio -filename=" + args[1] + " -direct=1 -iodepth " + args[2] +" -thread -rw=randread -ioengine=libaio -bs=" + args[3] + " -size=10G -numjobs=" + args[4] + " -runtime=" + args[5] + " -group_reporting -name=randr_" + args[3];;
                break;
            case "radom write":
                command =  "fio -filename=" + args[1] + " -direct=1 -iodepth " + args[2] +" -thread -rw=randwrite -ioengine=libaio -bs=" + args[3] + " -size=10G -numjobs=" + args[4] + " -runtime=" + args[5] + " -group_reporting -name=randw_" + args[3];;
                break;
            default:
                command="";
                System.out.println("Parameter errors");
                System.exit(0);
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
            System.out.println("failed to execute");
            System.exit(1);
        }
        System.out.println(sb.toString());
        System.out.println("successfully");
        System.exit(0);
    }
}
