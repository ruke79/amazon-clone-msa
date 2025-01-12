package com.challenge.chat.domain.member.entity;

import java.util.List;

import com.challenge.chat.domain.chat.entity.MemberChatRoom;
import com.challenge.chat.domain.member.constant.MemberRole;
import com.challenge.chat.domain.member.constant.SocialType;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Member {

	@Id
	@Column(name = "member_id")
	private Long id;

	private String email; // 이메일
	private String password; // 비밀번호
	private String nickname; // 닉네임
	private String imageUrl; // 프로필 이미지

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	//@Enumerated(EnumType.STRING)
	//private SocialType socialType; // KAKAO, NAVER, GOOGLE

	//private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)


	@OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<MemberChatRoom> roomList;



}
