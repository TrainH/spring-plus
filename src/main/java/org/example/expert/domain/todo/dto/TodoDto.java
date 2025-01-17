package org.example.expert.domain.todo.dto;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
public class TodoDto {
    public record SearchRequest(String title,
                                LocalDateTime fromDate,
                                LocalDateTime toDate,
                                String nickname
                                ) {
    }

    public record SearchResponse(Long id,
                                 String title,
                                 LocalDateTime createdAt,
                                 int commentCount) {
        public SearchResponse(Todo todo, long commentCount) {
            this(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getCreatedAt(),
                    (int) commentCount
            );
        }
    }
}
