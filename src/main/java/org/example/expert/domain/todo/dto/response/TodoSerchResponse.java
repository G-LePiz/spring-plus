package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSerchResponse {
    private final Long id;
    private final String title;
    private final int manager_count;
    private final int comment_count;

    public TodoSerchResponse(Long id, String title, int manager_count, int comment_count) {
        this.id = id;
        this.title = title;
        this.manager_count = manager_count;
        this.comment_count = comment_count;
    }
}
