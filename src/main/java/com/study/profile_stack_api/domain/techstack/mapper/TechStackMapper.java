package com.study.profile_stack_api.domain.techstack.mapper;

import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackUpdateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.Proficiency;
import com.study.profile_stack_api.domain.techstack.entity.TechCategory;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TechStackMapper {
    // DTO -> Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "profileId", ignore = true),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    })
    TechStack toEntity(TechStackCreateRequest techStackCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "profileId", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    })
    void updateEntity(TechStackUpdateRequest techStackUpdateRequest, @MappingTarget TechStack techStack);


    // Entity -> DTD
    TechStackResponse toResponse(TechStack techStack);
    List<TechStackResponse> toResponseList(List<TechStack> techStackList);

    // util
    default String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    default TechCategory mapCategory(String category) {
        if (category == null) {
            return null;
        }

        try {
            return TechCategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("허용되지 않은 category 값입니다. 가능한 값: LANGUAGE, FRAMEWORK, DATABASE, DEVOPS, TOOL, ETC");
        }
    }

    default Proficiency mapProficiency(String proficiency) {
        if (proficiency == null) {
            return null;
        }

        try {
            return Proficiency.valueOf(proficiency);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("허용되지 않은 proficiency 값입니다. 가능한 값: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT");
        }
    }
}
