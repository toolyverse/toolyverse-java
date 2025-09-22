package toolyverse.io.toolyverse.infrastructure.handler;

public interface CommandWithParam<I> {

   void execute(I input);
}
