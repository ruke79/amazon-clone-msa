package com.project.chatserver.model;

import java.util.ArrayList;
import java.util.List;

import com.project.chatserver.constants.MemberRole;

import lombok.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name="member")
public class ChatMember {

	@Id
	@Column(name = "member_id")
	private Long id;

	private String email; // 이메일
	//private String password; // 비밀번호
	private String nickname; // 닉네임
	private String imageUrl; // 프로필 이미지

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	//@Enumerated(EnumType.STRING)
	//private SocialType socialType; // KAKAO, NAVER, GOOGLE

	//private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)


	@OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ParticipantChatRoom> roomList =  new ArrayList<ParticipantChatRoom>();



}
