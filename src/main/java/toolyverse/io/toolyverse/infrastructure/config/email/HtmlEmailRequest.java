package toolyverse.io.toolyverse.infrastructure.config.email;

public record HtmlEmailRequest(String subject, String to, String templateName) {
}
