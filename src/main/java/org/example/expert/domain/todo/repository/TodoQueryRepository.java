package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.TodoDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;


@Repository
@RequiredArgsConstructor
public class TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<Todo> findByIdWithUser(long todoId) {
        Todo result = queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<TodoDto.SearchResponse> searchTodos(TodoDto.SearchRequest request, Pageable pageable) {
        return queryFactory.select(
                Projections.constructor(
                        TodoDto.SearchResponse.class,
                        todo,
                        select(Wildcard.count)
                                .from(comment)
                                .where(todo.id.eq(comment.todo.id)
                                ),
                        select(Wildcard.count)
                                .from(manager)
                                .where(manager.todo.id.eq(todo.id))
                )

        ).from(todo)
                .where(
                        containsTitle(request.title()),
                        goeFromDate(request.fromDate()),
                        loeToDate(request.toDate()),
                        containsManagerName(request.managerName())
                )
                .orderBy(getSortOrders(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

    }


    private BooleanExpression eqTitle(String title) {
        if (title == null) {
            return null;
        }
        return todo.title.eq(title);
    }

    private BooleanExpression containsTitle(String title) {
        if (title == null || title.isEmpty()) {
            return null;
        }
        return todo.title.containsIgnoreCase(title);
    }

    private BooleanExpression goeFromDate(LocalDateTime fromDate) {
        if (fromDate == null) {
            return null;
        }
        return todo.createdAt.goe(fromDate);
    }

    private BooleanExpression loeToDate(LocalDateTime toDate) {
        if (toDate == null) {
            return null;
        }
        return todo.createdAt.loe(toDate);
    }

    private BooleanExpression containsManagerName(String managerName) {
        if (managerName == null || managerName.isEmpty()) {
            return null;
        }
        return user.nickname.containsIgnoreCase(managerName);
    }

    private OrderSpecifier<?>[] getSortOrders(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            return new OrderSpecifier[]{
                    todo.id.desc()
            };
        }


        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            boolean isAscending = order.isAscending();
            OrderSpecifier<?> orderSpecifier = switch (property) {
                case "title" -> isAscending ? todo.title.asc() : todo.title.desc();
                case "createdAt" -> isAscending ? todo.createdAt.asc() : todo.createdAt.desc();
                case "id" -> isAscending ? todo.id.asc() : todo.id.desc();
                default -> todo.id.asc(); // 기본 정렬 조건
            };
            orders.add(orderSpecifier);
        }
        return orders.toArray(new OrderSpecifier[]{});
    }

    public Long getTotalCount(TodoDto.SearchRequest request) {
        return Optional.ofNullable(
                        queryFactory.select(Wildcard.count)
                                .from(todo)
                                .where(
                                        containsTitle(request.title()),
                                        goeFromDate(request.fromDate()),
                                        loeToDate(request.toDate()),
                                        containsManagerName(request.managerName())
                                )
                                .fetchOne())
                .orElse(0L);
    }
}
