package com.study.profile_stack_api.domain.auth.mapper;

import com.study.profile_stack_api.domain.auth.dto.response.TokenRefreshResponse;
import com.study.profile_stack_api.domain.auth.entity.Member;
import com.study.profile_stack_api.domain.auth.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "memberId", source = "member.id"),
            @Mapping(target = "token", source = "refreshToken"),
            @Mapping(target = "expiredDate", source = "expiredDate"),
            @Mapping(target = "createdAt", source = "createdAt")
    })
    RefreshToken toEntity(Member member, String refreshToken, LocalDateTime expiredDate, LocalDateTime createdAt);

    @Mappings({
            @Mapping(target="accessToken", source = "accessToken")
    })
    TokenRefreshResponse toResponse(String accessToken);
}
