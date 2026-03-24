package com.study.profile_stack_api.domain.profile.mapper;

import com.study.profile_stack_api.domain.profile.dto.request.ProfileCreateRequest;
import com.study.profile_stack_api.domain.profile.dto.request.ProfileUpdateRequest;
import com.study.profile_stack_api.domain.profile.dto.response.ProfileResponse;
import com.study.profile_stack_api.domain.profile.entity.Position;
import com.study.profile_stack_api.domain.profile.entity.Profile;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    // ProfileRequest → Profile 변환
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "bio", expression = "java(nullToEmpty(request.getBio()))"),
            @Mapping(target = "githubUrl", expression = "java(nullToEmpty(request.getGithubUrl()))"),
            @Mapping(target = "blogUrl", expression = "java(nullToEmpty(request.getBlogUrl()))")
    })
    Profile toEntity(ProfileCreateRequest request);

    // ProfileUpdateRequest -> Profile 업데이트
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "memberId", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    })
    void updateEntity(ProfileUpdateRequest request, @MappingTarget Profile profile);

    // 리스트 매핑
    List<ProfileResponse> toResponseList(List<Profile> profiles);

    // Profile → ProfileResponse 변환
    @Mappings({
            @Mapping(target = "position", source = "position", qualifiedByName = "positionToDescription"),
            @Mapping(target = "positionIcon", source = "position", qualifiedByName = "positionToIcon")
    })
    ProfileResponse toResponse(Profile profile);

    // util
    default String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    @Named("positionToDescription")
    default String positionToDescription(Position position) {
        return position == null ? null : position.getDescription();
    }

    @Named("positionToIcon")
    default String positionToIcon(Position position) {
        return position == null ? null : position.getIcon();
    }

    default Position map(String position) {
        if (position == null) {
            return null;
        }

        try {
            return Position.valueOf(position);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("허용되지 않은 position 값입니다. 가능한 값: BACKEND, FRONTEND, FULLSTACK, MOBILE, DEVOPS, DATA, AI, ETC");
        }
    }
}
