package com.woowahan.recipe.repository;

import com.woowahan.recipe.domain.entity.RecipeEntity;
import com.woowahan.recipe.domain.entity.ReviewEntity;
import com.woowahan.recipe.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    Page<ReviewEntity> findAllByRecipe(RecipeEntity recipe, Pageable pageable);
    Page<ReviewEntity> findAllByUser(UserEntity user, Pageable pageable);
}
