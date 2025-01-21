package org.example.expert.domain.user.dto;

import org.example.expert.domain.user.entity.User;

public class UserDto {
    public record SearchRequest(String nickname) {

    }

    public record SearchResponse(Long id,
                                 String email,
                                 String nickname) {
        public SearchResponse(User user) {
            this(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname()
            );
        }
    }
}
