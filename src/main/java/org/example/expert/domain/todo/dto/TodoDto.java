package org.example.expert.domain.todo.dto;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
public class TodoDto {
    public record SearchRequest(String title,
                                LocalDateTime fromDate,
                                LocalDateTime toDate,
                                String managerName
                                ) {
    }

    public record SearchResponse(Long id,
                                 String title,
                                 LocalDateTime createdAt,
                                 int commentCount,
                                 int managerCount) {
        public SearchResponse(Todo todo, long commentCount, long managerCount) {
            this(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getCreatedAt(),
                    (int) commentCount,
                    (int) managerCount
            );
        }
    }
}
