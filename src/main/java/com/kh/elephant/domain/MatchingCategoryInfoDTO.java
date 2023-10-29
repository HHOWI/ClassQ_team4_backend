package com.kh.elephant.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MatchingCategoryInfoDTO {

    private PostDTO postDTO;

    private List<MatchingCategoryInfo> matchingCategories;
}
