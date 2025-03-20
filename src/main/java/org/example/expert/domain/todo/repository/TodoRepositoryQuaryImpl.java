package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSerchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.querydsl.core.util.MathUtils.result;
import static org.example.expert.domain.todo.entity.QTodo.todo;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryQuaryImpl implements TodoRepositoryQuary{

    QTodo qTodo = todo;

    final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory.select(qTodo)
                .from(qTodo)
                .leftJoin(qTodo.user).fetchJoin() // n+1 문제 해결
                .where(qTodo.id.eq(todoId)) // eq => equal
                .fetchOne() // 1개를 조회, 이 데이터를 적용하겠다.
        );
    }

    @Override
    public Page<TodoSerchResponse> findAll(long todoId, String todoTitle, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable) {
        List<TodoSerchResponse> result = jpaQueryFactory.select(Projections.constructor(TodoSerchResponse.class,
                qTodo.id, qTodo.title, qTodo.managers.size().as("manager_count"), qTodo.comments.size().as("comment_count")))
                .from(qTodo)
                .where(todoTitle(todoTitle), datestarts(startDate), dateEnd(endDate), findnickname(nickname))
                .orderBy(qTodo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalsize = Optional.ofNullable(jpaQueryFactory.select(Wildcard.count) // wildcard.count -> count(*)
                .from(qTodo)
                .where(todoTitle(todoTitle), datestarts(startDate), dateEnd(endDate), findnickname(nickname))
                .fetchOne()).orElse(0L);
        
        return PageableExecutionUtils.getPage(result, pageable, () -> totalsize);
    }

    private BooleanExpression datestarts(LocalDate startDate) {
        return Objects.nonNull(startDate) ? todo.createdAt.after(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression dateEnd(LocalDate endDate) {
        return Objects.nonNull(endDate) ? todo.createdAt.before(endDate.atTime(23,59,59,59)) : null;
    }

    private BooleanExpression todoTitle(String todotitle) {
        return Objects.nonNull(todotitle) ? todo.title.contains(todotitle) : null;
    }

    private BooleanExpression findnickname(String nickname) {
        return Objects.nonNull(nickname) ? todo.user.nickname.contains(nickname) : null;
    }


}
