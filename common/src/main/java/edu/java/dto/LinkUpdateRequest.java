package edu.java.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(

    Long id,

    @NotNull
    URI url,

    @NotBlank
    String description,

    List<Long> tgChatIds
) {
}
