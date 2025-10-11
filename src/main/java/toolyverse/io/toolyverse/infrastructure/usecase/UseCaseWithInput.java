package toolyverse.io.toolyverse.infrastructure.usecase;

public interface UseCaseWithInput<I> {

    void execute(I input);
}
