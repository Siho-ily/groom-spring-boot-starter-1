package com.study.profile_stack_api.domain.techstack.mapper;

import com.study.profile_stack_api.domain.techstack.dto.request.TechStackCreateRequest;
import com.study.profile_stack_api.domain.techstack.dto.request.TechStackUpdateRequest;
import com.study.profile_stack_api.domain.techstack.dto.response.TechStackResponse;
import com.study.profile_stack_api.domain.techstack.entity.TechStack;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TechStackMapper {
    // DTO -> Entity
    TechStack toEntity(TechStackCreateRequest techStackCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "profileId", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void updateEntity(TechStackUpdateRequest techStackUpdateRequest, @MappingTarget TechStack techStack);


    // Entity -> DTD
    TechStackResponse toResponse(TechStack techStack);
    List<TechStackResponse> toResponseList(List<TechStack> techStackList);
}
