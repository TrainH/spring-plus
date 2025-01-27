package org.example.expert.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.UserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<UserDto.SearchResponse> searchUsers(UserDto.SearchRequest request,
                                                    Pageable pageable) {
        return queryFactory.select(
                Projections.constructor(
                        UserDto.SearchResponse.class,
                        user
                )
        ).from(user)
                .where(
                        eqNickname(request.nickname())
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    private BooleanExpression eqNickname(String nickname) {
        if (nickname == null){
            return null;
        }
        return user.nickname.eq(nickname);
    }

}
