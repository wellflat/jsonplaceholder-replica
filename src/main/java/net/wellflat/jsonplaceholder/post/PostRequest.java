package net.wellflat.jsonplaceholder.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequest(
    @NotNull(message = "userIdは必須です")
    Long userId,

    @NotBlank(message = "titleは必須です")
    @Size(max = 200, message = "titleは200文字以内で入力してください")
    String title,

    @NotBlank(message = "bodyは必須です")
    String body
) {}
