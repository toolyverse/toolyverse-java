package toolyverse.io.toolyverse.infrastructure.usecase;

public interface UseCase<I, O> {

    O execute(I input);
}
