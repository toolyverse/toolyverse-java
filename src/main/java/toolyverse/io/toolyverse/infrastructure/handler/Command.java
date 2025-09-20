package toolyverse.io.toolyverse.infrastructure.handler;

public interface Command {
    void execute();

    default void execute(Object input) {
        execute();
    }
}

