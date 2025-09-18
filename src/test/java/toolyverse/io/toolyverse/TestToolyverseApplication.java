package toolyverse.io.toolyverse;

import org.springframework.boot.SpringApplication;

public class TestToolyverseApplication {

    public static void main(String[] args) {
        SpringApplication.from(ToolyverseApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
