package toolyverse.io.toolyverse.infrastructure.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageableResponse<U> {
    private List<U> content;
    private PageDetails pageable;

    @Data
    @Builder
    public static class PageDetails {
        private long totalElements;
        private int numberOfElements;
        private int totalPages;
        private boolean last;
        private boolean first;
        private boolean empty;
    }

}
