package com.ducdv38.springsecurity.repository;

import com.ducdv38.springsecurity.entity.InvalidateToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidateRepository  extends CrudRepository<InvalidateToken, String> {
}
