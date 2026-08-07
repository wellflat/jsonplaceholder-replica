package net.wellflat.jsonplaceholder.post;

import jakarta.validation.constraints.Size;

public record PostPatchRequest(
    Long userId,

    @Size(max = 200, message = "titleは200文字以内で入力してください")
    String title,

    String body
) {}
