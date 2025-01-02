package com.nft.content.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.content.domain.Carousel;

public interface CarouselRepo extends JpaRepository<Carousel, String>, JpaSpecificationExecutor<Carousel> {

	List<Carousel> findByDeletedFlagIsFalseOrderByOrderNo();

	Carousel findTopByDeletedFlagIsFalseOrderByOrderNoDesc();

}
