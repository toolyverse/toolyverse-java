package toolyverse.io.toolyverse.infrastructure.handler;

public interface Query<O> {
    O execute();
}
