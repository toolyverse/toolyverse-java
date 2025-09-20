package toolyverse.io.toolyverse.infrastructure.handler;

public interface CommandWithParam<I, O> {

    O execute(I input);
}
