package example.pack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class TestService implements CommandLineRunner {

    @Value("${depth}")
    private int depth;
    @Value("${number}")
    private int number;

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        String testDir = "test_" + System.nanoTime();
        String command = "mkdir " + testDir;
        System.out.println(command);
        System.out.println("create test directory " + testDir + " and start to test......");
        Process exec = Runtime.getRuntime().exec(command);
        exec.waitFor();
        if(exec.exitValue() != 0){
            System.out.println("failed to mkdir " + testDir);
            System.exit(1);
        }
        long start = System.currentTimeMillis();
        for(int i = 0; i < number; i++){
            StringBuilder tempDir = new StringBuilder(testDir + "/sub_" + i);
            for(int j = 0; j < depth; j++){
                tempDir.append("/sub" + j);
            }
            command = "mkdir -p " + tempDir.toString();
            System.out.println(command);
            exec = Runtime.getRuntime().exec(command);
            exec.waitFor();
            if(exec.exitValue() != 0){
                System.out.println("failed to mkdir " + tempDir);
                System.exit(1);
            }
        }
        long end = System.currentTimeMillis();

        command = "rm -rf " + testDir;
        exec = Runtime.getRuntime().exec(command);
        exec.waitFor();
        if(exec.exitValue() != 0){
            System.out.println("failed to remove " + testDir);
            System.exit(1);
        }
        System.out.println("successfully, directory number is " + number + ", directory depth is " + depth + " The time spent is " + (end - start) + "ms");
        System.exit(0);
    }
}
