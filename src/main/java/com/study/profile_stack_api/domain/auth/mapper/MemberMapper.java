package com.study.profile_stack_api.domain.auth.mapper;

import com.study.profile_stack_api.domain.auth.dto.request.SignupRequest;
import com.study.profile_stack_api.domain.auth.dto.response.LoginResponse;
import com.study.profile_stack_api.domain.auth.dto.response.SignupResponse;
import com.study.profile_stack_api.domain.auth.entity.Member;
import com.study.profile_stack_api.domain.auth.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", imports = Role.class)
public interface MemberMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "username", source = "request.username"),
            @Mapping(target = "password", source = "encodedPassword"),
            @Mapping(target = "role", expression = "java(Role.USER)"),
            @Mapping(target = "createdAt", ignore = true)
    })
    Member toMemberEntity(SignupRequest request, String encodedPassword);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "username", source = "username")
    })
    SignupResponse toSignupResponse(Member member);


    // 로그인
    @Mappings({
            @Mapping(target="accessToken", source = "accessToken"),
            @Mapping(target="refreshToken", source = "refreshToken")
    })
    LoginResponse toLoginResponse(String accessToken, String refreshToken);
}