package example.pack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;



@Component
public class TestService implements CommandLineRunner {

    @Value("${depth}")
    private int depth;
    @Value("${number}")
    private int number;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        String testDir = "test_" + System.currentTimeMillis();
        String command = "mkdir " + testDir;
        System.out.println(command);
        System.out.println("create test directory " + testDir + " and start to test......");
        Process exec = Runtime.getRuntime().exec(command);
        exec.waitFor();
        if(exec.exitValue() != 0){
            throw new Exception("failed to mkdir " + testDir);
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
                throw new Exception("failed to mkdir "+ tempDir);
            }
        }
        long end = System.currentTimeMillis();

        command = "rm -rf " + testDir;
        exec = Runtime.getRuntime().exec(command);
        exec.waitFor();
        if(exec.exitValue() != 0){
            throw new Exception("failed to remove " + testDir);
        }
        System.out.println("successfully, directory number is " + number + ", directory depth is " + depth + " The time spent is " + (end - start) + "ms");
        context.close();
    }
}
