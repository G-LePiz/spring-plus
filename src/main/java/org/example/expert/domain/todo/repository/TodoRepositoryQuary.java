package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSerchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoRepositoryQuary {

    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    Page<TodoSerchResponse> findAll(long todoId, String todoTitle, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable);
}
