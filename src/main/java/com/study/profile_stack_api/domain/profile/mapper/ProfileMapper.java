package com.study.profile_stack_api.domain.profile.mapper;

import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    // ProfileRequest → Profile 변환
    Profile toEntity(ProfileCreateRequest request);

    // ProfileUpdateRequest -> Profile 업데이트
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "memberId", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void updateEntity(ProfileUpdateRequest request, @MappingTarget Profile profile);

    // 리스트 매핑
    List<ProfileResponse> toResponseList(List<Profile> profiles);

    // Profile → ProfileResponse 변환
    ProfileResponse toResponse(Profile profile);
}
