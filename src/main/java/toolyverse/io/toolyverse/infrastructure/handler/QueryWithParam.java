package toolyverse.io.toolyverse.infrastructure.handler;

public interface QueryWithParam<I, O> {

    O execute(I input);
}
